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

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.ApiMap;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.FolderData;
import com.merlin.bean.Path;
import com.merlin.click.OnTapClick;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.PopupWindow;
import com.merlin.file.R;
import com.merlin.file.databinding.LayoutFileModifyBinding;
import com.merlin.file.databinding.SingleEditTextBinding;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;
import com.merlin.retrofit.RetrofitCanceler;
import com.merlin.server.Client;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.http.POST;

public abstract class FileBrowser extends BrowserAdapter<Path> implements OnTapClick,Mode {
    private final Client mMeta;
    private final Callback mCallback;
    private PopupWindow mPopWindow;
    private final Retrofit mRetrofit=new Retrofit();

    private interface Api{
        @POST("/none")
        Observable<Reply<ApiMap<String, Path>>> noneRequest();
    }

    public FileBrowser(Client meta, Callback callback){
        mCallback=callback;
        mMeta=meta;
    }

    public interface Callback extends OnTapClick{
        void onFolderPageLoaded(PageData page, String debug);
    }

    public final Client getMeta() {
        return mMeta;
    }

    public final String getClientHost(){
        Client client=mMeta;
        return null!=client?client.getHost():null;
    }

    protected void onModeChange(int last,int mode){
        //Do nothing
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        return false;
    }

    @Override
    protected final void onPageLoadSucceed(PageData page, String debug) {
        super.onPageLoadSucceed(page, debug);
        Callback callback=mCallback;
        if (null!=callback){
            callback.onFolderPageLoaded(page,debug);
        }
    }

    public final boolean openPath(Path file,String debug){
        return null!=file&&(file.isDirectory()?browserPath(file.getPath(),
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
        String path=null!=data&&data instanceof FolderData?((FolderData)data).getPath():null;
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
                            return onCreatePath(dir, CoverMode.NONE,parent,input,(what, note, data2, arg)->{
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

    protected FileProcess onCreateFileProcess(int mode,ArrayList<Path> files,String target,Integer coverMode,String debug){
        switch (mode){
            case MODE_DELETE:
                return new FileDeleteProcess(R.string.delete,files);
        }
        return null;
    }

    public final boolean copyPaths(ArrayList<Path> files,String folder,int coverMode,String debug){
        FileProcess process=onCreateFileProcess(Mode.MODE_COPY,files,folder,coverMode,debug);
        return null!=process?process(process,null):(toast(R.string.fail)&&false);
    }

    public final boolean movePaths(ArrayList<Path> files, String folder, int coverMode, String debug){
        FileProcess process=onCreateFileProcess(Mode.MODE_MOVE,files,folder,coverMode,debug);
        return null!=process?process(process,null):(toast(R.string.fail)&&false);
    }

    public final boolean deletePath(ArrayList<Path> paths,OnApiFinish<Reply<Path>> finish,String debug){
        FileProcess process=onCreateFileProcess(Mode.MODE_DELETE,paths,null,null,debug);
        return null!=process?process(process,finish):(toast(R.string.fail)&&false);
    }

    public final boolean deletePath(Path data,OnApiFinish<Reply<Path>> finish,String debug){
        if (null==data||!(data instanceof Path)){
            return toast(R.string.pathInvalid)&&false;
        }
        ArrayList<Path> list=new ArrayList<>(1);
        list.add(data);
        return deletePath(list,finish, debug);
    }

    public final boolean process(FileProcess process,OnApiFinish<Reply<Path>> finish){
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
        return dialog.create().setCancelable(false).setCanceledOnTouchOutside(false).title(title).
                message(getText(R.string.processSure,title,message)).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId, data)->{
//                    FileProcess.Interrupt interrupt=null!=interrupts&&interrupts.length>0?interrupts[0]:null;
//                    if (null!=interrupt){
//                        interrupts[0]=null;
//                        return interrupt.setWhat(resId)||true;
//                    }
                    switch (resId){
                        case R.string.sure:
                             dialog.setContentView(binding,false).left(null).message("");//Clean message
                             final OnApiFinish apiFinish=(int what, String note, Object apiData, Object arg) ->{
                                    if (what==What.WHAT_SUCCEED){
                                        post(()->dialog.dismiss(),100);
                                    }
                             };
                             final FileProcess.OnProcessUpdate update=(what,note,from,to,arg)-> {
                                   switch (what){
//                                       case What.WHAT_DOING:
//                                       case What.WHAT_FAIL_UNKNOWN:
//                                           dialog.message(null).title(note);
//                                           binding.setLeft(from);
//                                           binding.setRight(to);
//                                           break;
//                                       case What.WHAT_INTERRUPT:
//                                           if (null!=arg&&arg instanceof FileProcess.Interrupt){
//                                               interrupts[0]=(FileProcess.Interrupt)arg;
//                                               dialog.message(note).left(R.string.sure).right(R.string.cancel);
//                                           }
//                                           break;
                                   }
                             };
                            if (null==call(prepare(Api.class, "http://None.request.Url", null)
                                    .noneRequest().subscribeOn(Schedulers.io()).doOnSubscribe((disposable -> {
                                if (!disposable.isDisposed()){
                                    disposable.dispose();//Cancel none request
                                    Canceler canceler=cancelers[0]=process.onProcess(update,apiFinish,mRetrofit);
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

    @Override
    public Boolean onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder, Remover remover) {
        Path path=null!=data&&data instanceof Path?(Path)data:null;
        return null!=remover?deletePath(path,(int what, String note, Reply<Path> reply, Object arg)->{
            if (null!=reply&&reply.isSuccess()&&reply.getWhat()==What.WHAT_SUCCEED){
                remover.remove(true);
            }
        },"While item slide remove."):null;
    }

}
