package com.merlin.transport;

import android.content.Intent;
import android.os.IBinder;

import com.merlin.global.Service;

public class TransporterService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
