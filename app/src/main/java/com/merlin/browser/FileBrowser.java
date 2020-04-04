package com.merlin.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.Callback;
import com.merlin.api.CoverMode;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FModify;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.Path;
import com.merlin.client.R;
import com.merlin.client.databinding.FileContextMenuBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.server.Retrofit;
import com.merlin.server.RetrofitCanceler;
import com.merlin.view.OnTapClick;
import com.merlin.view.PopupWindow;

import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

public abstract class FileBrowser extends BrowserAdapter implements OnTapClick,Mode {
    private final ClientMeta mMeta;
    private final Context mContext;
    private final Callback mCallback;
    private PopupWindow mPopWindow;
    private Retrofit mRetrofit;

    public FileBrowser(Context context, ClientMeta meta,Callback callback){
        mCallback=callback;
        mContext=context;
        mMeta=meta;
    }

    public interface Callback extends OnTapClick{
        void onFolderPageLoaded(PageData page, String debug);
        int getMode();
    }

    public final ClientMeta getMeta() {
        return mMeta;
    }

    @Override
    protected final void onPageLoadSucceed(PageData page, String debug) {
        super.onPageLoadSucceed(page, debug);
        Callback callback=mCallback;
        if (null!=callback){
            callback.onFolderPageLoaded(page,debug);
        }
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount) {
            case 1:
                switch (resId) {
                    case R.string.detail:
                        return showFileDetail(view,data,"After detail tap click.");
                    case R.string.setAsHome:
                        return setAsHome(view,data,"After set as home tap click.");
                    case R.string.rename:
                        return null!=data&&data instanceof FileMeta &&renameFile((FileMeta)data, CoverMode.NONE,"After rename tap click.");
                    default:
                        if (null != data && data instanceof FileMeta) {
                            FileMeta file = (FileMeta) data;
                            if (isMode(MODE_MULTI_CHOOSE)) {
                                return multiChoose(file, "After tap click.") || true;
                            } else if (file.isAccessible()) {
                                return file.isDirectory()? browserPath(file.getPath(false),
                                        "After directory click."):openFile(file, "After item tap click.");
                            } else {
                                toast(R.string.nonePermission);
                            }
                        }
                }
                break;
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

    protected final boolean isMode(int ... modes){
        if (null!=modes&&modes.length>0){
            int curr=getMode();
            for (int mode:modes){
                if (mode==curr){
                    return true;
                }
            }
        }
        return false;
    }

    protected final int getMode(){
        Callback callback= mCallback;
        return null!=callback?callback.getMode():MODE_NORMAL;
    }

    protected boolean openFile(FileMeta file,String debug){
        //Do nothing
        return false;
    }

    private boolean onShowFileContextMenu(View view,FileMeta meta,String debug){
        if (null!=view&&null!=meta){
            FileContextMenuBinding binding= DataBindingUtil.inflate(LayoutInflater.from
                    (view.getContext()), R.layout.file_context_menu,null,false);
            if (null!=binding){
                binding.setFile(meta);
                return showAtLocationAsContext(view,binding);
            }
        }
        return false;
    }

    public  final boolean browserParent(String debug){
        PageData current=getLastPage();
        if (null!=current&&current instanceof FolderData){
            FolderData folder=(FolderData)current;
            String parent=folder.getParent();
            String curr=folder.getPath();
            if (null==parent||parent.length()<=0||(null!=curr&&curr.equals(parent))){
                return false;
            }
            return null!=parent&&browserPath(parent,debug);
        }
        return false;
    }

    private boolean browserPath(String pathValue, String debug){
        return loadPage(null!=pathValue?pathValue:"",debug);
    }

    protected final String getText(int textResId, Object ...args){
        Context context=getViewContext();
        return null!=context?context.getResources().getString(textResId,args):null;
   }

   protected final boolean toast(Object text){
        Context context=null!=text?mContext:null;
        text=null!=context?text instanceof Integer?getText((Integer)text):text:null;
        if (null!=text&&text instanceof CharSequence){
            Toast.makeText(context,(CharSequence)text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
   }

   protected final Context getViewContext(){
        return mContext;
   }

   protected final String getClientRoot(){
        ClientMeta meta=mMeta;
        return null!=meta?meta.getRoot():null;
   }

    private boolean showFileDetail(View view,Object data,String debug){
        FileMeta meta=null!=data&&data instanceof FileMeta?(FileMeta)data:null;
        String path=null!=meta?meta.getPath(false):null;
        if (null==path||path.length()<=0){
            toast(R.string.pathInvalid);
            return false;
        }
        return onShowFileDetail(view,meta,debug);
    }

    private boolean setAsHome(View view,Object data,String debug){
        String path=null!=data&&data instanceof FolderData?((FolderData)data).getPath():null;
        return (null==path||path.length()<=0)?(toast(R.string.fail)&&false):onSetAsHome(view,path,debug);
    }

    private boolean renameFile(FileMeta meta,int coverMode,String debug){
        final String path=null!=meta?meta.getPath(false):null;
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
                    onRenameFile(path,text,coverMode,(what, note, data, arg)->{
                        boolean succeed=what== What.WHAT_SUCCEED;
                        toast(note);
                        if (succeed&&null!=data&&meta.applyChange(data)){
                            replace(meta,"After rename succeed.");
                        }
                    },debug);
                }
            });
        }
        Debug.W(getClass(),"Can't rename file.path="+path);
        return false;
    }

    protected abstract boolean onShowFileDetail(View view,FileMeta meta,String debug);
    protected abstract boolean onSetAsHome(View view,String path,String debug);
    protected abstract boolean onRenameFile(String path, String name, int coverMode,OnApiFinish<Reply<Path>> finish,String debug);
//    protected abstract boolean onDeleteFile(List<String> files, OnApiFinish<Reply<ApiList<String>>> finish, String debug);

    protected final <T extends ViewDataBinding> T inflate(int layoutId){
        return inflate(getViewContext(),layoutId);
    }

    protected final <T extends ViewDataBinding> T inflate(Context context,int layoutId){
        T binding=null!=context? DataBindingUtil.inflate(LayoutInflater.from(context),layoutId,null,false):null;
        return binding;
    }

    protected final boolean showAtLocationAsContext(View parent, ViewDataBinding binding) {
        return showAtLocation(parent,binding, PopupWindow.DISMISS_OUT_MASK|PopupWindow.DISMISS_INNER_MASK);
    }

    protected final boolean showAtLocation(View parent,ViewDataBinding binding,Integer dismissFlag){
        return null!=binding&&showAtLocation(parent,binding.getRoot(),dismissFlag);
    }

    protected final boolean showAtLocation(View parent,View root,Integer dismissFlag) {
        return showAtLocation(parent,root, Gravity.CENTER,0,0,dismissFlag);
    }

    protected final boolean showAtLocation(View parent,View root,int gravity,int x,int y,Integer dismissFlag){
        if (null!=root&&(null==root.getParent())){
            PopupWindow popupWindow=fetchPopWindow();
            if (null!=popupWindow) {
                popupWindow.setContentView(root);
                dismissFlag = null == dismissFlag ? PopupWindow.DISMISS_OUT_MASK | PopupWindow.DISMISS_INNER_MASK : dismissFlag;
                popupWindow.showAtLocation(parent, gravity, x, y, mCallback, dismissFlag);
                return true;
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

    protected final boolean post(Runnable runnable,int delay){
        if (null!=runnable){
            delay=delay<=0?0:delay;
            View view=getRecyclerView();
            if (null!=view){
                return view.postDelayed(runnable,delay);
            }else{
                new Handler(Looper.getMainLooper()).postDelayed(runnable,delay);
            }
        }
        return false;
    }

    protected final boolean startActivity(Intent intent){
            Context context=getViewContext();
            if (null!=context&&null!=intent){
                context.startActivity(intent);
                return true;
            }
            return false;
    }

    protected final  <T> T prepare(Class<T>  cls, String url, Executor callbackExecutor){
        Retrofit retrofit=mRetrofit;
        return (null!=retrofit?retrofit:(mRetrofit=new Retrofit())).prepare(cls,url,callbackExecutor);
    }

    protected final  <T> RetrofitCanceler call(Observable<T> observable, Scheduler subscribeOn,
                                               Scheduler observeOn, com.merlin.api.Callback...callbacks){
        Retrofit retrofit=mRetrofit;
        return (null!=retrofit?retrofit:(mRetrofit=new Retrofit())).call(observable,subscribeOn,observeOn,callbacks);
    }
}
