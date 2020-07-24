package com.file.task;

import android.os.Handler;
import android.os.Looper;

import com.merlin.file.transport.NasFileUploadTask;
import com.merlin.task.Task;
import com.merlin.task.file.HttpDownloadTask;

public class TaskService extends com.merlin.task.TaskService {
    private Task mTask=new NasFileUploadTask("任务34242","",null,"");

    @Override
    public void onCreate() {
        super.onCreate();
        Handler handler= new Handler(Looper.getMainLooper());
//        String name,String from, String to,String method
        Task task=new HttpDownloadTask("下载文件","https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1915162133,1168520697&fm=15&gp=0.jpg",
                "/sdcard/lin.jpg","GET");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mExecutor.addTask(task,"");
                mExecutor.start("");
//                mExecutor.removeTask((d)->{ return true;},null,"");
//                handler.postDelayed();
            }
        },5000);
    }
}
