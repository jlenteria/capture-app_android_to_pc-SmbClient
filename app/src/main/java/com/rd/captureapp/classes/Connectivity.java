package com.rd.captureapp.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Connectivity {
    Context mContext;

    public Connectivity(Context _context){
        mContext = _context;
    }

    public boolean isConnected(){
        boolean connected = false;
        try{
            ConnectivityManager cm =  (ConnectivityManager)mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
        }catch (Exception e){
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}
