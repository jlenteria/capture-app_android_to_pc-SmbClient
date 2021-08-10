package com.rd.captureapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rd.captureapp.classes.Connectivity;
import com.rd.captureapp.classes.SMBTask;
import com.rd.captureapp.models.PCInforModel;

import java.io.IOException;


import gun0912.tedbottompicker.TedBottomPicker;


public class MainActivity extends BaseActivity {
    public static final int CAMERA_PERM_CODE = 100;
    ImageView selectedImage;
    TextView txt_ipAdd;
    Button captureBtn, uploadBtn,changePc;
    Bitmap capturedImageBm = null;
    Uri mCapturedImageURI;
    PCInforModel pcInforModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        checkNeedPermissions();
        initView();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    private void initView() {
        pcInforModel = getPc();
        if(pcInforModel != null){
            pcInforModel = getPc();
        }
        else{
            pcInforModel  = (PCInforModel) getIntent().getSerializableExtra("PC_INFO_EXTRA");
        }
        selectedImage = findViewById(R.id.imageView);
        captureBtn = findViewById(R.id.captureBtn);
        uploadBtn = findViewById(R.id.uploadBtn);
        changePc = findViewById(R.id.changePc);
        txt_ipAdd =  findViewById(R.id.txt_ipAdd);

        if(pcInforModel != null){
            txt_ipAdd.setText(pcInforModel.getIpAdd());
        }
        captureBtn.setOnClickListener(v -> {
            try{
                checkNeedPermissions();
                openCamera();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        } );
        uploadBtn.setOnClickListener(v -> {
            try{
                checkNeedPermissions();
                uploadPicture();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        });
        changePc.setOnClickListener(v -> {
            clearLocalFile();
            Intent intent = new Intent(mContext, PCInfoActivity.class);
            intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        });
    }
    private void uploadPicture(){
        Connectivity connectivity = new Connectivity(mContext);
        if(connectivity.isConnected()){
            SMBTask smbTask = new SMBTask(pcInforModel, mContext, capturedImageBm);
            smbTask.execute();
        }else{
            showMessage("Internet Connection Problem!", "Please check your internet connection.", true, "Ok", "", null,null);
        }
    }
    private void openCamera() {
        TedBottomPicker imgPicker = new TedBottomPicker.Builder(this).setOnImageSelectedListener(uri -> {
            if(uri != null){
                mCapturedImageURI = uri;
                Glide.with(this).load(uri).into(selectedImage);
                try {
                    capturedImageBm =  MediaStore.Images.Media.getBitmap(this.getContentResolver(), mCapturedImageURI);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                uploadBtn.setEnabled(true);
                uploadBtn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.teal_700));
            }
        }).create();
        imgPicker.show(getSupportFragmentManager());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERM_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void checkNeedPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA )
                != PackageManager.PERMISSION_GRANTED ||  ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            }, CAMERA_PERM_CODE);
        }
    }
}
