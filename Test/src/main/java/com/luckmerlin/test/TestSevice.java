package com.luckmerlin.test;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.luckmerlin.core.debug.Debug;

public class TestSevice extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Debug.D("DDDoonBindDDDDdd  "+this);
        return new TestBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Debug.D("DDDDDDDdd  "+this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Debug.D("DDDDonDestroyDDDdd  "+this);
    }
}
