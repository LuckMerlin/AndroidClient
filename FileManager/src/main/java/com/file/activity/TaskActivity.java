package com.file.activity;

import android.net.Uri;
import android.os.Bundle;


import androidx.annotation.Nullable;

import com.file.model.TaskModel;
import com.google.gson.Gson;
import com.merlin.bean.Path;
import com.merlin.file.transport.NasFileUploadTask;
import com.merlin.model.ModelActivity;
import com.merlin.task.OnTaskUpdate;
import com.merlin.task.Task;
import com.merlin.task.TaskExecutor;
import com.task.debug.Debug;

public class TaskActivity extends ModelActivity<TaskModel> {
    OnTaskUpdate update=new OnTaskUpdate() {
        @Override
        public void onTaskUpdate(int status, int what, String note, Object obj, Task task) {
                Debug.D("dddddd "+status+" "+what+" "+note+" "+obj+" "+task);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskExecutor executor=new TaskExecutor();
//        String htt="http://sc1.111ttt.cn/2017/1/11/11/304112002493.mp3";
//        Task task=new HttpDownloadTask(htt,"/sdcard/linqiangTest.mp3");
//        Path path=new Path();
        String path="{\n" +
                "        \"modifyTime\":1549323360,\n" +
                "        \"parent\":\"/volume1/music/\",\n" +
                "        \"size\":-10009,\n" +
                "        \"mode\":33279,\n" +
                "        \"permissions\":33279,\n" +
                "        \"name\":\"张信哲、刘嘉玲 - 有一点动心\",\n" +
                "        \"accessTime\":1595076902,\n" +
                "        \"length\":9322928,\n" +
                "        \"port\":2018,\n" +
                "        \"md5\":null,\n" +
                "        \"mime\":\"audio/mpeg\",\n" +
                "        \"host\":\"192.168.0.6\",\n" +
                "        \"createTime\":1571472865,\n" +
                "        \"extension\":\".mp3\"\n" +
                "    }";
        String folder="{\n" +
                "        \"modifyTime\":1549323360,\n" +
                "        \"parent\":\"/volume1/MFiles/\",\n" +
                "        \"size\":0,\n" +
                "        \"mode\":33279,\n" +
                "        \"permissions\":33279,\n" +
                "        \"name\":\"Music\",\n" +
                "        \"accessTime\":1595076902,\n" +
                "        \"length\":9322928,\n" +
                "        \"port\":2018,\n" +
                "        \"md5\":null,\n" +
                "        \"mime\":\"audio/mpeg\",\n" +
                "        \"host\":\"192.168.0.6\",\n" +
                "        \"createTime\":1571472865,\n" +
                "        \"extension\":\"\"\n" +
                "    }";
//        NasFileDownloadTask task=new NasFileDownloadTask(new Gson().fromJson(path,Path.class),"/sdcard/linqiang.mp3");
//        task.setCover(Cover.COVER_REPLACE);
//        boolean d=executor.add(task,null);
        executor.put(update,null);
        executor.addTask(new NasFileUploadTask("/sdcard/linqiang.mp3",new Gson().fromJson(folder,Path.class),"linqiang.mp3"),null);
        boolean ddd=executor.start(null);
        Debug.D("AAAAAAAAAAa  "+ddd);
    }
}
