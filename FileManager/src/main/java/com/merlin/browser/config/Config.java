package com.merlin.browser.config;

import android.os.Bundle;

public final class Config {
    private final Bundle mBundle=new Bundle();
//    private String mServerHost="http://106.12.163.77";
    private String mServerHost="http://192.168.0.6";
//    private String mServerPort="5000";
    private String mServerPort="2018";


    public String getServerHost() {
        return mServerHost;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public String getServerUri(){
        String serverHost=mServerHost;
        String serverPort=mServerPort;
        return (null!=serverHost?serverHost:"")+":"+(null!=serverPort?serverPort:"");
    }
}
