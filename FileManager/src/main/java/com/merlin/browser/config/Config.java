package com.merlin.browser.config;

public final class Config {
    private String mServerHost="http://106.12.163.77";
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
