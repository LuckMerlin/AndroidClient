package com.merlin.browser;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.browser.file.FileCopyProcess;
import com.browser.file.FileDeleteProcess;
import com.browser.file.FileMoveProcess;
import com.browser.file.FileProcess;
import com.browser.file.OnProcessUpdate;
import com.file.activity.PhotoPreviewActivity;
import com.merlin.adapter.BrowserAdapter;
import com.merlin.adapter.ItemTouchInterrupt;
import com.merlin.api.ApiMap;
import com.merlin.api.OnApiFinish;
import com.merlin.api.OnProcessChange;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Folder;
import com.merlin.bean.Path;
import com.merlin.browser.config.Config;
import com.merlin.click.OnTapClick;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.PopupWindow;
import com.merlin.file.R;
import com.merlin.file.databinding.LayoutFileModifyBinding;
import com.merlin.file.databinding.SingleEditTextBinding;
import com.merlin.retrofit.Retrofit;
import com.merlin.retrofit.RetrofitCanceler;
import com.merlin.server.Client;
import com.merlin.task.file.Cover;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;

public abstract class FileBrowser extends BrowserAdapter<Path> implements OnTapClick,Mode {
    private final Client mMeta;
    private final Callback mCallback;
    private PopupWindow mPopWindow;

    private final Retrofit mRetrofit=new Retrofit(){
        @Override
        protected String onResolveUrl(Class<?> cls, Executor callbackExecutor) {
                Context context=getAdapterContext();
                context=null!=context?context.getApplicationContext():null;
                android.app.Application application= null!=context&&context instanceof android.app.Application?(android.app.Application)context:null;
                com.merlin.browser.Application app=null!=application&&application instanceof
                        com.merlin.browser.Application?(com.merlin.browser.Application)application:null;
                Config config=null!=app?app.getConfig():null;
            return null!=config?config.getServerUri():null;
        }
    };

    private interface Api{
        @POST("/none")
        Observable<Reply<ApiMap<String, Path>>> noneRequest();
    }

    public FileBrowser(Client meta, Callback callback){
        mCallback=callback;
        mMeta=meta;
    }

    public interface Callback extends OnTapClick{
        void onFolderPageLoaded(String arg,PageData page, String debug);
    }

    public final Client getMeta() {
        return mMeta;
    }

    public final String getClientHost(){
        Client client=mMeta;
        return null!=client?client.getHost():null;
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        return false;
    }

    @Override
    protected void onPageLoadSucceed(String arg, PageData<Path> page, String debug) {
        super.onPageLoadSucceed(arg, page, debug);
        Callback callback=mCallback;
        if (null!=callback){
            callback.onFolderPageLoaded(arg,page,debug);
        }
    }

    public final boolean openPath(Path file,String debug){
        if (null!=file){
            if (file.isDirectory()){
                return browserPath(file.getPath(), "While open path "+(null!=debug?debug:"."));
            }
            if (file.isImage()){
                ArrayList<Path> list=new ArrayList<>(1);
                list.add(file);
                if(PhotoPreviewActivity.start(getContext(),list,0,debug)){
                    return true;
                }
            }
            return onOpenPath(file,debug);
        }
        return false;
    }

    public  final boolean browserParent(String debug){
        PageData current=getLastPage();
        Folder folder=null!=current&&current instanceof Folder?(Folder)current:null;
        Path folderPath=null!=folder?folder.getPath():null;
        if (null!=folderPath){
            String parent=folderPath.getParent();
            String curr=folderPath.getPath();
            if (null==parent||parent.length()<=0||(null!=curr&&curr.equals(parent))){
                return false;
            }
            return null!=parent&&browserPath(parent,debug);
        }
        return false;
    }

    public final boolean browserPath(String pathValue, String debug){
        return loadPage(null!=pathValue?pathValue:"",debug);
    }

    protected final String getClientHome(){
        Client meta=mMeta;
        return null!=meta?meta.getHome():null;
   }

    public final boolean showFileDetail(Object data,String debug){
        Path meta=null!=data&&data instanceof Path?(Path)data:null;
        String path=null!=meta?meta.getPath():null;
        if (null==path||path.length()<=0){
            toast(R.string.pathInvalid);
            return false;
        }
        return onShowPathDetail(meta,debug);
    }

    public final boolean setAsHome(Object data,String debug){
        Path folderPath=null!=data&&data instanceof Folder ?((Folder)data).getPath():null;
        String path=null!=folderPath?folderPath.getPath():null;
        return (null==path||path.length()<=0)?(toast(R.string.fail)&&false):onSetAsHome(path,(what,note, data1, arg)->{
            boolean succeed=null!=data1&&data1.isSuccess()&&data1.getWhat()==What.WHAT_SUCCEED;
            toast(succeed?R.string.succeed:(null!=note&&note.length()>0?note:R.string.fail));
        },debug);
    }

    public final boolean reboot(String debug){
        return onReboot(debug);
    }

    public final boolean renamePath(Path pathObj, int coverMode, String debug){
        final String path=null!=pathObj?pathObj.getPath():null;
        if (null!=path&&path.length()>0){
            final String name=pathObj.getName(true);
            final Dialog dialog=new Dialog(getViewContext());
            SingleEditTextBinding binding=inflate(R.layout.single_edit_text);
            return dialog.setContentView(binding,true).title(R.string.rename).left(R.string.sure).
                    right(R.string.cancel).show((View view,int clickCount, int resId, Object data)->{
                    switch (resId){
                        case R.string.sure:
                            String text=binding.singleET.getText().toString();
                            if (null==text||text.length()<=0){
                                toast(R.string.inputNotNull);
                            }else if (null!=name&&text.equals(name)){
                                toast(R.string.noneChanged);
                            }else{
                                dialog.dismiss();
                                onRenamePath(path,text,coverMode,(what, note, data1, arg)->{
                                    boolean succeed=what== What.WHAT_SUCCEED;
                                    if (succeed&&pathObj.applyNameChange(data1)){
                                        replace(pathObj,"After rename succeed.");
                                    }
                                    toast(note);
                                },debug);
                            }
                            break;
                    }
                return true; });
        }
        Debug.W(getClass(),"Can't rename file.path="+path);
        return false;
    }

    public final boolean createPath(boolean dir,String debug){
        Folder folderData=getLastFolder();
        final Path parentPath=null!=folderData?folderData.getPath():null;
        final String parent=null!=parentPath?parentPath.getPath():null;
        if (null==parent||parent.length()<=0){
            toast(R.string.pathNotExist);
            return false;
        }
        final Dialog dialog=new Dialog(getViewContext());
        return dialog.setContentView(R.layout.edit_text,true).title(dir?R.string.createFolder:R.string.createFile).left(R.string.sure)
                .right(R.string.cancel).show(( view, clickCount,  resId, data)->{
                    if (resId==R.string.sure){
                        String input=dialog.getViewText(R.id.edit_text,null);
                        if (null==input||input.length()<=0){
                            toast(R.string.inputNotNull);
                            return true;
                        }else{
                            dialog.dismiss();
                            return onCreatePath(dir, Cover.COVER_NONE,parent,input,(what, note, data2, arg)->{
                                toast(note);
                                if (what==What.WHAT_SUCCEED){
                                    resetAdapter("After path create succeed.");
                                }
                            },debug);
                        }
                    }
                    dialog.dismiss();
                    return true;
                });
    }

    public final boolean copyPaths(ArrayList<Path> files, Path folder, int coverMode, OnProcessUpdate callback, String debug){
        return process(new FileCopyProcess(getText(R.string.copy),files,folder,coverMode),callback);
    }

    public final boolean movePaths(ArrayList<Path> files, Path folder, int coverMode,OnProcessUpdate callback, String debug){
        return process(new FileMoveProcess(getText(R.string.move),files,folder,coverMode),callback);
    }

    public final boolean deletePath(ArrayList<Path> paths, OnProcessUpdate callback, String debug){
        return process(new FileDeleteProcess(getText(R.string.delete),paths),callback);
    }

    public final boolean process(FileProcess process,final OnProcessUpdate callback){
        final int length=null!=process?process.size():-1;
        if (length<=0){
            return toast(R.string.listEmpty)&&false;
        }
        String message=process.getMessage(getAdapterContext());
        final Dialog dialog=new Dialog(getAdapterContext());
        final LayoutFileModifyBinding binding=inflate(R.layout.layout_file_modify);
        Object title=process.getTitle();
        return dialog.create().setCancelable(false).setCanceledOnTouchOutside(false).title(title).
                message(getText(R.string.processSure,title,message)).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId, data)->{
                    switch (resId){
                        case R.string.sure:
                            if (process.isCanceled()){
                                dialog.dismiss();
                            }else {
                                dialog.setContentView(binding, false).title(title + "(" + process.getProcessedCount() + "/" + process.size() + "）").left(null).message(null);//Clean message
                                binding.setRight(null);
                                final OnProcessChange update = (OnProcessChange<Path>) (Float progress,String note,Path instant, List<Path> processed) -> {
                                    binding.setInstant(instant);
                                    dialog.title(title + "(" + process.getProcessedCount() + "/" + process.size() + "）");
                                    if (null != progress) {
                                        binding.setProgress(progress);
                                    }
                                };
                                if (null == call(prepare(Api.class, "http://None.request.Url", null).noneRequest().subscribeOn(Schedulers.io()).doOnSubscribe((disposable -> {
                                    if (!disposable.isDisposed()) {
                                        disposable.dispose();//Cancel none request
                                        Reply reply = process.onProcess(update, mRetrofit);
                                        binding.setReply(reply);
                                        dialog.right(R.string.finished);
                                    } else {
                                        post(() -> {
                                            dialog.dismiss();
                                            toast(R.string.canceled);
                                        }, 1000);
                                    }
                                })), null, null)) {
                                    dialog.dismiss();
                                    toast(R.string.fail);
                                }
                            }
                            break;
                        case R.string.cancel:
                            if (process.isProcessing()){
                                if (process.cancel(true,"After user cancel click.")) {
                                    dialog.right(R.string.sure);
                                    toast(R.string.canceled);
                                }
                            }else{
                                dialog.dismiss();
                            }
                            if (null!=callback) {
                                process.iterateAllUnFinish((Object key, Reply reply) -> {
                                    if (null == reply && null != key && key instanceof Path) {
                                        callback.onProcessUpdate(false,(Path)key);
                                    }
                                });
                            }
                            break;
                        case R.string.finished:
                            dialog.dismiss();
                            break;
                    }
                    return true; });
    }

    protected abstract boolean onReboot(String debug);
    protected abstract boolean onOpenPath(Path meta,String debug);
    protected abstract boolean onShowPathDetail(Path meta,String debug);
    protected abstract boolean onSetAsHome(String path,OnApiFinish<Reply<String>> finish,String debug);
    protected abstract boolean onCreatePath(boolean dir, int coverMode, String folder, String name, OnApiFinish<Reply<Path>> finish, String debug);
    protected abstract boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug);

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
        url=null!=url&&url.length()>0?url:getClientHost();
        return null!=retrofit?retrofit.prepare(cls,url,callbackExecutor):null;
    }

    protected final  <T> RetrofitCanceler call(Observable<T> observable, Scheduler subscribeOn,
                                               Scheduler observeOn, com.merlin.api.Callback...callbacks){
        Retrofit retrofit=mRetrofit;
        return null!=retrofit?retrofit.call(observable,subscribeOn,observeOn,callbacks):null;
    }

    protected final Context getContext(){
        return getViewContext();
    }

    protected final Context getViewContext(){
        return getAdapterContext();
    }

    protected final boolean invokeFinish(boolean succeed, Integer what, String note, OnApiFinish finish, Object data, Object arg){
        if (null!=finish){
            what=null!=what?what:What.WHAT_ERROR;
            Reply reply=null!=data?new Reply<>(succeed,what,note,data):null;
            finish.onApiFinish(what,note,reply,arg);
            return true;
        }
        return false;
    }

    @Override
    public void onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder, Remover remover) {
        switch (direction){
           case ItemTouchInterrupt.LEFT:
               Path path=null!=data&&data instanceof Path?(Path)data:null;
               if (null!=remover&&null!=path) {
                   ArrayList<Path> paths=new ArrayList<>(1);
                   paths.add(path);
                   deletePath(paths,(boolean succeed, Path childPath)-> {
                       if (!succeed&&null!=childPath&&childPath.equals(data)){
                           add(position,path, true, "After delete fail.");
                       }
                   },"While item slide remove.");
               }
               break;
           case ItemTouchInterrupt.RIGHT:
               Path openPath=null!=data&&data instanceof Path?(Path)data:null;
               if (null!=openPath) {
                   add(position,openPath, true, "While slide move to open.");
                   if(!openPath(openPath,"While slide move to open.")){
                       toast(getText(R.string.whatFail,getText(R.string.open)));
                   }
               }
               break;
       }
    }

}
