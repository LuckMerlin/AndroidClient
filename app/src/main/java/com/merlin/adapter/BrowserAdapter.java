package com.merlin.adapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.NasFile;
import com.merlin.bean.FileModify;
import com.merlin.model.BrowserModel;

import java.util.ArrayList;
import java.util.List;

public abstract class BrowserAdapter<T extends FileMeta> extends MultiSectionAdapter<String, T, FolderData<T>> implements OnMoreLoadable{
    private List<T> mMultiChoose;

    public final List<T> getMultiChoose() {
        return mMultiChoose;
    }

    public final boolean renamePath(T meta, FileModify modify){
        List<T> list=null!=meta&&null!=modify?getData():null;
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
        List<T> list = mMultiChoose;
        List<T> data = getData();
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

    public final boolean setMode(int mode){
        List current=mMultiChoose;
        if (null!=current){
            current.clear();
        }
        mMultiChoose=null;
        if (mode == BrowserModel.MODE_MULTI_CHOOSE){
            mMultiChoose=new ArrayList<>(1);
        }
        notifyDataSetChanged();
        return false;
    }

    public final int getChooseCount(){
        List<T> list=mMultiChoose;
        return null!=list?list.size():0;
    }

    public final boolean isMultiChoose(){
        return null!=mMultiChoose;
    }

    public final List<T> getChoose(){
        List<T> list=mMultiChoose;
        return list;
    }

    public final boolean multiChoose(T meta){
        List<T> list=mMultiChoose;
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

    public final boolean isChoose(NasFile meta){
        List<T> choose=mMultiChoose;
        return null!=meta&&null!=choose&&choose.contains(meta);
    }

}
