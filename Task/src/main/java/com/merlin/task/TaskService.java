package com.merlin.task;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TaskService extends Service {
    private final TaskExecutor mExecutor=new TaskExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
