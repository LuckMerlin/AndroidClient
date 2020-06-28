package com.merlin.browser;


import com.merlin.browser.config.Config;

public class Application extends android.app.Application {
    private final Config mConfig=new Config();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public  Config getConfig() {
        return mConfig;
    }
}
