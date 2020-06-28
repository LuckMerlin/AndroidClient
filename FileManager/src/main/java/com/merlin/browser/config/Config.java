package com.merlin.browser.config;

public final class Config {
    private String mServerHost;
    private String mServerPort;

    public String getServerHost() {
        return mServerHost;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public String getServer(){
        String serverHost=mServerHost;
        String serverPort=mServerPort;
        return (null!=serverHost?serverHost:"")+(null!=serverPort?serverPort:"");
    }
}
