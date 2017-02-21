package com.example.pishang.pishanggui;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;


public class Globalshare extends Application{



    private String mGlobalVarValue;
    private BluetoothSocket btSocket;
    public String getGlobalVarValue() {
        return mGlobalVarValue;
    }

    public void setGlobalVarValue(String str) {
        mGlobalVarValue = str;
    }

    public BluetoothSocket getGlobalSocketValue(){
        return btSocket;
    }

    public void setGlobalSocketValue(BluetoothSocket socket){
        this.btSocket = socket;
    }
}




