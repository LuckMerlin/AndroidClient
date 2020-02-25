package com.merlin.bean;

import android.content.Context;

import androidx.annotation.NonNull;

import com.merlin.client.R;

import java.io.Serializable;

public final class ClientMeta implements Serializable {
    /**
     * account : nas
     * timestamp : 1.5761359971479676E9
     * altitude : 0
     * longitude : 0
     * address :
     * deviceType : nasDevice
     * name : DESKTOP-1CGFIVE
     * platform : win32
     */
    private String url;
    private String account;
    private String deviceType;
    private String name;
    private String platform;
    private long free;
    private long total;
    private final static String LOCAL_URL="http://127.0.0.1";

    public static ClientMeta buildLocalClient(Context context){
        ClientMeta meta=new ClientMeta();
        meta.url=LOCAL_URL;
        meta.platform="Android";
        meta.deviceType="Mobile";
        meta.name=null!=context?context.getString(R.string.local):"Local";
        meta.account="Local";
        return meta;
    }

    public String getAccount() {
        return account;
    }

    public boolean isLocalClient(){
        String urlValue=url;
        return null!=urlValue&&urlValue.equals(LOCAL_URL);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public long getTotal() {
        return total;
    }

    public long getFree() {
        return free;
    }

    public String getUrl() {
        return url;
    }

    public boolean isDeviceType(String type){
        String deviceType=null!=type?this.deviceType:null;
        return null!=deviceType&&deviceType.equals(type);
    }

    @NonNull
    @Override
    public String toString() {
        return ""+url+" "+super.toString();
    }
}
