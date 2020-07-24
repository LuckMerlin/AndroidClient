package com.file.task;

import android.os.Handler;
import android.os.Looper;

import com.merlin.file.transport.NasFileUploadTask;
import com.merlin.task.Task;

public class TaskService extends com.merlin.task.TaskService {
    private Task mTask=new NasFileUploadTask("任务34242","",null,"");

    @Override
    public void onCreate() {
        super.onCreate();
//        Task dd=new NasFileUploadTask("1","",null,"");
        mExecutor.addTask(new NasFileUploadTask("1werq","",null,""),"");
        mExecutor.addTask(new NasFileUploadTask("2rwqr","",null,""),"");
        mExecutor.addTask(new NasFileUploadTask("3wrqw","",null,""),"");
        Handler handler= new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExecutor.removeTask((d)->{ return true;},null,"");
//                handler.postDelayed();
            }
        },3000);
    }
}
