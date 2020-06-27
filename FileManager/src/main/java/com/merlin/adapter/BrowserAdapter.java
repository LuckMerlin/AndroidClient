package com.merlin.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.PageData;
import com.merlin.bean.FolderData;
import com.merlin.bean.Path;
import com.merlin.browser.Collector;
import com.merlin.browser.Thumbs;
import com.merlin.file.R;
import com.merlin.file.databinding.ItemListFileBinding;

import java.util.ArrayList;
import java.util.List;

public abstract class BrowserAdapter<T extends Path> extends PageAdapter<String, T>
        implements OnMoreLoadable, OnItemTouchResolver,OnItemSlideRemove{
    private final Thumbs mThumbs=new Thumbs();
    private Collector<Path> mMultiChoose;

    @Override
    protected Integer onResolveViewTypeLayoutId(int viewType) {
        return viewType==TYPE_DATA? R.layout.item_list_file:null;
    }

    public final boolean multiChoose(Object object,Boolean choose,String debug){
        Collector<Path> collector=null!=object?mMultiChoose:null;
        if (null!=collector){
            choose=null!=choose?choose:!collector.contains(object);
            if (choose){
                if (collector.contains(object)||!(object instanceof Path)){
                    return false;
                }
                Integer max=collector.getMax();
                if (null!=max&&max>=0&&collector.size()>=max){//Check if reached max
                    return false;
                }
                if (collector.add((Path) object,debug)){
                    notifyItemChanged(index(object),debug);
                    updateVisibleItems("After choose item changed.");
                    return true;
                }
            }else{
                int index=collector.indexOf(object);
                Path indexed= index>=0&&index<collector.size()?collector.get(index):null;
                if (null!=indexed&&collector.remove(indexed)){
                    notifyItemChanged(index,debug);
                    updateVisibleItems("After un choose item change.");
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean chooseAll(boolean choose,String debug){
        Collector<Path> collector=mMultiChoose;
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

    public final boolean setMultiCollector(Collector<Path> collector,String debug){
        Collector<Path> curr=mMultiChoose;
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
            Collector<Path> multiChoose=mMultiChoose;
            Integer max=null!=multiChoose?multiChoose.getMax():null;
            max=null!=max?max:0;
            if (null!=multiChoose){
                int chooseCount=multiChoose.size();
                boolean choose=multiChoose.contains(data);
                itemBinding.setIsMultiChoose(choose||max<0||chooseCount<max);
                itemBinding.setIsChoose(choose);
            }else{
                itemBinding.setIsMultiChoose(false);
            }
//            itemBinding.setThumbUrl(null!=data?data.isDirectory()?R.drawable.folder:mThumbs.getThumb(data.getPath(null)):null);
            itemBinding.setPosition(position);
        }
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }

    public final FolderData getLastFolder(){
        PageData pageData=getLastPage();
        return null!=pageData&&pageData instanceof FolderData ?(FolderData)pageData:null;
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


    @Override
    public Object onResolveItemTouch(RecyclerView recyclerView) {
        return new ItemTouchInterrupt(){
            @Override
            protected Integer onResolveSlide(RecyclerView.ViewHolder holder, RecyclerView.LayoutManager manager) {
                return new ItemSlideRemover().onResolveSlide(holder,manager);
            }
        };
    }
}
