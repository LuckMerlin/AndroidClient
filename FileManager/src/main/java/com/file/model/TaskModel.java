package com.file.model;

import android.view.View;

import com.merlin.adapter.TaskAdapter;
import com.merlin.file.transport.NasFileUploadTask;
import com.merlin.task.Task;
import com.task.debug.Debug;

import java.util.ArrayList;
import java.util.List;

public class TaskModel extends BaseModel {
    private final TaskAdapter mAdapter=new TaskAdapter();

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        List<Task> tasks=new ArrayList<>();
        tasks.add(new NasFileUploadTask("测试1","",null,""));
        tasks.add(new NasFileUploadTask("测试2","",null,""));
        tasks.add(new NasFileUploadTask("测试3","",null,""));
        tasks.add(new NasFileUploadTask("测试4","",null,""));
        mAdapter.set(tasks,null);
        Debug.D("aaaaaaaaaaa"+this);
    }

    public TaskAdapter getAdapter() {
        return mAdapter;
    }
}
