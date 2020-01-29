package com.merlin.bean;

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

    private String account;
    private String deviceType;
    private String name;
    private String platform;
    private long free;
    private long total;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public long getTotal() {
        return total;
    }

    public long getFree() {
        return free;
    }

    public boolean isDeviceType(String type){
        String deviceType=null!=type?this.deviceType:null;
        return null!=deviceType&&deviceType.equals(type);
    }

}
