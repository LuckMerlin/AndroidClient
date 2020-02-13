package com.merlin.adapter;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.bean.FileMeta;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemListFileBinding;
import com.merlin.debug.Debug;
import com.merlin.util.FileSize;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FileBrowserAdapter extends BaseAdapter<FileMeta, ItemListFileBinding>  {
    private List<FileMeta> mMultiChoose;

    @Override
    protected Integer onResolveNormalTypeLayoutId() {
        return R.layout.item_list_file;
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, ItemListFileBinding binding, int position, FileMeta data, @NonNull List<Object> payloads) {
        if (null!=binding&&null!=data){
            boolean multiChoose=null!=mMultiChoose;
            binding.setIsChoose(isChoose(data));
            binding.setIsMultiChoose(multiChoose);
            binding.setMeta(data);
            binding.setPosition(position);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            int count=data.getChildCount();
//            String sub=data.isDirectory()?"("+(count<=0?0:count)+")":" "+data.getExtension();
//            sub+=" "+ FileSize.formatSizeText(data.getLength());
//            sub+=" "+sdf.format(new Date((long)data.getModifyTime()));
//            sub+=" "+data.getPermissions();
//            setText(binding.itemListFileSub,sub,null);
        }
    }

    public boolean isChoose(FileMeta meta){
        List<FileMeta> choose=mMultiChoose;
        return null!=meta&&null!=choose&&choose.contains(meta);
    }

    public void multiMode(boolean entry){
        List current=mMultiChoose;
        if (null!=current){
            current.clear();
        }
        mMultiChoose=null;
        if (entry){
            mMultiChoose=new ArrayList<>(1);
        }
        notifyDataSetChanged();
    }

    public final boolean chooseAll(boolean choose){
        List<FileMeta> list = mMultiChoose;
        List<FileMeta> data = getData();
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

    public boolean multiChoose(FileMeta meta){
        List<FileMeta> list=mMultiChoose;
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

    public int getChooseCount(){
        List<FileMeta> list=mMultiChoose;
        return null!=list?list.size():0;
    }

    public List<FileMeta> getChoose(){
        List<FileMeta> list=mMultiChoose;
        return list;
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext());
    }
}
