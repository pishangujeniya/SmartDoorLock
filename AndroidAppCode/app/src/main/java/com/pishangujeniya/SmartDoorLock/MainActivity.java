package com.pishangujeniya.SmartDoorLock;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.zl.reik.dilatingdotsprogressbar.DilatingDotsProgressBar;

public class MainActivity extends AppCompatActivity {

    public DilatingDotsProgressBar mDilatingDotsProgressBar;
    public BluetoothAdapter BA;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BA = BluetoothAdapter.getDefaultAdapter();

        mDilatingDotsProgressBar = (DilatingDotsProgressBar) findViewById(R.id.progress);

// show progress bar and start animating
        mDilatingDotsProgressBar.showNow();

        if (BA.isEnabled()) {

            BA.disable();


            //delay(1000);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    Intent i = new Intent(MainActivity.this, Connect.class);
                    startActivity(i);
                }
            }, 1500);


        } else {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    Intent i = new Intent(MainActivity.this, Connect.class);
                    startActivity(i);


                }
            }, 1500);


        }

    }


}
