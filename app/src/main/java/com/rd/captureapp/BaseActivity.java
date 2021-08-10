package com.rd.captureapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AppCompatActivity;

import com.rd.captureapp.models.PCInforModel;
import com.rd.captureapp.models.PCInforModel_;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class  BaseActivity extends AppCompatActivity {
    public Context mContext;
    ProgressDialog progressDialog;
    private Box<PCInforModel> pcInfoBox;
    private Query<PCInforModel>pcQuery;

    public PCInforModel getPc(){
        PCInforModel _pcInfoModel;
        pcInfoBox = getPcInfoModel();
        pcQuery = pcInfoBox.query().order(PCInforModel_.ipAdd).build();

        if(pcQuery.find().size() > 0){
            _pcInfoModel = pcQuery.find().get(0);

            return _pcInfoModel;
        }
        return null;
    }

    public Box<PCInforModel> getPcInfoModel(){
        return ((App)getApplication()).getBoxStore().boxFor(PCInforModel.class);
    }

    public void clearLocalFile(){
        getPcInfoModel().removeAll();
    }
    public void showMessage(String title, String message, boolean cancelable, String pos, String neg, DialogInterface.OnClickListener posClick, DialogInterface.OnClickListener negClick)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(title);
        builder1.setCancelable(cancelable);
        builder1.setMessage(message);
        builder1.setPositiveButton(pos, posClick);
        builder1.setNegativeButton(neg, negClick);
        builder1.create().show();
    }

    public void showProgress(){
        runOnUiThread(() -> progressDialog = ProgressDialog.show(this, "", "Uploading, please wait...", true, false));
    }
    public void hideProgress(){
        if(progressDialog != null){
            runOnUiThread(() -> {
                progressDialog.dismiss();
            });
        }
    }

}
