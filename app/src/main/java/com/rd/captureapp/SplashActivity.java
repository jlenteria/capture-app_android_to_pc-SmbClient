package com.rd.captureapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.rd.captureapp.models.PCInforModel;

public class SplashActivity extends BaseActivity {
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        handler = new Handler();
        handler.postDelayed(() -> runOnUiThread(() -> {
            Intent pcActivity = new Intent(mContext, PCInfoActivity.class);
            pcActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(pcActivity);
        }),1000);
    }
}