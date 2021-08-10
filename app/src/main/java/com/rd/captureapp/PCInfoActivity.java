package com.rd.captureapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.rd.captureapp.classes.Connectivity;
import com.rd.captureapp.classes.SMBTask;
import com.rd.captureapp.models.PCInforModel;

public class PCInfoActivity extends BaseActivity {
    EditText ipAddress, username, password, sharedFolder, domain;
    CheckBox isSaveInfo;
    Button enterBtn;
    PCInforModel pcInforModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_c_info);
        mContext = this;
        initial();
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    public void initial(){
        pcInforModel = getPc();

        ipAddress = findViewById(R.id.ipAddress);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        isSaveInfo = findViewById(R.id.isSaveInfo);
        sharedFolder = findViewById(R.id.sharedFolder);
        domain = findViewById(R.id.domain);
        enterBtn = findViewById(R.id.enterBtn);
        enterBtn.setOnClickListener(v ->  {
            enterBtnFunc();
        });
        if(pcInforModel != null){
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
    }
    private void enterBtnFunc(){
        Connectivity connectivity = new Connectivity(mContext);
        if(connectivity.isConnected()){
            if(TextUtils.isEmpty(ipAddress.getText().toString().trim())  || TextUtils.isEmpty( username.getText().toString().trim())|| TextUtils.isEmpty( password.getText().toString().trim()) || TextUtils.isEmpty(sharedFolder.getText().toString().trim()))
            {
                showMessage("Oops!", "Please fill up required fields!", true, "Ok", "", null,null);
            }else{
                pcInforModel = new PCInforModel();
                pcInforModel.setIpAdd(ipAddress.getText().toString().trim());
                pcInforModel.setUsername(username.getText().toString().trim());
                pcInforModel.setPassword(password.getText().toString().trim());
                pcInforModel.setSharedFolder(sharedFolder.getText().toString().trim());
                pcInforModel.setDomain(domain.getText().toString().trim());
                if (isSaveInfo.isChecked()) {
                    getPcInfoModel().put(pcInforModel);
                }
                SMBTask smbTask = new SMBTask(pcInforModel,mContext, null);
                smbTask.execute();
            }
        }else{
            showMessage("Internet Connection Problem!", "Please check your internet connection.", true, "Ok", "", null,null);
        }
    }

}