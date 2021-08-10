package com.rd.captureapp.classes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.rd.captureapp.BaseActivity;
import com.rd.captureapp.MainActivity;
import com.rd.captureapp.R;
import com.rd.captureapp.models.PCInforModel;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

public class SMBTask extends AsyncTask<Void, Void, DiskShare> {
    PCInforModel pcInforModel;
    ProgressDialog progressDialog;
    Bitmap capturedImage;
    Context mContext;
    com.hierynomus.smbj.share.File file = null;

    public SMBTask(PCInforModel _pcInforModel, Context _mContext, Bitmap _capturedImage){
        this.pcInforModel = _pcInforModel;
        this.mContext = _mContext;
        this.progressDialog = new ProgressDialog(_mContext);
        this.capturedImage = _capturedImage;
    }
    @Override
    protected void onPreExecute() {
        if(capturedImage == null){
            progressDialog.setMessage("Connecting... please wait.");
        }
        else {
            progressDialog.setMessage("Uploading... please wait.");
        }
        progressDialog.show();
    }
    @Override
    protected DiskShare doInBackground(Void... voids) {
        DiskShare diskShare;
        try{
            Log.d("", "DOMAIN: " + pcInforModel.getDomain());
            SmbConfig cfg = SmbConfig.builder().build();
            SMBClient client = new SMBClient(cfg);
            Connection connection = client.connect(pcInforModel.getIpAdd());
            Session session = connection.authenticate(new AuthenticationContext(pcInforModel.getUsername(), pcInforModel.getPassword().toCharArray(), pcInforModel.getDomain()));
            DiskShare share = (DiskShare) session.connectShare(pcInforModel.getSharedFolder());

            if(capturedImage!= null){
                SimpleDateFormat formatter = new SimpleDateFormat("MM_dd_yyyy_hh_mm_ss");
                Date date = new Date();
                String uniqueID = UUID.randomUUID().toString();
                String date1 = formatter.format(date);
                String fileName = "Image_" + date1 + "_" + uniqueID +  ".png";

                if(!share.fileExists(fileName)){
                    file = share.openFile(fileName,
                            new HashSet<>(Arrays.asList(AccessMask.GENERIC_ALL)),
                            new HashSet<>(Arrays.asList(FileAttributes.FILE_ATTRIBUTE_NORMAL)),
                            SMB2ShareAccess.ALL,
                            SMB2CreateDisposition.FILE_CREATE,
                            new HashSet<>(Arrays.asList(SMB2CreateOptions.FILE_DIRECTORY_FILE))
                    );
                }
                try {
                    OutputStream os = file.getOutputStream();
                    capturedImage.compress(Bitmap.CompressFormat.PNG, 90, os);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            diskShare = share;
        }catch (Exception e){
            e.printStackTrace();
            diskShare = null;
        }
        return diskShare;
    }
    @SuppressLint("WrongThread")
    @Override
    protected void onPostExecute(DiskShare s) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        setResult(s);
    }
    public void setResult(DiskShare s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if(capturedImage != null){
            if(s != null){
                builder.setTitle("Success!");
                builder.setMessage("Successfully uploaded, please take a look at your shared folder named '" + pcInforModel.getSharedFolder() +"'" );
                builder.setCancelable(true);
                builder.setPositiveButton("OK", null);
                ImageView img = ((Activity)mContext).findViewById(R.id.imageView);
                Button btn = ((Activity)mContext).findViewById(R.id.uploadBtn);
                capturedImage = null;
                btn.setEnabled(false);
                btn.setBackgroundTintList(ContextCompat.getColorStateList(mContext, R.color.gray));
                img.setImageBitmap(null);
            }else{
                builder.setTitle("Oops!");
                builder.setMessage("Failed uploading image, please try again." );
                builder.setCancelable(false);
                builder.setPositiveButton("OK", null);
            }
        }else{
            if (s != null) {
                builder.setTitle("Success!");
                builder.setMessage("Successfully connected to " + pcInforModel.getIpAdd());
                builder.setCancelable(false);
                builder.setPositiveButton("OK", ((v,i) -> {
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("PC_INFO_EXTRA", pcInforModel);
                    mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) mContext).toBundle());
                }));
            } else {
                builder.setTitle("Oops!");
                builder.setCancelable(true);
                builder.setPositiveButton("OK", null);
                builder.setMessage("Connection failed, please input the right credentials.");
            }
        }
        builder.show();
    }
}
