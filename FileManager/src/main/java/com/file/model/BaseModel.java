package com.file.model;

import android.app.Application;

import com.merlin.browser.config.Config;
import com.merlin.model.Model;

import java.util.concurrent.Executor;

public class BaseModel extends Model {


    protected final String getServerUri(){
        Config config=getConfig();
        return null!=config?config.getServerUri():null;
    }

    @Override
    protected String onResolveUrl(Class<?> cls, Executor callbackExecutor) {
        return getServerUri();
    }

    protected final Config getConfig(){
        Application application= getApplication();
        com.merlin.browser.Application app=null!=application&&application instanceof
                com.merlin.browser.Application?(com.merlin.browser.Application)application:null;
        return null!=app?app.getConfig():null;
    }
}
