package com.merlin.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Create LuckMerlin
 * Date 15:43 2020/8/12
 * TODO
 */
public class TestService3 extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }
}
