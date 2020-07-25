package com.file.model;

import android.content.ComponentName;
import android.os.IBinder;
import android.view.View;

import com.merlin.adapter.TaskAdapter;
import com.merlin.click.OnTapClick;
import com.merlin.file.R;
import com.merlin.model.OnServiceBindChange;
import com.merlin.task.OnTaskUpdate;
import com.merlin.task.Status;
import com.merlin.task.Task;
import com.merlin.task.TaskBinder;

public class TaskModel extends BaseModel implements OnServiceBindChange, OnTaskUpdate, OnTapClick {
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
    public final boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            default:
                if (null!=data&&data instanceof Task){
                    return pauseStart((Task)data,"After tap click.");
                }
                break;
        }
        return true;
    }

    private boolean pauseStart(Task task,String debug){
        if (null!=task){
            if (task.isFinishSucceed()){
                return toast(getText(R.string.alreadyWhat,getText(R.string.succeed)))&&false;
            }else if (!task.isDoing()){
                return startTask(task,debug);
            }
        }
        return false;
    }

    private boolean startTask(Task task,String debug){
        TaskBinder binder=null!=task?mBinder:null;
        return null!=binder&&!task.isDoing()&&binder.start((child)->null!=child&&child==task,debug,null);
    }

    private boolean pauseTask(Task task,String debug){
        TaskBinder binder=null!=task?mBinder:null;
        return null!=binder&&!task.isDoing()&&binder.pause((child)->null!=child&&child==task,debug);
    }

    @Override
    public void onTaskUpdate(int status, int what, String note, Object obj, Task task) {
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
