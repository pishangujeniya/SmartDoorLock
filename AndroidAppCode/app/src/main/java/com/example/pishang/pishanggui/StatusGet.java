package com.example.pishang.pishanggui;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StatusGet extends AppCompatActivity {

    BluetoothSocket btSocket;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    InputStream mmInputStream;
    OutputStream mmOutStream;
    Globalshare mApp;
    ConnectedThread ct;
    ImageView iv;

//    com.dd.CircularProgressButton SG;
    Button SG;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;
            switch (msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    //Toast.makeText(getApplicationContext(),writeMessage,Toast.LENGTH_SHORT).show();
//                    SG.setProgress(100);
                    Snackbar.make(findViewById(android.R.id.content), writeMessage, Snackbar.LENGTH_LONG).show();


                    final String meramessage = writeMessage;
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 100ms
//                    mDilatingDotsProgressBar.hideNow();

                                                //WWAITING
                                                if (meramessage.length() == 7) {
                                                    //Toast.makeText(getApplicationContext(),"UNLOCKED MA GAYu",Toast.LENGTH_SHORT).show();
//                                                    Intent tounlock = new Intent(StatusGet.this,ToUnlock.class);
//                                                    startActivity(tounlock);
                                                    SG.setVisibility(View.GONE);
                                                    iv.setImageResource(R.drawable.closelock);
                                                    iv.setVisibility(View.VISIBLE);

                                                } else if (meramessage.length() == 9) {
                                                    //Toast.makeText(getApplicationContext(),"LOCKED MA GAYu",Toast.LENGTH_SHORT).show();
//                                                    Intent tolock =  new Intent(StatusGet.this, ToLock.class);
//                                                    startActivity(tolock);
                                                    SG.setVisibility(View.GONE);
                                                    iv.setImageResource(R.drawable.openlock);
                                                    iv.setVisibility(View.VISIBLE);
                                                }


                                            }
                                        }

                            , 800);
                    handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                //Do something after 100ms
//                    mDilatingDotsProgressBar.hideNow();

                                                //WWAITING


                                                if (meramessage.length() == 7) {
                                                    //Toast.makeText(getApplicationContext(),"UNLOCKED MA GAYu",Toast.LENGTH_SHORT).show();
                                                    Intent tounlock = new Intent(StatusGet.this, ToUnlock.class);
                                                    startActivity(tounlock);
                                                    finish();
                                                } else if (meramessage.length() == 9) {
                                                    //Toast.makeText(getApplicationContext(),"LOCKED MA GAYu",Toast.LENGTH_SHORT).show();
                                                    Intent tolock = new Intent(StatusGet.this, ToLock.class);
                                                    startActivity(tolock);
                                                    finish();
                                                }


                                            }
                                        }

                            , 4500);


//                    if(writeMessage.length()==7){
//                        //Toast.makeText(getApplicationContext(),"UNLOCKED MA GAYu",Toast.LENGTH_SHORT).show();
//                        Intent tounlock = new Intent(StatusGet.this,ToUnlock.class);
//                        startActivity(tounlock);
//                    }
//                    else if(writeMessage.length() == 9){
//                        //Toast.makeText(getApplicationContext(),"LOCKED MA GAYu",Toast.LENGTH_SHORT).show();
//                        Intent tolock =  new Intent(StatusGet.this, ToLock.class);
//                        startActivity(tolock);
//                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_get);

//        SG = (com.dd.CircularProgressButton) findViewById(R.id.statusgetting);
        SG = (Button) findViewById(R.id.statusgetting);
        Globalshare mApp = ((Globalshare) getApplicationContext());
        btSocket = mApp.getGlobalSocketValue();

        iv = (ImageView) findViewById(R.id.imageView);
        iv.setVisibility(View.GONE);
    }

    public void StatusGetting(View view) {

//        SG.setIndeterminateProgressMode(true);

//        SG.setProgress(50);

        if (btSocket != null) {
            ct = new ConnectedThread(btSocket);
            ct.start();
            ct.write("GETSTATUS\n");
        } else {
            //SOCKET DIDNT GET IN THIS ACTIVITY
        }


    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        byte[] buffer = new byte[1024];

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "\n".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String s) {
            try {
                mmOutStream.write(s.getBytes());
                //Toast.makeText(getApplicationContext(), buffer.toString(),Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
