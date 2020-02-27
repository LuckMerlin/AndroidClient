package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.client.R;
import com.merlin.retrofit.Retrofit;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;
import com.merlin.view.PopupWindow;
import com.merlin.view.Res;

import java.lang.ref.WeakReference;
import java.util.List;

public class BrowserModel<T extends FileMeta> implements Model.OnActivityResume, FileBrowserModel.OnBrowserModelChange, OnTapClick, OnLongClick, Model.OnActivityBackPress {
    public final static int MODE_INVALID=1211;
    public final static int MODE_NORMAL=1212;
    public final static int MODE_MULTI_CHOOSE=1213;
    public final static int MODE_COPY=1214;
    public final static int MODE_MOVE=1215;
    private ClientMeta mClientMeta;
    private int mMode=MODE_INVALID;
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>();
    private final ObservableField<String> mMultiChooseSummary=new ObservableField<>();
    private BrowserAdapter<T> mBrowserAdapter;
    private WeakReference<Context> mContext;
    private Object mProcessing;
    private PopupWindow mPopWindow;

    public interface OnPageDataLoad{
        void onPageDataLoad(BrowserModel model,FolderData folder);
    }

    public BrowserModel(Context context,ClientMeta meta){
        mClientMeta=meta;
        mContext=null!=context?new WeakReference<>(context):null;
        entryMode(MODE_NORMAL,"After model create.");
    }

    protected final boolean setAdapter(BrowserAdapter<T> adapter){
        if (null!=adapter){
            mBrowserAdapter=adapter;
//            return browserPath("","After model adapter set.");
            return true;
        }
        return false;
    }

    protected final boolean setMeta(ClientMeta meta,String debug){
        if (null!=meta){
            mClientMeta = meta;
            return true;
        }
        return false;
    }

    protected final boolean resetBrowserCurrentFolder(String debug){
        BrowserAdapter<T> adapter=mBrowserAdapter;
        return null!=adapter&&adapter.reset(debug);
    }

    public final FolderData getLastPage(){
        BrowserAdapter<T> adapter=mBrowserAdapter;
        FolderData meta=null!=adapter?adapter.getLastPage():null;
        return meta;
    }

    private final boolean refreshCurrentPath(String debug){
        FolderData meta=getLastPage();
        return browserPath(null!=meta?meta.getPath():null,debug);
    }

    protected boolean onBackIconPressed(View view,String debug){
        return browserParent(view,debug);
    }

    private boolean browserPath(String pathValue, String debug){
        BrowserAdapter adapter=mBrowserAdapter;
        if (null==pathValue){
            pathValue="";
            adapter.empty();
        }
        return null!=adapter&&adapter.loadPage(pathValue,debug);
    }

    private boolean browserParent(View view,String debug){
        FolderData current=getLastPage();
        String parent=null!=current?current.getParent():null;
        String curr=null!=current?current.getPath():null;
        if (null==parent||parent.length()<=0||(null!=curr&&curr.equals(parent))){
            Context context=null!=view?view.getContext():null;
            if (null!=context){
                Toast.makeText(context,R.string.alreadyArrivedRoot,Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return null!=parent&&browserPath(parent,debug);
    }

    public ClientMeta getClientMeta() {
        return mClientMeta;
    }

    public final int getMode() {
        return mMode;
    }

    public final ObservableField<String> getMultiChooseSummary() {
        return mMultiChooseSummary;
    }

    public final boolean isAllChoose(){
        return false;
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                return onSingleTapClick(view,resId,data);
        }
        return false;
    }

    protected boolean onSingleTapClick(View view, int resId, Object data) {
        switch (resId){
            default:
                if (null!=data&&data instanceof FileMeta ){
                    FileMeta file=(FileMeta)data;
                    if (isMode(MODE_MULTI_CHOOSE)) {
                        BrowserAdapter adapter=getBrowserAdapter();
                        adapter.multiChoose(file);
                        return (null!=adapter&&adapter.multiChoose(file))||true;
                    }else if(file.isAccessible()){
                        if (file.isDirectory()) {
                            browserPath(file.getPath(), "After directory click.");
                        } else{//Open file
                        }
                    }else{
                        Toast.makeText(view.getContext(),R.string.nonePermission,Toast.LENGTH_SHORT).show();
                    }
                }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        return false;
    }

    protected final boolean entryMode(int mode,String debug){
        if (!isMode(mode)){
            mProcessing=null;
            mMode=mode;
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

    protected final Object getProcessing() {
        return mProcessing;
    }

    protected final void setProcessing(Object processing,String debug) {
        this.mProcessing = processing;
    }

    protected final boolean isMode(int mode){
        return mode==mMode;
    }

    @Override
    public void onBrowserModelChanged(BrowserModel last, BrowserModel current) {
        if (null!=current&&current==this){
            refreshCurrentPath("After browser model changed.");
        }
    }

    private boolean refreshMultiChooseCount() {
        FolderData folder=getLastPage();
        int length=null!=folder?folder.getLength():0;
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
        refreshCurrentPath("After activity onResume.");
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        return !isMode(MODE_NORMAL)? entryMode(MODE_NORMAL,"After activity  back press."):browserParent(null,"After back pressed called.");
    }

    public BrowserAdapter<T> getBrowserAdapter() {
        return mBrowserAdapter;
    }

    protected final Context getViewContext(){
        WeakReference<Context> reference=mContext;
        return null!=reference?reference.get():null;
    }

    protected final boolean toast(Object id){
        Context context=null!=id?getViewContext():null;
        if (null!=context){
            Object text=id instanceof Integer?context.getString((Integer)id):id;
            if (text instanceof String){
                if (Looper.getMainLooper()!=Looper.myLooper()){
                    new Handler(Looper.getMainLooper()).post(()->Toast.makeText(context,(String)text,Toast.LENGTH_SHORT).show());
                }else{
                    Toast.makeText(context,(String)id,Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        return false;
    }

    protected final String getText(int textResId,Object ...args){
        Context context=getViewContext();
        return null!=context?context.getResources().getString(textResId,args):null;
    }

    protected final <T extends ViewDataBinding> T inflate(int layoutId, Res...res){
        return inflate(getViewContext(),layoutId,res);
    }

    protected final <T extends ViewDataBinding> T inflate(Context context,int layoutId, Res ...res){
        T binding=null!=context? DataBindingUtil.inflate(LayoutInflater.from(context),layoutId,null,false):null;
        if (null!=binding&&null!=res&&res.length>0){
            for (Res r:res) {
                Integer resourceId=null!=r?r.getResourceId():null;
                if (null!=resourceId){
                    binding.setVariable(resourceId,r.getArg());
                }
            }
        }
        return binding;
    }

    protected final boolean showAtLocationAsContext(View parent,ViewDataBinding binding) {
        return showAtLocation(parent,binding, PopupWindow.DISMISS_OUT_MASK|PopupWindow.DISMISS_INNER_MASK);
    }


    protected final boolean showAtLocation(View parent,ViewDataBinding binding,Integer dismissFlag){
        return null!=binding&&showAtLocation(parent,binding.getRoot(),dismissFlag);
    }

    protected final boolean showAtLocationAsContext(View parent,View root) {
        return showAtLocation(parent,root,PopupWindow.DISMISS_OUT_MASK|PopupWindow.DISMISS_INNER_MASK);
    }

    protected final boolean showAtLocation(View parent,View root,Integer dismissFlag) {
        return showAtLocation(parent,root, Gravity.CENTER,0,0,dismissFlag);
    }

    protected final boolean showAtLocation(View parent,ViewDataBinding binding,int gravity,int x,int y,Integer dismissFlag) {
        return null!=binding&&showAtLocation(parent,binding.getRoot(),gravity,x,y,dismissFlag);
    }
    protected final boolean showAtLocation(View parent,View root,int gravity,int x,int y,Integer dismissFlag){
        if (null!=root&&(null==root.getParent())){
            PopupWindow popupWindow=fetchPopWindow();
            if (null!=popupWindow) {
                popupWindow.setContentView(root);
                dismissFlag = null == dismissFlag ? PopupWindow.DISMISS_OUT_MASK | PopupWindow.DISMISS_INNER_MASK : dismissFlag;
                popupWindow.showAtLocation(parent, gravity, x, y, this, dismissFlag);
                return true;
            }
            return false;
        }
        return false;
    }

    protected final boolean showAsDropDown(View anchor,ViewDataBinding binding, int x, int y,Integer dismissFlag) {
        return null!=binding&&showAsDropDown(anchor,binding.getRoot(),x,y,null,dismissFlag);
    }

    protected final boolean showAsDropDown(View anchor,View root, int x, int y,Object interrupter,Integer dismissFlag){
        if (null!=anchor&&null!=root&&null==root.getParent()&&(anchor!=root)){
            PopupWindow popupWindow=fetchPopWindow();
            if (null!=popupWindow){
                popupWindow.setContentView(root);
                dismissFlag = null == dismissFlag ? PopupWindow.DISMISS_OUT_MASK | PopupWindow.DISMISS_INNER_MASK : dismissFlag;
                return popupWindow.showAsDropDown(anchor, x, y, null!=interrupter?interrupter:this, dismissFlag);
            }
            return false;
        }
        return false;
    }

    private PopupWindow fetchPopWindow(){
        PopupWindow currentPopWindow=mPopWindow;
        final PopupWindow popupWindow=null!=currentPopWindow?currentPopWindow:(mPopWindow=new PopupWindow(true,(window)->{
            PopupWindow curr=mPopWindow;
            if (null!=curr&&null!=window&&curr==window){
                mPopWindow=null;
            }
        }));
        return popupWindow;
    }

}
