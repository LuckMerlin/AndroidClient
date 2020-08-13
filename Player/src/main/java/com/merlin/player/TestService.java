package com.merlin.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.luckmerlin.core.debug.Debug;

/**
 * Create LuckMerlin
 * Date 15:43 2020/8/12
 * TODO
 */
public class TestService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DDD();
    }

    private final static class DDD extends Binder{

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.D("ASDonDestroyFAS ");
    }
}
