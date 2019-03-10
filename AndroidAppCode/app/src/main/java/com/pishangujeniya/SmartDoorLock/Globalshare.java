package com.pishangujeniya.SmartDoorLock;

import android.app.Application;
import android.bluetooth.BluetoothSocket;


public class Globalshare extends Application {

    private BluetoothSocket btSocket;

    public BluetoothSocket getGlobalSocketValue() {
        return btSocket;
    }

    public void setGlobalSocketValue(BluetoothSocket socket) {
        this.btSocket = socket;
    }
}




