package com.merlin.player;

import com.merlin.mvvm.LMBinder;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LMBinder.autoBindActivityModel(this);
    }
}
