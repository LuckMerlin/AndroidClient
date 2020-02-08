package com.merlin.model;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderMeta;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.SearchDialog;
import com.merlin.media.MediaPlayService;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.view.ContextMenu;
import com.merlin.view.ContextMenuWindow;
import com.merlin.protocol.Tag;
import com.merlin.server.Frame;
import com.merlin.view.OnMultiClick;


import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class FileBrowserModel extends Model implements Label, Tag, OnMultiClick, Model.OnActivityResume,Model.OnActivityBackPress {
    private final ObservableField<FolderMeta> mCurrent=new ObservableField();
    private final ObservableField<ClientMeta> mClientMeta=new ObservableField<>();
    private final ObservableField<String> mMultiCount=new ObservableField<>();
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>(false);
    private final ObservableBoolean mMultiMode=new ObservableBoolean(false);
    private final ContextMenuWindow mPopupWindow=new ContextMenuWindow(true);
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
            }).queryFiles(path, page,10);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<FolderMeta>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_PAGE) int page,
                                                 @Field(LABEL_LIMIT) int limit);
        @POST(Address.PREFIX_FILE_CLIENT_META)
        Observable<Reply<ClientMeta>> queryClientMeta();

        @POST(Address.PREFIX_USER_REBOOT)
        Observable<Reply> rebootClient();
    }

    private interface OnChooseExist{
        void onChooseExist(List<FileMeta> list);
    }

    public FileBrowserModel(){
        refreshClientMeta("While model create.");
        browserPath("","While model create.");
    }

    @Override
    public boolean onMultiClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                if (null!=data&&data instanceof FileMeta){
                    onFileMetaClick((FileMeta)data);
                    return true;
                }
                break;
        }
        return false;
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
        Debug.D(getClass(),"Reboot client meta "+(null!=debug?debug:"."));
        return null!=call(Api.class,(OnApiFinish<Reply>)(what, note, data, arg)->{
            if(what==WHAT_SUCCEED){
                toast(R.string.rebooting);
            }
        }).rebootClient();
    }

//    @Override
//    public void onItemClick(View view, int sourceId,int position, Object data) {
//        if (null!=data){
//            if (data instanceof FileMeta){
//                onFileMetaClick(view, sourceId, position, (FileMeta) data);
//            }else if (data instanceof ContextMenu){
//                onContextMenuClick(view,sourceId,position,(ContextMenu)data);
//            }
//        }
//    }

    private boolean onFileMetaClick(FileMeta file){
        if (null!=file) {
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
        }
        return false;
    }

//    @Override
//    public boolean onItemLongClick(View view, int sourceId,int position, Object data) {
//        if(null!=data&&data instanceof FileMeta){
//            mPopupWindow.showAtLocation(view, Gravity.CENTER,0,0);
//            mPopupWindow.setOnItemClickListener(this);
//            mPopupWindow.reset(R.string.rename,R.string.addToFavorite,R.string.detail);
//            return true;
//        }
//        return false;
//    }

    private void onContextMenuClick(View view, int sourceId,int position, ContextMenu menu){
        if (null!=menu){
            toast("点击了 "+menu.getTextId());
        }
    }

//    @Override
//    public boolean onItemMultiClick(View view, int clickCount, int sourceId, int position, Object data) {
//        switch (clickCount){
//            case= 2:
//
//                break;
//            case 3:
//                if (null!=data&&data instanceof FileMeta){
//                    return !isMultiMode().get()&&multiMode(true);
//                }
//                break;
//        }
//        return false;
//    }

//    @Override
//    public void onViewClick(View v, int id,Object obj) {
//        Debug.D(getClass()," onViewClick "+v);
//        switch (id){
//            case R.id.fileBrowser_cancelIV:
//                onBackPressed();
//                break;
//            case R.id.fileBrowser_topBackIV:
//                if (!browserParent("After top back click.")){
//                    toast(R.string.alreadyArrivedRoot);
//                }
//                break;
//            case R.id.fileBrowser_chooseAllIV:
//                chooseAll(true);
//                break;
//            case R.id.fileBrowser_unChooseAllIV:
//                chooseAll(false);
//                break;
//            case R.id.fileBrowser_menuIV:
//                mPopupWindow.showAtLocation(v, Gravity.CENTER,0,0);
//                mPopupWindow.setOnItemClickListener(this);
////                mPopupWindow.reset(R.string.reboot);
//                rebootClient("test");
//                break;
//            case R.id.fileBrowser_topSearchIV:
//                 new SearchDialog(v.getContext()).setOnSearchInputChange((input)->{
//                     toast("该表了 "+input);
//                 }).show();
//                break;
//            case R.id.fileBrowser_transmitIV:
//                startActivity(TransportActivity.class);
//                break;
//            case R.id.fileBrowser_downloadTV:
////                runChoose((list)->DownloadService.postDownload(v.getContext(),getClientAccount(),null,list),true);
//                break;
//        }
//    }

    private boolean browserParent(String debug){
        FolderMeta current=mCurrent.get();
        String parent=null!=current?current.getParent():null;
        String curr=null!=current?current.getPath():null;
        if (null==parent||parent.length()<=0||(null!=curr&&curr.equals(parent))){
            return false;
        }
        return browserPath(parent,debug);
    }

//    @Override
//    public void onRefresh() {
//        setRefreshing(false);
//        refreshCurrentPath("While list refresh trigger.");//Browser current path again
//    }


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

    private static class Browsing{
        private final String mPath;
        private final int mPage;
        private Browsing(String path,int page){
            mPath=path;
            mPage=page;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (null!=obj&&obj instanceof Browsing){
                String path=((Browsing)obj).mPath;
               if (mPage==((Browsing)obj).mPage&&((null==path&&null==mPath)
                       ||(null!=path&&null!=mPath&&path.equals(mPath)))){
                   return true;
               }
            }
            return super.equals(obj);
        }
    }


    public BrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }
}
