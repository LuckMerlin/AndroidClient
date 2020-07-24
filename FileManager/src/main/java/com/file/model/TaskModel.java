package com.file.model;

import android.content.ComponentName;
import android.os.IBinder;

import com.merlin.adapter.TaskAdapter;
import com.merlin.model.OnServiceBindChange;
import com.merlin.task.OnTaskUpdate;
import com.merlin.task.Status;
import com.merlin.task.Task;
import com.merlin.task.TaskBinder;
import com.task.debug.Debug;

public class TaskModel extends BaseModel implements OnServiceBindChange, OnTaskUpdate {
    private final TaskAdapter mAdapter=new TaskAdapter();
    private TaskBinder mBinder;

    @Override
    public void onServiceBindChanged(ComponentName name, IBinder service) {
        TaskBinder binder=mBinder=null!=service&&service instanceof TaskBinder ?((TaskBinder)service):null;
        mAdapter.clean();
        if (null!=binder){
            binder.put(this,null);
            mAdapter.set(binder.getTasks(null,-1),"After service bind changed.");
        }
    }

    @Override
    public void onTaskUpdate(int status, int what, String note, Object obj, Task task) {
        Debug.D("ss "+status+" "+what+" "+note);
        switch (status){
            case Status.ADD:
                mAdapter.add(task,true,"After add status.");break;
            case Status.REMOVE:
                mAdapter.remove(task,"After remove status.");break;
            default:
                if (null!=task){
                    mAdapter.replace(task,"After task status update "+status);
                }
                break;
        }
    }

    public TaskAdapter getAdapter() {
        return mAdapter;
    }
}
