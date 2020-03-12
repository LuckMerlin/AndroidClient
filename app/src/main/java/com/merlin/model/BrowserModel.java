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
import com.merlin.api.ApiList;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FMode;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FModify;
import com.merlin.bean.FolderData;
import com.merlin.client.R;
import com.merlin.client.databinding.FileContextMenuBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.server.Retrofit;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;
import com.merlin.view.PopupWindow;
import com.merlin.view.Res;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BrowserModel<T extends FileMeta> implements Model.OnActivityResume, FileBrowserModel.OnBrowserClientChange, OnTapClick, OnLongClick, Model.OnActivityBackPress {
    public final static int MODE_INVALID=1211;
    public final static int MODE_NORMAL=1212;
    public final static int MODE_MULTI_CHOOSE=1213;
    public final static int MODE_COPY=1214;
    public final static int MODE_MOVE=1215;
    public final static int MODE_UPLOAD=1216;
    public final static int MODE_DOWNLOAD=1217;
    private ClientMeta mClientMeta;
    private int mMode=MODE_INVALID;
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>();
    private final ObservableField<String> mMultiChooseSummary=new ObservableField<>();
    private BrowserAdapter<T> mBrowserAdapter;
//    private final List<OnModeFinish> mModeFinish=new ArrayList<>();
    private WeakReference<Context> mContext;
//    private Object mProcessing;
    private PopupWindow mPopWindow;
    private final ClientCallback mCallback;

//    protected interface OnModeFinish{
//        void onModeFinish(int last,int current);
//    }

    public BrowserModel(Context context,ClientMeta meta,ClientCallback callback){
        mClientMeta=meta;
        mCallback=callback;
        mContext=null!=context?new WeakReference<>(context):null;
        entryMode(MODE_UPLOAD,"After model create.");
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

    protected final boolean startActivity(Intent intent){
            Context context=getViewContext();
            if (null!=context&&null!=intent){
                context.startActivity(intent);
                return true;
            }
            return false;
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
        if (null==adapter){
            return false;
        }
        if (null==pathValue){
            pathValue="";
            adapter.empty();
        }
        return adapter.loadPage(pathValue,debug);
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
            case 2:
                switch (resId){
                    default:
                        if (null!=data&&data instanceof FileMeta){
                            return onShowFileContextMenu(view,(FileMeta)data,"After 2 tap click.");
                        }
                }
                break;
        }
        return false;
    }

    protected boolean onSingleTapClick(View view, int resId, Object data) {
        switch (resId){
            case R.string.detail:
                return null!=data&&data instanceof FileMeta &&showFileDetail((FileMeta)data,"After detail tap click.");
            case R.string.open:
                return null!=data&&open(data,"After open tap click.");
            case R.string.createFile:
                return createFile(false,"After create file tap click.");
            case R.string.createFolder:
                return createFile(true,"After create folder tap click.");
            case R.string.rename:
                return null!=data&&data instanceof FileMeta &&renameFile((FileMeta)data, FModify.MODE_NONE,"After rename tap click.");
            case R.string.delete:
                Collection list=null;
                if (null!=data){
                    if (data instanceof FileMeta){
                        (list=new ArrayList<>(1)).add((FileMeta)data);
                    }else if (data instanceof Collection){
                        list=(Collection)data;
                    }
                }
                return null!=list&&list.size()>0&&deleteFile(list,"After delete tap click.");
            default:
                if (null!=data&&data instanceof FileMeta){
                    FileMeta file=(FileMeta)data;
                    if (isMode(MODE_MULTI_CHOOSE)) {
                        BrowserAdapter adapter=getBrowserAdapter();
                        adapter.multiChoose(file);
                        return (null!=adapter&&adapter.multiChoose(file))||true;
                    }else if(file.isAccessible()){
                        if (file.isDirectory()) {
                            return browserPath(file.getPath(), "After directory click.");
                        } else{//Open file
                            return open(data,"After item tap click.");
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
            int last=mMode;
            mMode=mode;
            setProcessing(null,"While mode entry.");
            ClientCallback callback=mCallback;
            if (null!=callback){
                callback.onBrowserModeChange(this,last,mode);
            }
            BrowserAdapter adapter=mBrowserAdapter;
            if (null!=adapter){
                adapter.setMode(mode);
            }
            switch (mode){
                case MODE_MULTI_CHOOSE:
                    return refreshMultiChooseCount();
                case MODE_COPY:
                case MODE_UPLOAD:
                    break;
                case MODE_MOVE:
                    break;
            }
            return true;
        }
        return false;
    }


    protected final boolean isMode(int mode){
        return mode==mMode;
    }

    protected boolean setProcessing(Object object,String debug){
         final ClientCallback callback=mCallback;
        return null!=callback&&callback.onProcessSet(object,debug);
    }

    @Override
    public void onBrowserClientChanged(BrowserModel last, BrowserModel current) {
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

    protected final Collection<Object> getAllClients(){
        ClientCallback callback=mCallback;
        return null!=callback?callback.getAllClients():null;
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
                    Toast.makeText(context,(String)text,Toast.LENGTH_SHORT).show();
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

    private boolean onShowFileContextMenu(View view,FileMeta meta,String debug){
        if (null!=view&&null!=meta){
            FileContextMenuBinding binding= DataBindingUtil.inflate(LayoutInflater.from(view.getContext()), R.layout.file_context_menu,null,false);
            if (null!=binding){
                binding.setFile(meta);
                return showAtLocationAsContext(view,binding);
            }
        }
        return false;
    }

    protected abstract boolean onShowFileDetail(FileMeta meta,String debug);
    protected abstract boolean onOpenFile(FileMeta meta,String debug);
    protected abstract boolean onRenameFile(String path, String name, int mode, OnApiFinish<Reply<FModify>> finish, String debug);
    protected abstract boolean onDeleteFile(List<String> files, OnApiFinish<Reply<ApiList<String>>> finish, String debug);
    protected abstract boolean onCreateFile(boolean dir, int mode, String folder, String name, OnApiFinish<Reply<FModify>> finish, String debug);
    private boolean deleteFile(Collection files,String debug){
        final int length=null!=files?files.size():-1;
        if (length>0){
            Dialog dialog=new Dialog(getViewContext());
            Object firstObj=files.iterator().next();
            FileMeta first=null!=firstObj&&firstObj instanceof FileMeta?((FileMeta)firstObj):null;
            String name=null!=first?first.getName(false):null;
            String message=""+(length==1?(null!=name?(""+getText(first.isDirectory()? R.string.folder:R.string.file)+" "+name):""):getText(R.string.items,length));
            return dialog.create().title(R.string.delete).message(getText(R.string.deleteSure,message)).left(R.string.sure).right(R.string.cancel).show((view, clickCount,  resId, data)->{
                dialog.dismiss();
                if (resId ==R.string.sure){
                    final List<String> paths=new ArrayList<>();
                    Map<String, FileMeta> map=new HashMap<>(length);
                    for (Object meta:files) {
                        String path=null!=meta&&meta instanceof FileMeta?((FileMeta)meta).getPath():null;
                        if (null!=path&&path.length()>0){
                            paths.add(path);
                            map.put(path,((FileMeta)meta));
                        }
                    }
                    return null!=paths&&paths.size()>0&&onDeleteFile(paths,(what, note, data3, arg)->{
                        toast(note);
                        if (what==What.WHAT_SUCCEED){
                            List<String> deletedPaths=null!=data3?data3.getData():null;
                            BrowserAdapter adapter=getBrowserAdapter();
                            int size=null!=deletedPaths&&null!=adapter?deletedPaths.size():-1;
                            if (size>0){
                                List<FileMeta> deleted=new ArrayList<>(size);
                                for (String  path:deletedPaths) {
                                    FileMeta child=null!=path?map.get(path):null;
                                    if (null!=child){
                                        deleted.add(child);
                                    }
                                }
                                adapter.remove(deleted,debug);
                            }
                        }
                    },debug);
                }
                return true;
            },false);
        }
        Debug.D(getClass(),"Can't delete file.");
        return false;
    }
    private boolean createFile(boolean dir,String debug){
        FolderData folderMeta=getLastPage();
        final String parent=null!=folderMeta?folderMeta.getPath():null;
        if (null==parent||parent.length()<=0){
            toast(R.string.pathNotExist);
            return false;
        }
        final Dialog dialog=new Dialog(getViewContext());
        return dialog.setContentView(R.layout.edit_text).title(dir?R.string.createFolder:R.string.createFile).left(R.string.sure)
                .right(R.string.cancel).show(( view, clickCount,  resId, data)->{
                    if (resId==R.string.sure){
                        String input=dialog.getViewText(R.id.edit_text,null);
                        if (null==input||input.length()<=0){
                            toast(R.string.inputNotNull);
                            return true;
                        }else{
                            dialog.dismiss();
                            return onCreateFile(dir, FMode.MODE_NONE,parent,input,(what, note, data2, arg)->{
                                if(what== What.WHAT_SUCCEED){
                                    resetBrowserCurrentFolder("After file create succeed.");
                                }
                                toast(note);
                            },debug);
                        }
                    }
                    dialog.dismiss();
                    return true;
                });
    }
    private boolean renameFile(FileMeta meta,int mode,String debug){
        final String path=null!=meta?meta.getPath():null;
        if (null!=path&&path.length()>0){
            final String name=meta.getName(true);
            return new SingleInputDialog(getViewContext()).show(R.string.rename,(dlg, text)->{
                if (null==text||text.length()<=0){
                    toast(R.string.inputNotNull);
                }else if (null!=name&&text.equals(name)){
                    toast(R.string.noneChanged);
                }else{
                    if (null!=dlg){
                        dlg.dismiss();
                    }
                    onRenameFile(path,text,mode,(what, note, data, arg)->{
                        boolean succeed=what==What.WHAT_SUCCEED;
                        toast(note);
                        BrowserAdapter adapter=getBrowserAdapter();
                        FModify modify=succeed&&null!=data&&null!=adapter?data.getData():null;
                        if (succeed&&null!=modify&&null!=adapter){
                            adapter.renamePath(meta,modify);
                        }
                    },debug);
                }
            });
        }
        Debug.W(getClass(),"Can't rename file.path="+path);
        return false;
    }

    private boolean open(Object data,String debug){
        return null!=data&&data instanceof FileMeta&&onOpenFile((FileMeta)data,debug);
    }
    private boolean showFileDetail(FileMeta meta,String debug){
        String path=null!=meta?meta.getPath():null;
        if (null==path||path.length()<=0){
            toast(R.string.pathInvalid);
            return false;
        }
        return onShowFileDetail(meta,debug);
    }
}
