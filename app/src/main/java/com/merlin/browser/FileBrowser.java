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

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.ApiMap;
import com.merlin.api.Canceler;
import com.merlin.api.CoverMode;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.Document;
import com.merlin.bean.FolderData;
import com.merlin.bean.Path;
import com.merlin.client.R;
import com.merlin.client.databinding.LayoutFileModifyBinding;
import com.merlin.client.databinding.SingleEditTextBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.server.Retrofit;
import com.merlin.server.RetrofitCanceler;
import com.merlin.util.Layout;
import com.merlin.view.Clicker;
import com.merlin.view.OnTapClick;
import com.merlin.view.PopupWindow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;

public abstract class FileBrowser extends BrowserAdapter implements OnTapClick,Mode {
    private final ClientMeta mMeta;
    private final Callback mCallback;
    private PopupWindow mPopWindow;
    private final Retrofit mRetrofit=new Retrofit();

    private interface Api{
        @POST("/none")
        Observable<Reply<ApiMap<String,Path>>> noneRequest();
    }

    public FileBrowser(ClientMeta meta,Callback callback){
        mCallback=callback;
        mMeta=meta;
    }

    public interface Callback extends OnTapClick{
        void onFolderPageLoaded(PageData page, String debug);
    }

    public final ClientMeta getMeta() {
        return mMeta;
    }

    protected void onModeChange(int last,int mode){
        //Do nothing
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
//                    case R.string.detail:
//                        return showFileDetail(view,data,"After detail tap click.");
//                    case R.string.createFile:
//                        return createPath(false,"After create file tap click.");
//                    case R.string.createFolder:
//                        return createPath(true,"After create folder tap click.");
//                    case R.string.setAsHome:
//                        return setAsHome(view,data,"After set as home tap click.");
//                    case R.string.delete:
//                        return deletePath(data,"After tap click.");
//                    case R.string.rename:
//                        return null!=data&&data instanceof FileMeta &&renameFile((FileMeta)data, CoverMode.NONE,"After rename tap click.");
                }
                break;
//            case 2:
//                switch (resId){
//                    default:
//                    if (null!=data&&data instanceof FileMeta){
//                        return onShowFileContextMenu(view,(FileMeta)data,"After 2 tap click.");
//                    }
//                }
//                break;
        }
        return false;
    }

    public final boolean openPath(Document file,String debug){
        return null!=file&&(file.isDirectory()?browserPath(file.getPath(null),
                "While open path "+(null!=debug?debug:".")):onOpenPath(file,debug));
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

    public final boolean browserPath(String pathValue, String debug){
        return loadPage(null!=pathValue?pathValue:"",debug);
    }

    protected final String getClientRoot(){
        ClientMeta meta=mMeta;
        return null!=meta?meta.getRoot():null;
   }

    public final boolean showFileDetail(Object data,String debug){
        Document meta=null!=data&&data instanceof Document?(Document)data:null;
        String path=null!=meta?meta.getPath(null):null;
        if (null==path||path.length()<=0){
            toast(R.string.pathInvalid);
            return false;
        }
        return onShowPathDetail(meta,debug);
    }

    public final boolean setAsHome(Object data,String debug){
        String path=null!=data&&data instanceof FolderData?((FolderData)data).getPath():null;
        return (null==path||path.length()<=0)?(toast(R.string.fail)&&false):onSetAsHome(path,(what,note, data1, arg)->{
            boolean succeed=null!=data1&&data1.isSuccess()&&data1.getWhat()==What.WHAT_SUCCEED;
            toast(succeed?R.string.succeed:(null!=note&&note.length()>0?note:R.string.fail));
        },debug);
    }

    public final boolean reboot(String debug){
        return onReboot(debug);
    }

    public final boolean renamePath(Document meta, int coverMode, String debug){
        final String path=null!=meta?meta.getPath(null):null;
        if (null!=path&&path.length()>0){
            final String name=meta.getName(true);
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
                                    if (succeed&&null!=data&&meta.applyPathChange(data1)){
                                        replace(meta,"After rename succeed.");
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
        FolderData folderData=getLastFolder();
        final String parent=null!=folderData?folderData.getPath():null;
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
                            return onCreatePath(dir,CoverMode.NONE,parent,input,(what, note, data2, arg)->{
                                toast(note);
                            },debug);
                        }
                    }
                    dialog.dismiss();
                    return true;
                });
    }

    public final boolean copyPaths(ArrayList<Document> files,String folder,int coverMode,String debug){
        FileProcess process=createPathsProcess(R.string.copy,files,folder,coverMode,debug);
        return null!=process?process(process):(toast(R.string.fail)&&false);
    }

    public final boolean movePaths(ArrayList<Document> files, String folder, int coverMode, String debug){
        FileProcess process=createPathsProcess(R.string.move,files,folder,coverMode,debug);
        return null!=process?process(process):(toast(R.string.fail)&&false);
    }

    public final boolean deletePaths(ArrayList<Document> paths,String debug){
        FileProcess process=createPathsProcess(R.string.delete,paths,null,null,debug);
        return null!=process?process(process):(toast(R.string.fail)&&false);
    }

    public final boolean deletePath(Object data,String debug){
        if (null==data||!(data instanceof File)){
            return toast(R.string.pathInvalid)&&false;
        }
        ArrayList<Document> list=new ArrayList<>();
        list.add((Document)data);
        return deletePaths(list, debug);
    }

    public final boolean process(FileProcess process){
        final int length=null!=process?process.size():-1;
        if (length<=0){
            return toast(R.string.listEmpty)&&false;
        }
        String message=process.getMessage(getAdapterContext());
        final Dialog dialog=new Dialog(getAdapterContext());
        final LayoutFileModifyBinding binding=inflate(R.layout.layout_file_modify);
        final Canceler[] cancelers=new Canceler[1];
        final FileProcess.Interrupt[] interrupts=new FileProcess.Interrupt[1];
        Object title=process.getTitle();
        return dialog.create().setCancelable(false).setCanceledOnTouchOutside(false).title(title).message(getText(R.string.processSure,title,message)).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId, data)->{
                    FileProcess.Interrupt interrupt=null!=interrupts&&interrupts.length>0?interrupts[0]:null;
                    if (null!=interrupt){
                        interrupts[0]=null;
                        return interrupt.setWhat(resId)||true;
                    }
                    switch (resId){
                        case R.string.sure:
                             dialog.setContentView(binding,false).left(null).message("");//Clean message
                             final OnApiFinish apiFinish=(int what, String note, Object apiData, Object arg) ->{
                                    if (what==What.WHAT_SUCCEED){
                                        post(()->dialog.dismiss(),1000);
                                    }
                             };
                             final FileProcess.OnProcessUpdate update=(what,note,from,to,arg)-> {
                                   switch (what){
                                       case What.WHAT_SUCCEED://Get through
                                            if (null!=arg){

                                            }
                                       case What.WHAT_DOING:
                                       case What.WHAT_FAIL_UNKNOWN:
                                           dialog.message(null).title(note);
                                           binding.setLeft(from);
                                           binding.setRight(to);
                                           break;
                                       case What.WHAT_INTERRUPT:
                                           if (null!=arg&&arg instanceof FileProcess.Interrupt){
                                               interrupts[0]=(FileProcess.Interrupt)arg;
                                               dialog.message(note).left(R.string.sure).right(R.string.cancel);
                                           }
                                           break;
                                   }
                             };
                            if (null==call(prepare(Api.class, "http://None.request.Url", null).noneRequest().subscribeOn(Schedulers.io()).doOnSubscribe((disposable -> {
                                if (!disposable.isDisposed()){
                                    disposable.dispose();//Cancel none request
                                    Canceler canceler=cancelers[0]=process.onProcess(update, apiFinish,mRetrofit);
                                    if (null==canceler){//Process fail?Dismiss dialog
                                        post(()->dialog.dismiss(),1000);
                                    }
                                }else {
                                    post(()->dialog.dismiss(),1000);
                                }
                            })), null, null)){
                                dialog.dismiss();
                            }
                            break;
                        case R.string.cancel:
                            Canceler canceler=null!=cancelers&&cancelers.length>0?cancelers[0]:null;
                            if (null==canceler||canceler.cancel(true,"While cancel tap.")){
                                dialog.dismiss();//Dismiss dialog while cancel succeed
                            }
                            break;
                    }
                    return true; });
    }

    protected abstract boolean onReboot(String debug);
    protected abstract boolean onOpenPath(Document meta,String debug);
    protected abstract boolean onShowPathDetail(Document meta,String debug);
    protected abstract boolean onSetAsHome(String path,OnApiFinish<Reply<String>> finish,String debug);
    protected abstract boolean onCreatePath(boolean dir,int coverMode,String folder,String name,OnApiFinish<Reply<Path>> finish,String debug);
    protected abstract boolean onRenamePath(String path, String name, int coverMode,OnApiFinish<Reply<Path>> finish,String debug);
    protected FileProcess onCreatePathsProcess(int mode,ArrayList<Document> paths,String folder,Integer coverMode,String debug){
        //Do nothing
        return null;
    }

    private FileProcess createPathsProcess(int mode,ArrayList<Document> paths,String folder,Integer coverMode,String debug){
        FileProcess process=onCreatePathsProcess(mode,paths,folder,coverMode,debug);
        if (null==process){
            switch (mode) {
                case R.string.delete:
                    return new FileDeleteProcess(mode,paths);
                case R.string.move:
                    return new FileMoveProcess(mode,paths,folder,coverMode);
                case R.string.copy:
                    return new FileCopyProcess(mode,paths,folder,coverMode);
                case R.string.download:
                    return new FileDownloadProcess(mode,paths,folder,coverMode);
                case R.string.upload:
                    return new FileUploadProcess(mode,paths,folder,coverMode);
            }
        }
        return process;
    }

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
        return null!=retrofit?retrofit.prepare(cls,url,callbackExecutor):null;
    }

    protected final  <T> RetrofitCanceler call(Observable<T> observable, Scheduler subscribeOn,
                                               Scheduler observeOn, com.merlin.api.Callback...callbacks){
        Retrofit retrofit=mRetrofit;
        return null!=retrofit?retrofit.call(observable,subscribeOn,observeOn,callbacks):null;
    }

    protected final Context getViewContext(){
        return getAdapterContext();
    }

    protected final boolean invokeFinish(boolean succeed, Integer what, String note, OnApiFinish finish, Object data, Object arg){
        if (null!=finish){
            what=null!=what?what:What.WHAT_ERROR_UNKNOWN;
            Reply reply=null!=data?new Reply<>(succeed,what,note,data):null;
            finish.onApiFinish(what,note,reply,arg);
            return true;
        }
        return false;
    }

}
