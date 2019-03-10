package com.pishangujeniya.SmartDoorLock;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BlueList extends AppCompatActivity {

    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter BA;
    String address = null;
    BluetoothSocket btSocket;
    Globalshare mAppl;
    Boolean isBtConnected = false;
    private ListView lv;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_list);

        lv = (ListView) findViewById(R.id.BlueListView);

        BA = BluetoothAdapter.getDefaultAdapter();
        btSocket = null;

        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        for (BluetoothDevice bt : pairedDevices)
            list.add(bt.getName() + bt.getAddress());
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemvalue = (String) lv.getItemAtPosition(position);
                // BA.cancelDiscovery();
                //  Toast.makeText(getApplicationContext(),"Connecting to : "+itemvalue, Toast.LENGTH_SHORT).show();

                String info = ((TextView) view).getText().toString();
                address = info.substring(info.length() - 17);

                //Toast.makeText(getApplicationContext(),itemvalue,Toast.LENGTH_LONG).show();
                new ConnectBT().execute();
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(BlueList.this, "Connecting...", "Please wait!!!");  //show a progress dialogue
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {

                if (btSocket == null || !isBtConnected) {
                    BA = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = BA.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(MY_UUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            Globalshare mApp = ((Globalshare) getApplicationContext());

            if (!ConnectSuccess) {
                Toast.makeText(getApplicationContext(), "Connection Failed. Is it a SPP Bluetooth? Try again.", Toast.LENGTH_SHORT).show();
                try {
                    btSocket.close();
                    BA.disable();
                } catch (Exception e) {
                    BA.disable();
                    //this.finishAffinity();
                    finish();
                    System.exit(0);
                }
                Intent iagain = new Intent(BlueList.this, Connect.class);
                startActivity(iagain);

            } else {
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                isBtConnected = true;
                mApp.setGlobalSocketValue(btSocket);
                Intent i = new Intent(BlueList.this, StatusGet.class);
                startActivity(i);
            }
            progress.dismiss();
        }
    }
}
