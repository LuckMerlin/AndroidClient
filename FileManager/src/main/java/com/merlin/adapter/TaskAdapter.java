package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.merlin.file.R;
import com.merlin.file.databinding.ItemTaskBinding;
import com.merlin.task.Task;

import java.util.List;

public class TaskAdapter extends ListAdapter<Task>{

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA? R.layout.item_task:null;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, Task data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, viewType, binding, position, data, payloads);
        if (null!=binding&&binding instanceof ItemTaskBinding){
            String statusText=null;
            if (null!=data){
                if (data.isDoing()){
                    statusText=getText(R.string.pause);
                }else if (data.isFinishSucceed()){
                    statusText=getText(R.string.succeed);
                }else if (data.isFinished()){
                    statusText=getText(R.string.finished);
                }else if (data.isIdle()){
                    statusText=getText(R.string.start);
                }
            }
            ((ItemTaskBinding)binding).setTask(data);
            ((ItemTaskBinding)binding).setStatusText(statusText);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }
}
