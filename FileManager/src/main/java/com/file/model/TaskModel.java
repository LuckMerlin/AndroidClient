package com.file.model;

import android.content.ComponentName;
import android.os.IBinder;

import com.file.task.TaskServiceBinder;
import com.merlin.adapter.TaskAdapter;
import com.merlin.model.OnServiceBindChange;
import com.merlin.task.OnTaskUpdate;
import com.merlin.task.Task;

public class TaskModel extends BaseModel implements OnServiceBindChange, OnTaskUpdate {
    private final TaskAdapter mAdapter=new TaskAdapter();
    private TaskServiceBinder mBinder;

    @Override
    public void onServiceBindChanged(ComponentName name, IBinder service) {
        mBinder=null!=service&&service instanceof TaskServiceBinder?((TaskServiceBinder)service):null;
    }

    @Override
    public void onTaskUpdate(int status, int what, String note, Object obj, Task task) {
        TaskServiceBinder binder=mBinder;
    }

    public TaskAdapter getAdapter() {
        return mAdapter;
    }
}
