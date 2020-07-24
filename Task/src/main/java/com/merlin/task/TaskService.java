package com.merlin.task;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import java.util.List;

public class TaskService extends Service {
    protected final TaskExecutor mExecutor=new TaskExecutor(new Handler(Looper.getMainLooper()));

    @Override
    public IBinder onBind(Intent intent) {
        return new TaskBinder(mExecutor);
    }
}
