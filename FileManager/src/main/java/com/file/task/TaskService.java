package com.file.task;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.merlin.task.Matcher;
import com.merlin.task.Task;

import java.util.List;

public class TaskService extends com.merlin.task.TaskService {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new TaskServiceBinder(){
            @Override
            public List<Task> getTasks(Matcher matcher, int max) {
                return TaskService.super.getTasks(matcher,max);
            }
        };
    }

}
