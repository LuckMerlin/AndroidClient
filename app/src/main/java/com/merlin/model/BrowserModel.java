package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.databinding.ObservableField;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FolderData;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;

import java.util.List;

public class BrowserModel extends Model implements Model.OnActivityResume, FileBrowserModel.OnBrowserModelChange, OnTapClick, OnLongClick, Model.OnActivityBackPress {
    public final static int MODE_NORMAL=1212;
    public final static int MODE_MULTI_CHOOSE=1213;
    public final static int MODE_COPY=1214;
    public final static int MODE_MOVE=1215;
    private final ObservableField<ClientMeta> mClientMeta=new ObservableField<>();
    private final ObservableField<FolderData> mCurrentFolder=new ObservableField();
    private final ObservableField<Integer> mMode=new ObservableField<>();
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>();
    private final ObservableField<String> mMultiChooseSummary=new ObservableField<>();
    private final BrowserAdapter mBrowserAdapter;
    private Object mProcessing;

    public interface OnBrowserModelChange{
        void onBrowserModelChanged(Model last,Model current);
    }

    public BrowserModel(ClientMeta meta,BrowserAdapter adapter){
        mClientMeta.set(meta);
        mBrowserAdapter=adapter;
        entryMode(MODE_NORMAL,"After model create.");
    }


    protected ClientMeta getMeta() {
        ObservableField<ClientMeta> meta=mClientMeta;
        return null!=meta?meta.get():null;
    }

    public ObservableField<ClientMeta> getClientMeta() {
        return mClientMeta;
    }

    public final ObservableField<Integer> getMode() {
        return mMode;
    }

    public final ObservableField<FolderData> getCurrentFolder() {
        return mCurrentFolder;
    }

    public final ObservableField<String> getMultiChooseSummary() {
        return mMultiChooseSummary;
    }

    public final boolean isAllChoose(){
        return false;
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        return false;
    }

    protected final boolean entryMode(int mode,String debug){
        if (!isMode(mode)){
            mProcessing=null;
            mMode.set(mode);
            BrowserAdapter adapter=mBrowserAdapter;
            if (null!=adapter){
                adapter.setMode(mode);
            }
            switch (mode){
                case MODE_MULTI_CHOOSE:
                    return refreshMultiChooseCount();
                case MODE_COPY:
                    break;
                case MODE_MOVE:
                    break;
            }
            return true;
        }
        return false;
    }

    protected final boolean isMode(int mode){
        ObservableField<Integer> current=mMode;
        Integer curr=null!=current?current.get():null;
        return null!=curr&&mode==curr;
    }

    @Override
    public void onBrowserModelChanged(Model last, Model current) {

    }

    private boolean refreshMultiChooseCount() {
        FolderData folderMeta=mCurrentFolder.get();
        int length=null!=folderMeta?folderMeta.getLength():0;
        BrowserAdapter adapter=mBrowserAdapter;
        int count=null!=adapter?adapter.getChooseCount():0;
        mMultiChooseSummary.set(count<=0?"None selected(0/"+length+")":"Selected("+count+"/"+length+")");
        if (null!=adapter){
            List<FolderData> data=adapter.getData();
            int size=null!=data?data.size():0;
            mAllChoose.set(size==count&&size>0);
            return true;
        }
        return false;
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
//        refreshCurrentPath("After activity onResume.");
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        //        if (!isMode(MODE_NORMAL)){
//            return entryMode(MODE_NORMAL);
//        }
//        return browserParent("After back pressed called.");
        return false;
    }

    public BrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }
}
