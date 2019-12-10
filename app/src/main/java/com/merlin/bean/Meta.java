package com.merlin.bean;

public final class Meta {
    private final String mName;
    private final String mDeviceType;
    private final String mFileDivider;
    private final long mUsedSpace;
    private final long mTotalSpace;

    public Meta(String name,String deviceType,String fileDivider,long usedSpace,long totalSpace){
        mName=name;
        mDeviceType=deviceType;
        mFileDivider=fileDivider;
        mUsedSpace=usedSpace;
        mTotalSpace=totalSpace;
    }

    public String getName() {
        return mName;
    }

    public long getTotalSpace() {
        return mTotalSpace;
    }

    public long getUsedSpace() {
        return mUsedSpace;
    }

    public String getFileDivider() {
        return mFileDivider;
    }

    public String getDeviceType() {
        return mDeviceType;
    }
}
