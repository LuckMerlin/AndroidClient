package com.file.task;

import android.os.Binder;

import com.merlin.task.Matcher;
import com.merlin.task.Task;

import java.util.List;

public abstract class TaskServiceBinder extends Binder {

    public abstract List<Task> getTasks(Matcher matcher, int max);
}
