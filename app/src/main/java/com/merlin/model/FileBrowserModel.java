package com.merlin.model;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.merlin.activity.TransportActivity;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.FileBrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FileMeta_BK;
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


import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class FileBrowserModel extends DataListModel implements SwipeRefreshLayout.OnRefreshListener,
        BaseAdapter.OnItemClickListener, BaseAdapter.OnItemLongClickListener,OnFrameReceive, BaseAdapter.OnLoadMore,
        BaseModel.OnModelViewClick, BaseModel.OnModelViewLongClick, BaseAdapter.OnItemMultiClickListener, Label, Tag {
    private final ObservableField<FolderMeta> mCurrent=new ObservableField();
    private final ObservableField<ClientMeta> mClientMeta=new ObservableField<>();
    private final ObservableField<String> mMultiCount=new ObservableField<>();
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>(false);
    private final ObservableBoolean mMultiMode=new ObservableBoolean(false);
    private final ContextMenuWindow mPopupWindow=new ContextMenuWindow(true);
    private Browsing mBrowsing;

    private interface BrowserApi{
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<FolderMeta>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_PAGE) int page,
                                                 @Field(LABEL_LIMIT) int limit);
        @POST(Address.PREFIX_FILE_CLIENT_META)
        Observable<Reply<ClientMeta>> queryClientMeta();

        @POST(Address.PREFIX_USER_ROOT)
        Observable<Reply> rebootClient();
    }

    private interface OnChooseExist{
        void onChooseExist(List<FileMeta> list);
    }

    public FileBrowserModel(Context context){
        super(context,new FileBrowserAdapter(),new LinearLayoutManager(context));
        getAdapter().setOnLoadMore(this);
        refreshClientMeta("While model create.");
    }

    @Override
    public boolean onLoadMore() {
        FolderMeta current=mCurrent.get();
        if (null==current){
            refreshCurrentPath("After load more call without folder set.");
            return false;
        }
        return browserPath(current.getPath(),current.getPage()+1,"After load more call.");
    }

    private boolean browserPath(String pathValue, String debug){
        return browserPath(pathValue,0,debug);
    }

    private boolean browserPath(String pathValue,int page,String debug){
        final String path=null!=pathValue?pathValue:"";
        final Browsing newBrowsing=new Browsing(path,page);
        Browsing browsing=mBrowsing;
        if (null!=browsing){
            synchronized (browsing){
                if (browsing.equals(newBrowsing)){
                    Debug.W(getClass(),"Not need browser path again "+path+" "+(null!=debug?debug:"."));
                    return false;
                }
            }
        }
        setRefreshing(true);
        mBrowsing=newBrowsing;
        Debug.D(getClass(),"Browsing path "+page+" "+path +" "+(null!=debug?debug:"."));
        return null!=call(BrowserApi.class,(OnApiFinish<Reply<FolderMeta>>)(what, note, data, arg)->{
            setRefreshing(false);
            Browsing current=mBrowsing;
            if (null!=current){
                synchronized (current){
                    if (current.equals(newBrowsing)){
                        FolderMeta meta=null!=data?data.getData():null;
                        mBrowsing=null;
                        if (what== WHAT_SUCCEED){
                            mCurrent.set(meta);
                            BaseAdapter adapter=getAdapter();
                            List  list=null!=meta?meta.getData():null;
                            if (newBrowsing.mPage>0){

                                adapter.add(list);
                            }else{
                                adapter.setData(list,true);
                            }
                        }
                    }
                }
            }
            if (what!=WHAT_SUCCEED){
                toast(R.string.requestFail, note);
            }
        }).queryFiles(path,page,12);
    }

    private boolean refreshClientMeta(String debug){
        Debug.D(getClass(),"Refresh client meta "+(null!=debug?debug:"."));
        return null!=call(BrowserApi.class,(OnApiFinish<Reply<ClientMeta>>)(what, note, data, arg)->{
            if(what==WHAT_SUCCEED){
                mClientMeta.set(null!=data?data.getData():null);
            }
        }).queryClientMeta();
    }

    private boolean rebootClient(String debug){
        Debug.D(getClass(),"Reboot client meta "+(null!=debug?debug:"."));
        return null!=call(BrowserApi.class,(OnApiFinish<Reply>)(what, note, data, arg)->{
            if(what==WHAT_SUCCEED){
                toast(R.string.rebooting);
            }
        }).rebootClient();
    }

    @Override
    public void onBridgeBoundChange(boolean bound) {
        if (bound){
            setColorSchemeColors(Color.RED,Color.YELLOW,Color.BLUE);
            setProgressBackgroundColorSchemeColor(Color.TRANSPARENT);
//            multiMode(true);
        }
    }

    @Override
    public void onItemClick(View view, int sourceId,int position, Object data) {
        if (null!=data){
            if (data instanceof FileMeta){
                onFileMetaClick(view, sourceId, position, (FileMeta) data);
            }else if (data instanceof ContextMenu){
                onContextMenuClick(view,sourceId,position,(ContextMenu)data);
            }
        }
    }

    private void onFileMetaClick(View view, int sourceId,int position, FileMeta file){
        if (null!=file) {
            if (isMultiMode().get()) {
                multiChoose(file);
            } else {
                if (file.isDirectory()){
//                    if (file.isReadable(file.getPermissions())) {
                    browserPath(file.getPath(), "After directory click.");
//                    }
                }else{
                    //test
                   String extension= file.getExtension();
                   if (extension.equals("mp3")){
                       MediaPlayService.play(getContext(),file.getMeta(),0,false);
                   }
//                    toast("点击了文件 "+extension+" " +file.getMeta());
//                if (!file.isRead()) {
//                    toast("文件不可读");
//                }else {
//                    toast("点击了文件" + file.getTitle());
//                    Debug.D(getClass(), "点击了文件 " + file.getPath());
////                    DownloadService.postDownload(getContext(), getClientAccount(), null, file);
//                }
                }
            }
        }
    }

    private void onContextMenuClick(View view, int sourceId,int position, ContextMenu menu){
        if (null!=menu){
            toast("点击了 "+menu.getTextId());
        }
    }

    @Override
    public boolean onItemMultiClick(View view, int clickCount, int sourceId, int position, Object data) {
        if (null!=data){
            if (data instanceof FileMeta_BK){
                if(clickCount==2){
                    mPopupWindow.showAtLocation(view, Gravity.CENTER,0,0);
                    mPopupWindow.setOnItemClickListener(this);
                    mPopupWindow.reset(R.string.rename,R.string.addToFavorite,R.string.detail);
                   return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onItemLongClick(View view, int sourceId,int position, Object data) {
        if (null!=data&&data instanceof FileMeta_BK){
            return !isMultiMode().get()&&multiMode(true);
        }
        return false;
    }

    @Override
    public boolean onViewLongClick(View v, int id) {
        switch (id){
            case R.id.fileBrowser_downloadTV:
                runChoose((list)->{

                },true);
                return true;
        }
        return false;
    }

    @Override
    public void onViewClick(View v, int id,Object obj) {
        Debug.D(getClass()," onViewClick "+v);
        switch (id){
            case R.id.fileBrowser_cancelIV:
                onBackPressed();
                break;
            case R.id.fileBrowser_topBackIV:
                if (!browserParent("After top back click.")){
                    toast(R.string.alreadyArrivedRoot);
                }
                break;
            case R.id.fileBrowser_chooseAllIV:
                chooseAll(true);
                break;
            case R.id.fileBrowser_unChooseAllIV:
                chooseAll(false);
                break;
            case R.id.fileBrowser_menuIV:
                mPopupWindow.showAtLocation(v, Gravity.CENTER,0,0);
                mPopupWindow.setOnItemClickListener(this);
//                mPopupWindow.reset(R.string.reboot);
                rebootClient("test");
                break;
            case R.id.fileBrowser_topSearchIV:
                 new SearchDialog(v.getContext()).setOnSearchInputChange((input)->{
                     toast("该表了 "+input);
                 }).show();
                break;
            case R.id.fileBrowser_transmitIV:
                startActivity(TransportActivity.class);
                break;
            case R.id.fileBrowser_downloadTV:
//                runChoose((list)->DownloadService.postDownload(v.getContext(),getClientAccount(),null,list),true);
                break;
        }
    }

    public boolean onBackPressed(){
        if (isMultiMode().get()){
            return chooseAll(false)||multiMode(false);
        }
        return browserParent("After back pressed called.");
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
    public void onRefresh() {
        setRefreshing(false);
        refreshCurrentPath("While list refresh trigger.");//Browser current path again
    }

    @Override
    public void onFrameReceived(Frame frame, Client client) {
    }

    public final boolean refreshCurrentPath(String debug){
        FolderMeta meta=mCurrent.get();
        return browserPath(null!=meta?meta.getPath():"/volume1",debug);
    }

    public final boolean chooseAll(boolean choose){
        FileBrowserAdapter adapter=(FileBrowserAdapter)getAdapter();
        if (isMultiMode().get()&&null!=adapter&&adapter.chooseAll(choose)){
            multiChooseCount();
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
        if (entry!=curr){
            FileBrowserAdapter adapter=(FileBrowserAdapter)getAdapter();
            adapter.multiMode(entry);
            mMultiMode.set(entry);
            return true;
        }
        return false;
    }

    private boolean multiChoose(FileMeta meta){
        FileBrowserAdapter adapter=(FileBrowserAdapter)getAdapter();
        if (null!=meta&&mMultiMode.get()&&adapter.multiChoose(meta)){
            multiChooseCount();
            return true;
        }
        return false;
    }

    public ObservableField<Boolean> isAllChoose() {
        return mAllChoose;
    }

    private void multiChooseCount() {
        int count=((FileBrowserAdapter)getAdapter()).getChooseCount();
        mMultiCount.set(count<=0?"None selected":"Selected("+count+")");
        BaseAdapter adapter=getAdapter();
        if (null!=adapter){
            List<FileMeta_BK> data=adapter.getData();
            int size=null!=data?data.size():0;
            mAllChoose.set(size==count&&size>0);
        }
    }

    public ObservableField<String> getMultiChooseCount() {
        return mMultiCount;
    }

    private void runChoose(OnChooseExist exit,boolean emptyToast){
        FileBrowserAdapter adapter=((FileBrowserAdapter)getAdapter());
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
}
