package com.rd.captureapp.models;

import com.hierynomus.smbj.share.DiskShare;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PCInforModel implements Serializable {

    @Id
    long id;
    long ipAddress;
    String username;
    String password;
    String  ipAdd;
    String sharedFolder;
    String domain;


    public void setDomain(String domain){this.domain = domain;}
    public String getDomain(){return domain;}
    public void setSharedFolder(String sharedFolder){this.sharedFolder = sharedFolder;}
    public String getSharedFolder(){return sharedFolder;}
    public void setId(long id) {
        this.id = id;
    }
    public long getId() {
        return id;
    }
    public void setIpAdd(String ipAdd){
        this.ipAdd = ipAdd;
    }
    public String getIpAdd(){
        return ipAdd;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return  username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPassword(){
        return password;
    }

}
