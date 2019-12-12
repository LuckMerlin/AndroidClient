package com.merlin.bean;

import java.io.Serializable;

public final class Meta implements Serializable {
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

    private String account;
    private double timestamp;
    private int altitude;
    private int longitude;
    private String address;
    private String deviceType;
    private String name;
    private String platform;
    private String root;
    private long free;
    private long total;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public void setTotal(long total) {
        this.total = total;
    }


    public String getRoot() {
        return root;
    }

    //    private boolean mOnline=false;
//    private final String mAccount;
//    private final String mName;
//    private final String mDeviceType;
//    private final String mRoot;
//    private final long mFreeSpace;
//    private final long mTotalSpace;
//
//    public Meta(String account,String name,String deviceType,String root,long freeSpace,long totalSpace){
//        mAccount=account;
//        mName=name;
//        mDeviceType=deviceType;
//        mRoot=root;
//        mFreeSpace=freeSpace;
//        mTotalSpace=totalSpace;
//    }
//
//
//    public void setOnline(boolean online) {
//        this.mOnline = online;
//    }
//
//    public boolean isOnline() {
//        return mOnline;
//    }
//
//    public String getName() {
//        return mName;
//    }
//
    public long getTotalSpace() {
        return total;
    }

    public long getFreeSpace() {
        return free;
    }
//
//    public String getAccount() {
//        return mAccount;
//    }
//
//    public String getRoot() {
//        return mRoot;
//    }
//
    public boolean isDeviceType(String type){
        String deviceType=null!=type?this.deviceType:null;
        return null!=deviceType&&deviceType.equals(type);
    }
//
//    public String getDeviceType() {
//        return mDeviceType;
//    }


    @Override
    public String toString() {
        return "Meta{" +
                "account='" + account + '\'' +
                ", timestamp=" + timestamp +
                ", altitude=" + altitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", name='" + name + '\'' +
                ", platform='" + platform + '\'' +
                ", root='" + root + '\'' +
                ", freeSpace=" + free +
                ", totalSpace=" + total +
                '}';
    }
}
