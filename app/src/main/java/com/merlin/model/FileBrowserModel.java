package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FileModify;
import com.merlin.bean.FolderMeta;
import com.merlin.client.R;
import com.merlin.client.databinding.FileBrowserMenuBinding;
import com.merlin.client.databinding.FileContextMenuBinding;
import com.merlin.client.databinding.FileDetailBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.MessageDialog;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.media.MediaPlayService;
import com.merlin.protocol.Tag;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;
import com.merlin.view.PopupWindow;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_FILE_EXIST;
import static com.merlin.api.What.WHAT_SUCCEED;

public class FileBrowserModel extends Model implements Label, Tag, OnTapClick, OnLongClick, Model.OnActivityResume,Model.OnActivityBackPress {
    private final ObservableField<FolderMeta> mCurrent=new ObservableField();
    private final ObservableField<ClientMeta> mClientMeta=new ObservableField<>();
    private final ObservableField<String> mMultiCount=new ObservableField<>();
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>(false);
    private final ObservableBoolean mMultiMode=new ObservableBoolean(false);
    private final BrowserAdapter mBrowserAdapter=new BrowserAdapter(){
        @Override
        protected boolean onPageLoad(String path, int page, OnApiFinish<Reply<FolderMeta>> finish) {
            return null!=path&&null!=call(Api.class,(OnApiFinish<Reply<FolderMeta>>)(what, note, data, arg)->{
                if (what==WHAT_SUCCEED){
                    mCurrent.set(null!=data?data.getData():null);
                }
                if (null!=finish){
                  finish.onApiFinish(what,note,data,arg);
              }
            }).queryFiles(path, page,50);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<FolderMeta>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_PAGE) int page,
                                                 @Field(LABEL_LIMIT) int limit);
        @POST(Address.PREFIX_FILE_CLIENT_META)
        Observable<Reply<ClientMeta>> queryClientMeta();

        @POST(Address.PREFIX_FILE+"/delete")
        @FormUrlEncoded
        Observable<Reply<ApiList<String>>> deleteFile(@Field(LABEL_PATH) List<String> paths);

        @POST(Address.PREFIX_FILE+"/rename")
        @FormUrlEncoded
        Observable<Reply<FileModify>> renameFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name);

        @POST(Address.PREFIX_USER_REBOOT)
        Observable<Reply> rebootClient();

        @POST(Address.PREFIX_FILE+"/create")
        @FormUrlEncoded
        Observable<Reply<FileModify>> createFile(@Field(LABEL_PATH) String path,@Field(LABEL_NAME) String name,@Field(LABEL_FOLDER) boolean folder);

        @POST(Address.PREFIX_FILE+"/detail")
        @FormUrlEncoded
        Observable<Reply<FileMeta>> getDetail(@Field(LABEL_PATH) String path);
    }

    private interface OnChooseExist{
        void onChooseExist(List<FileMeta> list);
    }

    public FileBrowserModel(){
        refreshClientMeta("While model create.");
        browserPath("","While model create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.string.reboot:
                        return rebootClient("After reboot tap click.");
                    case R.string.createFile:
                         return createFile(false);
                    case R.string.createFolder:
                        return createFile(true);
                    case R.string.upload:
                        return null!=data&&data instanceof FileMeta&&uploadFile((FileMeta)data);
                    case R.string.detail:
                        return null!=data&&data instanceof FileMeta&&showFileDetail((FileMeta)data);
                    case R.drawable.ic_menu_normal:
                        FileBrowserMenuBinding binding=inflate(R.layout.file_browser_menu);
                        if (null!=binding){
                            binding.setFolder(mCurrent.get());
                            showAtLocationAsContext(view,binding);
                            return true;
                        }
                        break;
                    default:
                        if (null!=data&&data instanceof FileMeta){
                            return onFileMetaClick(view,resId,(FileMeta)data);
                        }
                        break;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        if (null!=data&&data instanceof FileMeta){
            FileContextMenuBinding binding=DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),R.layout.file_context_menu,null,false);
            if (null!=binding){
                binding.setFile((FileMeta)data);
                showAtLocationAsContext(view,binding);
                return true;
            }
        }
        return false;
    }

    private boolean uploadFile(FileMeta meta){
        String path=null!=meta?meta.getPath():null;
        if (null==path||path.length()<=0 ||!meta.isDirectory()){
            toast(R.string.pathInvalid);
            return false;
        }
        return false;
    }

    private boolean showFileDetail(FileMeta meta){
        String path=null!=meta?meta.getPath():null;
        FileDetailBinding binding=null==path||path.length()<=0?null:inflate(R.layout.file_detail);
        if (null==binding){
            toast(R.string.pathInvalid);
            return false;
        }
        binding.setFile(meta);
        binding.setLoadState(What.WHAT_INVALID);
        final Dialog dialog=new Dialog(getViewContext());
        dialog.setContentView(null!=binding?binding.getRoot():null).show(( view, clickCount, resId, data)->{
            return true;
        },false);
        return null!=call(Api.class,(OnApiFinish<Reply<FileMeta>>)(what,note,data2,arg)->{
            FileMeta detail=what==WHAT_SUCCEED&&null!=data2?data2.getData():null;
            if (null!=detail){
                binding.setFile(detail);
            }
            binding.setLoadState(what);
        }).getDetail(path);
    }

    private boolean resetBrowserCurrentFolder(String debug){
        BrowserAdapter adapter=mBrowserAdapter;
        return null!=adapter&&adapter.resetLoad(debug);
    }

    private boolean browserPath(String pathValue, String debug){
        BrowserAdapter adapter=mBrowserAdapter;
        return null!=adapter&&adapter.loadPage(pathValue,debug);
    }

    private boolean refreshClientMeta(String debug){
        Debug.D(getClass(),"Refresh client meta "+(null!=debug?debug:"."));
        return null!=call(Api.class,(OnApiFinish<Reply<ClientMeta>>)(what, note, data, arg)->{
            if(what==WHAT_SUCCEED){
                mClientMeta.set(null!=data?data.getData():null);
            }
        }).queryClientMeta();
    }

    private boolean rebootClient(String debug){
        Dialog dialog=new Dialog(getViewContext());
        dialog.create().title(R.string.reboot).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId,data)-> {
                if (resId==R.string.sure){
                    Debug.D(getClass(),"Reboot client meta "+(null!=debug?debug:"."));
                    call(Api.class,(OnApiFinish<Reply>)(what, note, data2, arg)-> toast(note)).rebootClient();
                }
                dialog.dismiss();
                return true;
        });
        return true;
    }

    private boolean onFileMetaClick(View view,int resId,FileMeta file){
        if (null!=file) {
            switch (resId) {
                case R.string.delete:
                    List<FileMeta> list=null!=file?new ArrayList<>():null;
                    return null!=list&&list.add(file)&&deleteFile(list);
                case R.string.rename:
                    return renameFile(file);
                default:
                    if (isMultiMode().get()) {
                        multiChoose(file);
                    } else {
                        if (file.isDirectory()) {
                            browserPath(file.getPath(), "After directory click.");
                        } else {//Open file
                            if (!file.isAccessible()) {
                                return toast(R.string.nonePermission);
                            }
                            String extension = file.getExtension();
                            if (extension.equals("mp3")) {
                                return MediaPlayService.play(getContext(), file.getMeta(), 0, false);
                            } else {
                                return toast(R.string.noneSupportOpenFileType, extension);
                            }
                        }
                    }
                    break;
            }
        }
        return false;
    }

    private boolean createFile(boolean dir){
        FolderMeta folderMeta=mCurrent.get();
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
                       return null!=call(Api.class,(OnApiFinish<Reply<FileModify>>)(what,note,data2,arg)->{
                           if(what==WHAT_SUCCEED){
                               resetBrowserCurrentFolder("After file create succeed.");
                           }
                           toast(note);
                       }).createFile(parent,input,dir);
                   }
               }
               dialog.dismiss();
            return true;
        });
    }

    private boolean renameFile(FileMeta meta){
        final String path=null!=meta?meta.getPath():null;
        if (null!=path&&path.length()>0){
            final String name=meta.getName();
            return new SingleInputDialog(getViewContext()).show(R.string.rename,(dlg, text)->{
                if (null==text||text.length()<=0){
                    toast(R.string.inputNotNull);
                }else if (null!=name&&text.equals(name)){
                    toast(R.string.noneChanged);
                }else{
                    if (null!=dlg){
                        dlg.dismiss();
                    }
                    call(Api.class,(OnApiFinish<Reply<FileModify>>)(what,note,data,arg)->{
                        boolean succeed=what==WHAT_SUCCEED;
                        toast(succeed?R.string.succeed : what==WHAT_FILE_EXIST?R.string.fileAlreadyExist:R.string.fail);
                        BrowserAdapter adapter=mBrowserAdapter;
                        FileModify modify=succeed&&null!=data&&null!=adapter?data.getData():null;
                        if (succeed&&null!=modify&&null!=adapter){
                            adapter.renamePath(meta,modify);
                        }
                    }).renameFile(path,text);
                }
            });
        }
        Debug.W(getClass(),"Can't rename file.path="+path);
        return false;
    }

    private boolean deleteFile(List<FileMeta> files){
        final int length=null!=files?files.size():-1;
        if (length>0){
            List<String> paths=new ArrayList<>();
            Map<String,FileMeta> map=new HashMap<>(length);
            for (FileMeta meta:files) {
                String path=null!=meta?meta.getPath():null;
                if (null!=path&&path.length()>0){
                    paths.add(path);
                    map.put(path,meta);
                }
            }
            if (null!=paths&&paths.size()>0){
                return null!=call(Api.class,(OnApiFinish<Reply<ApiList<String>>>)(what,note,data,arg)->{
                    toast(note);
                    if (what==WHAT_SUCCEED){
                        List<String> deletedPaths=null!=data?data.getData():null;
                        BrowserAdapter adapter=mBrowserAdapter;
                        int size=null!=deletedPaths&&null!=adapter?deletedPaths.size():-1;
                        if (size>0){
                            List<FileMeta> deleted=new ArrayList<>(size);
                            for (String  path:deletedPaths) {
                                FileMeta child=null!=path?map.get(path):null;
                                if (null!=child){
                                    deleted.add(child);
                                }
                            }
                            adapter.remove(deleted);
                        }
                    }
                }).deleteFile(paths);
            }
        }
        Debug.D(getClass(),"Can't delete file.");
        return false;
    }

    private boolean browserParent(String debug){
        FolderMeta current=mCurrent.get();
        String parent=null!=current?current.getParent():null;
        String curr=null!=current?current.getPath():null;
        if (null==parent||parent.length()<=0||(null!=curr&&curr.equals(parent))){
            return false;
        }
        return browserPath(parent,debug);
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        if (isMultiMode().get()){
            return chooseAll(false)||multiMode(false);
        }
        return browserParent("After back pressed called.");
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
        refreshCurrentPath("After activity onResume.");
    }

    private final boolean refreshCurrentPath(String debug){
        FolderMeta meta=mCurrent.get();
        return browserPath(null!=meta?meta.getPath():null,debug);
    }

    public final boolean chooseAll(boolean choose){
        BrowserAdapter adapter=mBrowserAdapter;
        if (isMultiMode().get()&&null!=adapter&&adapter.chooseAll(choose)){
            refreshMultiChooseCount();
            return true;
        }
        return false;
    }

    public ObservableField<ClientMeta> getClientMeta() {
        return mClientMeta;
    }

    public ObservableBoolean isMultiMode() {
        return mMultiMode;
    }

    private boolean multiMode(boolean entry){
        boolean curr=mMultiMode.get();
        BrowserAdapter adapter=mBrowserAdapter;
        if (entry!=curr&&null!=adapter){
            adapter.multiMode(entry);
            mMultiMode.set(entry);
            refreshMultiChooseCount();
            return true;
        }
        return false;
    }

    private boolean multiChoose(FileMeta meta){
        BrowserAdapter adapter=mBrowserAdapter;
        if (null!=meta&&mMultiMode.get()&&adapter.multiChoose(meta)){
            refreshMultiChooseCount();
            return true;
        }
        return false;
    }

    public ObservableField<Boolean> isAllChoose() {
        return mAllChoose;
    }

    private void refreshMultiChooseCount() {
        FolderMeta folderMeta=mCurrent.get();
        int length=null!=folderMeta?folderMeta.getLength():0;
        BrowserAdapter adapter=mBrowserAdapter;
        int count=null!=adapter?adapter.getChooseCount():0;
        mMultiCount.set(count<=0?"None selected(0/"+length+")":"Selected("+count+"/"+length+")");
        if (null!=adapter){
            List<FileMeta> data=adapter.getData();
            int size=null!=data?data.size():0;
            mAllChoose.set(size==count&&size>0);
        }
    }

    public ObservableField<String> getMultiChooseCount() {
        return mMultiCount;
    }

    private void runChoose(OnChooseExist exit,boolean emptyToast){
        BrowserAdapter adapter=mBrowserAdapter;
        List<FileMeta> list=null!=adapter?adapter.getChoose():null;
        if (null==list||list.size()<=0){
            if (emptyToast){
                toast("Choose nothing.");
            }
        }else if(null!=exit){
            exit.onChooseExist(list);
        }
    }

    public ObservableField<FolderMeta> getCurrent() {
        return mCurrent;
    }

    public BrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }
}
