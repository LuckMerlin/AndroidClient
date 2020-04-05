package com.merlin.adapter;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.browser.Collector;
import com.merlin.browser.Mode;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemListFileBinding;
import com.merlin.debug.Debug;
import com.merlin.util.Thumbs;

import java.util.ArrayList;
import java.util.List;

public abstract class BrowserAdapter<T extends FileMeta> extends PageAdapter<String, T> implements OnMoreLoadable{
    private final Thumbs mThumbs=new Thumbs();
    private Collector<T> mMultiChoose;

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA?R.layout.item_list_file:null;
    }

    public final boolean multiChoose(Object object,Boolean choose,String debug){
        Collector<T> collector=null!=object&&object instanceof  FileMeta?mMultiChoose:null;
        if (null!=collector){
            choose=null!=choose?choose:!collector.contains(object);
            int index=(choose?!collector.contains(object):collector.contains(object))?index(object):-1;
            if (index>=0&&(choose?collector.add((T) object,debug):collector.remove(object,debug))){
                notifyItemChanged(index);
                return true;
            }
        }
        return false;
    }

    public final boolean chooseAll(boolean choose,String debug){
        Collector<T> collector=mMultiChoose;
        if (null!=collector){
            collector.clear();
            ArrayList<T> all=getData();
            if (null!=all&&all.size()>0&&(!choose||collector.addAll(all))){
                updateVisibleItems("After choose all change "+choose+" "+(null!=debug?debug:"."));
                return true;
            }
        }
        return false;
    }

    public final boolean setMultiCollector(Collector<T> collector,String debug){
        Collector<T> curr=mMultiChoose;
        if ((null==curr&&null==collector)||(null!=curr&&null!=collector&&curr==collector)){
            return false;
        }
        mMultiChoose=collector;
        return updateVisibleItems("After collector change "+(null!=debug?debug:"."))||true;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, int viewType, ViewDataBinding binding, int position, T data, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, viewType, binding, position, data, payloads);
        if (null!=binding&&binding instanceof ItemListFileBinding){
            ItemListFileBinding itemBinding=(ItemListFileBinding)binding;
            itemBinding.setMeta(data);
            Collector<T> multiChoose=mMultiChoose;
            itemBinding.setIsMultiChoose(null!=multiChoose);
            itemBinding.setIsChoose(null!=multiChoose&&multiChoose.contains(data));
            itemBinding.setThumbUrl(null!=data?data.isDirectory()?R.drawable.folder:mThumbs.getThumb(data.getPath(true)):null);
            itemBinding.setPosition(position);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }

    public final FolderData getLastFolder(){
        PageData pageData=getLastPage();
        return null!=pageData&&pageData instanceof FolderData?(FolderData)pageData:null;
    }


    //    public final boolean chooseAll(boolean choose){
//        List<T> list = mMultiChoose;
//        List<T> data = getData();
//        int size = null != data ? data.size() : 0;
//        if (choose && (size > 0 &&(null==list||size != list.size()))) {
//            if (null==list){
//                mMultiChoose=list=new ArrayList<>();
//            }else{
//                list.clear();
//            }
//            list.addAll(data);
//            notifyDataSetChanged();
//            return true;
//        } else if (!choose && null!=list&&list.size()>0) {
//            list.clear();
//            notifyDataSetChanged();
//            return true;
//        }
//        return false;
//    }
//
//    public final boolean setMode(int mode){
//        List current=mMultiChoose;
//        if (null!=current){
//            current.clear();
//        }
//        mMultiChoose=null;
//        if (mode == FileBrowser.MODE_MULTI_CHOOSE){
//            mMultiChoose=new ArrayList<>(1);
//        }
//        notifyDataSetChanged();
//        return false;
//    }
//
//    public final int getChooseCount(){
//        List<T> list=mMultiChoose;
//        return null!=list?list.size():0;
//    }
//
//    public final boolean isMultiChoose(){
//        return null!=mMultiChoose;
//    }
//
//    public final List<T> getChoose(){
//        List<T> list=mMultiChoose;
//        return list;
//    }
//
//    public final boolean multiChoose(T meta){
//        List<T> list=mMultiChoose;
//        if (null!=meta){
//            if (null==list?(list=new ArrayList<>()).add(meta):
//                    (list.contains(meta)?list.remove(meta):list.add(meta))){
//                mMultiChoose=null!=list&&list.size()>0?list:null;
//                notifyItemChanged(index(meta));
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public final boolean isChoose(T meta){
//        List<T> choose=mMultiChoose;
//        return null!=meta&&null!=choose&&choose.contains(meta);
//    }

}
