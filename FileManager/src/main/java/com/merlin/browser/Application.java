package com.merlin.browser;


import android.content.Intent;

import com.merlin.browser.config.Config;
import com.merlin.file.transport.FileTaskService;

public class Application extends android.app.Application {
    private final Config mConfig=new Config();

    public  Config getConfig() {
        return mConfig;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        startService(new Intent(this, FileTaskService.class));
    }
}
