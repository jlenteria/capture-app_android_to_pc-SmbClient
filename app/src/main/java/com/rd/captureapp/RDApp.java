package com.rd.captureapp;

import android.app.Application;

import com.rd.captureapp.models.MyObjectBox;

import io.objectbox.BoxStore;

public class RDApp extends Application {
    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(RDApp.this).build();
    }
    public BoxStore getBoxStore(){return boxStore;}
}
