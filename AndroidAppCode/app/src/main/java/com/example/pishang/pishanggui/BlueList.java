package com.example.pishang.pishanggui;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;
    BluetoothAdapter BA;
    String address = null;
    private ProgressDialog progress;
    BluetoothSocket btSocket;
    Globalshare mAppl;
    Boolean isBtConnected = false;
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_list);

        lv =  (ListView)findViewById(R.id.BlueListView);

        BA = BluetoothAdapter.getDefaultAdapter();
        btSocket =  null;

       // Snackbar.make(findViewById(android.R.id.content),"HELLO THIS IS SCNAK", Snackbar.LENGTH_LONG).show();

//        if (ContextCompat.checkSelfPermission(BlueList.this,
//                Manifest.permission.BLUETOOTH)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(BlueList.this,
//                    Manifest.permission.BLUETOOTH)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(BlueList.this,
//                        new String[]{Manifest.permission.BLUETOOTH},);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }


        //CODE sSTARTS here

        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName()+bt.getAddress());
        Toast.makeText(getApplicationContext(),"Showing Paired Devices",Toast.LENGTH_SHORT).show();

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemindex =  position;
                String itemvalue = (String) lv.getItemAtPosition(itemindex);
                // BA.cancelDiscovery();
                //  Toast.makeText(getApplicationContext(),"Connecting to : "+itemvalue, Toast.LENGTH_SHORT).show();

                String info = ((TextView) view).getText().toString();
                address = info.substring(info.length() - 17);

                //Toast.makeText(getApplicationContext(),itemvalue,Toast.LENGTH_LONG).show();
                new ConnectBT().execute();
            }
        });



    }

//    public void listvisible(View v) {



//    }




//Cpde emds

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(BlueList.this, "Connecting...", "Please wait!!!");  //show a progress dialogue
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {

                if (btSocket == null || !isBtConnected)
                {
                    BA = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = BA.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(MY_UUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            Globalshare mApp = ((Globalshare) getApplicationContext());

            if (!ConnectSuccess)
            {
                Toast.makeText(getApplicationContext(),"Connection Failed. Is it a SPP Bluetooth? Try again.",Toast.LENGTH_SHORT).show();
                try{
                    btSocket.close();
                    BA.disable();
                }catch (Exception e)
                {
                    BA.disable();
                    //this.finishAffinity();
                    finish();
                    System.exit(0);
                }
                Intent iagain = new Intent(BlueList.this,Connect.class);
                startActivity(iagain);

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                isBtConnected = true;
                mApp.setGlobalSocketValue(btSocket);
                Intent i = new Intent(BlueList.this,StatusGet.class);
                startActivity(i);
            }
            progress.dismiss();
        }
    }
}
