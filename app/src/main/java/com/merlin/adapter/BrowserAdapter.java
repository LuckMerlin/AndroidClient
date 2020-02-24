package com.merlin.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.NasFile;
import com.merlin.bean.FileModify;
import com.merlin.bean.NasFolder;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemListFileBinding;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;

import java.util.ArrayList;
import java.util.List;

public abstract class BrowserAdapter extends MultiSectionAdapter<String, NasFile, NasFolder> implements OnMoreLoadable{
    private List<NasFile> mMultiChoose;

    public List<NasFile> getMultiChoose() {
        return mMultiChoose;
    }

    @Override
    protected Integer onResolveItemLayoutId(ViewGroup parent, int viewType) {
        return R.layout.item_list_file;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ViewDataBinding binding, int position, NasFile data, @NonNull List<Object> payloads) {
        if (null!=binding&&null!=data&&binding instanceof ItemListFileBinding){
            ItemListFileBinding itemBinding=(ItemListFileBinding)binding;
            boolean multiChoose=null!=mMultiChoose;
            itemBinding.setIsChoose(isChoose(data));
            itemBinding.setIsMultiChoose(multiChoose);
            itemBinding.setMeta(data);
            itemBinding.setPosition(position);
        }
    }

    public final boolean renamePath(NasFile meta, FileModify modify){
        List<NasFile> list=null!=meta&&null!=modify?getData():null;
        int size=null!=list?list.size():-1;
        int index=size>0?list.indexOf(meta):-1;
        meta=index>=0&&index<size?list.get(index):null;
        if (null!=meta&&meta.applyModify(modify)){
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }

    public final boolean chooseAll(boolean choose){
        List<NasFile> list = mMultiChoose;
        List<NasFile> data = getData();
        int size = null != data ? data.size() : 0;
        if (choose && (size > 0 &&(null==list||size != list.size()))) {
            if (null==list){
                mMultiChoose=list=new ArrayList<>();
            }else{
                list.clear();
            }
            list.addAll(data);
            notifyDataSetChanged();
            return true;
        } else if (!choose && null!=list&&list.size()>0) {
            list.clear();
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public boolean setMode(int mode){
        List current=mMultiChoose;
        if (null!=current){
            current.clear();
        }
        mMultiChoose=null;
        if (mode == FileBrowserModel.MODE_MULTI_CHOOSE){
            mMultiChoose=new ArrayList<>(1);
        }
        notifyDataSetChanged();
        return false;
    }

    public int getChooseCount(){
        List<NasFile> list=mMultiChoose;
        return null!=list?list.size():0;
    }

    public List<NasFile> getChoose(){
        List<NasFile> list=mMultiChoose;
        return list;
    }

    public boolean multiChoose(NasFile meta){
        List<NasFile> list=mMultiChoose;
        if (null!=meta){
            if (null==list?(list=new ArrayList<>()).add(meta):
                    (list.contains(meta)?list.remove(meta):list.add(meta))){
                mMultiChoose=null!=list&&list.size()>0?list:null;
                notifyItemChanged(index(meta));
                return true;
            }
        }
        return false;
    }

    public boolean isChoose(NasFile meta){
        List<NasFile> choose=mMultiChoose;
        return null!=meta&&null!=choose&&choose.contains(meta);
    }

}
