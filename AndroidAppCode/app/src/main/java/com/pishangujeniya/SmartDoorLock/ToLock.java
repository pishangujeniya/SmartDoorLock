package com.pishangujeniya.SmartDoorLock;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ToLock extends AppCompatActivity {
    BluetoothSocket btSocket;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    InputStream mmInputStream;
    OutputStream mmOutStream;
    Globalshare mApp;
    ConnectedThread ct;

    String CLOSECOMMAND;

    String pvalue;

    EditText closepass;

    BluetoothAdapter BA;
    ImageView ivclose;

    Button tolockhistorButton;
    String dt;

    DatabaseHelper mydb;
    @SuppressLint("HandlerLeak")
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
                    Snackbar.make(findViewById(android.R.id.content), writeMessage, Snackbar.LENGTH_LONG).show();
                    if (writeMessage.length() == 8) {
                        ivclose.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_lock);

        setTitle("To LOCK");

        mydb = new DatabaseHelper(this);

        Globalshare mApp = ((Globalshare) getApplicationContext());
        btSocket = mApp.getGlobalSocketValue();
        tolockhistorButton = (Button) findViewById(R.id.tolockhistorybutton);

        Button B6 = (Button) findViewById(R.id.button6);

        closepass = (EditText) findViewById(R.id.editText4);

        BA = BluetoothAdapter.getDefaultAdapter();

        ivclose = (ImageView) findViewById(R.id.closelockiv);
        ivclose.setVisibility(View.INVISIBLE);


    }

    public void tolockhistory(View v) {

        Intent showhistory = new Intent(ToLock.this, History.class);
        startActivity(showhistory);
    }

    public void exitapp(View v) {

        try {

            btSocket.close();
            BA.disable();
            this.finishAffinity();
            finish();
            System.exit(0);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Socket Closing error", Toast.LENGTH_SHORT).show();
        }


    }

    public void closekar(View v) {

        // DATABASE MA ENTRY MARVANO MODULE
        dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        boolean isinserted = mydb.insertdata(dt, "LOCKED");
        if (isinserted = true) {
//            Toast.makeText(getApplicationContext(),"INSERTED",Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(getApplicationContext(),"NOT INSERTED",Toast.LENGTH_SHORT).show();
        }

        // DATABASE MA ENTRY MARVANO MODULE  END COMMENT

        pvalue = closepass.getText().toString();
        CLOSECOMMAND = "CLOSE=" + pvalue + "\n";

        if (btSocket != null) {
            ct = new ConnectedThread(btSocket);
            ct.start();
            ct.write(CLOSECOMMAND);

        } else {
            Toast.makeText(getApplicationContext(), "Socket Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        byte[] buffer = new byte[1024];

        ConnectedThread(BluetoothSocket socket) {
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
        void write(String s) {
            try {
                mmOutStream.write(s.getBytes());
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
