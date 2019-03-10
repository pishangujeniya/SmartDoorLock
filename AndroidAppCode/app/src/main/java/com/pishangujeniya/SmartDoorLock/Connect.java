package com.pishangujeniya.SmartDoorLock;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class Connect extends AppCompatActivity {
    Handler handler = new Handler();
    //    com.dd.CircularProgressButton CN;
    Button CN;
    BluetoothAdapter BA;
    Button connecthistorybutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        connecthistorybutton = (Button) findViewById(R.id.connecthistorybutton);

//        CN = (com.dd.CircularProgressButton) findViewById(R.id.connectnow);
        CN = (Button) findViewById(R.id.connectnow);
        BA = BluetoothAdapter.getDefaultAdapter();


    }

    public void connecthistoryonclick(View v) {

        Intent showhistory = new Intent(Connect.this, History.class);
        startActivity(showhistory);

    }

    public void ConnectNow(View view) {

//        CN.setIndeterminateProgressMode(true);

        //   CN.setProgress(25);

//        CN.setProgress(50);

        Intent turnon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);


        //CN.setProgress(50);
        startActivityForResult(turnon, 0);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                if (BA.isEnabled()) {
//                    CN.setProgress(100);
                    Toast.makeText(getApplicationContext(), "Bluetooth ON", Toast.LENGTH_SHORT).show();
                    //Connected Method Start Below
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms

                            Intent bl = new Intent(Connect.this, BlueList.class);
                            startActivity(bl);


                        }
                    }, 1200);


                } else {
//                    CN.setProgress(-1);
                    Toast.makeText(getApplicationContext(), "Bluetooth ERROR", Toast.LENGTH_SHORT).show();
                    waito(2000);
                    if (BA.isEnabled()) {
//                        CN.setProgress(100);
                        Toast.makeText(getApplicationContext(), "Bluetooth ON", Toast.LENGTH_SHORT).show();
                        // Connected Method Start Below
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms

                                Intent bl = new Intent(Connect.this, BlueList.class);
                                startActivity(bl);


                            }
                        }, 1200);

                    } else {
                        Intent i = new Intent(Connect.this, MainActivity.class);
                        startActivity(i);
                    }

                }


            }
        }, 5000);


    }

    public void waito(int milsec) {
        handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    //WAITING
                                }
                            }

                , milsec);

    }

    // TO not go back to Splash Screen
    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();

    }
}
