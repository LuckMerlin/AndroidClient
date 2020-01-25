package com.merlin.model;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import com.merlin.api.What;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.merlin.activity.TransportActivity;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.FileBrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FileMeta_BK;
import com.merlin.bean.FolderMeta;
import com.merlin.bean.Meta;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.SearchDialog;
import com.merlin.oksocket.OnFrameReceive;
import com.merlin.oksocket.Socket;
import com.merlin.retrofit.Retrofit;
import com.merlin.view.ContextMenu;
import com.merlin.view.ContextMenuWindow;
import com.merlin.protocol.Tag;
import com.merlin.server.Frame;
import com.merlin.server.Json;
import com.merlin.server.Response;


import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class FileBrowserModel extends DataListModel implements SwipeRefreshLayout.OnRefreshListener,
        BaseAdapter.OnItemClickListener, BaseAdapter.OnItemLongClickListener,OnFrameReceive,
        BaseModel.OnModelViewClick, BaseModel.OnModelViewLongClick, BaseAdapter.OnItemMultiClickListener, Label, Tag {
    private final ObservableField<FolderMeta> mCurrent=new ObservableField();
    private final ObservableField<Meta> mClientMeta=new ObservableField<>();
    private final ObservableField<String> mMultiCount=new ObservableField<>();
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>(false);
    private final ObservableBoolean mMultiMode=new ObservableBoolean(false);
    private final ContextMenuWindow mPopupWindow=new ContextMenuWindow(true);
    private String mBrowsingPath;

    private interface BrowserApi{
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<FolderMeta>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_PAGE) int page,
                                                 @Field(LABEL_LIMIT) int limit);
    }

    private interface OnChooseExist{
        void onChooseExist(List<FileMeta> list);
    }

    public FileBrowserModel(Context context){
        super(context,new FileBrowserAdapter(),new LinearLayoutManager(context));
    }

    private boolean browserPath(String pathValue,String debug){
        return browserPath(pathValue,0,debug);
    }

    private boolean browserPath(String pathValue,int page,String debug){
        if (null==pathValue){//If need browser root
            Meta meta=mClientMeta.get();
            String root=null!=meta?meta.getRoot():null;
            pathValue=root;
        }
        final String path=pathValue;
        if (null==path){
            Debug.W(getClass(),"Can't browser invalid NULL path "+(null!=debug?debug:"."));
            toast(R.string.pathInvalid);
            return false;
        }
        final String browsingPath=mBrowsingPath;
        if (null!=browsingPath){
            synchronized (browsingPath){
                if (browsingPath.equals(path)){
                    Debug.W(getClass(),"Not need browser path again "+path+" "+(null!=debug?debug:"."));
                    toast(R.string.operating);
                    return false;
                }
            }
        }
        setRefreshing(true);
        mBrowsingPath=path;
        Debug.D(getClass(),"Browsing path "+path +" "+(null!=debug?debug:"."));
        return null!=call(BrowserApi.class,(OnApiFinish<Reply<FolderMeta>>)(what, note, data, arg)->{
            setRefreshing(false);
            String browsing=mBrowsingPath;
            if (null!=browsing){
                synchronized (browsing){
                    if (browsing.equals(path)){
                        FolderMeta meta=data.getData();
                        mCurrent.set(meta);
                        mBrowsingPath=null;
                        if (what== WHAT_SUCCEED){
                            getAdapter().setData(null!=meta?meta.getData():null,true);
                        }
                    }
                }
            }
            if (!data.isSuccess()){
                toast(R.string.requestFail);
            }
        }).queryFiles(path,page,12);
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
                    toast("点击了文件" + file.getTitle());
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
                browserParent("After top back click.");
                break;
            case R.id.fileBrowser_chooseAllIV:
                chooseAll(true);
                break;
            case R.id.fileBrowser_unChooseAllIV:
                chooseAll(false);
                break;
            case R.id.fileBrowser_menuIV:
                toast("点击了菜单");
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
        if (null==parent||parent.length()<=0){
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
        Response response=null!=frame?frame.getResponse():null;
        if (null!=response &&response.isSucceed()&&response.getWhat()== WHAT_SUCCEED){//Test
            String note=response.getNote();
            if (null!=note&&note.contains("wuyue")){
//                JSONObject object=new JSONObject();
//                Json.putIfNotNull(object,TAG_ONLINE, true);
//                putIfNotNull(object,TAG_ACCOUNT,"nas");
//                getClientMeta(object, new Socket.OnRequestFinish() {
//                       @Override
//                       public void onRequestFinish(boolean succeed, int what, Frame frame) {
//                           String text=succeed&&null!=frame?frame.getBodyText():null;
//                           Debug.D(getClass(),"收到 meta 了。"+text);
//                       }
//                   });
//                 browser(mCurrPath.get());
//                openFile("/volume1/Upload/Photo/Xiamen/IMG_20190729_204708.jpg");
            }
        }
    }

    private boolean openFile(String path){
//        path = "F:\\LuckMerlin\\SLManager\\test.png";
//        path = "C:\\Users\\admin\\Desktop\\linqiang.mp3";
        JSONObject object=new JSONObject();
        Json.putIfNotNull(object,TAG_COMMAND_TYPE,TAG_COMMAND_READ_FILE);
        Json.putIfNotNull(object,TAG_FILE,path);
        Handler handler=new Handler(Looper.getMainLooper());
        return sendMessage(object.toString(), "linqiang", TAG_MESSAGE_QUERY,30*1000,new Socket.OnRequestFinish() {
            @Override
            public void onRequestFinish(boolean succeed, int what,String note, Frame frame) {
                    handler.post(new Runnable() {
                    @Override
                    public void run() {
                        byte[] bytes=frame.getBodyBytes();
//                        Dialog dialog=new Dialog(MM);
//                        ImageView imageView=new ImageView(MM);
//                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
//                        dialog.setContentView(imageView);
//                        dialog.show();
                    }
                });
            }
        });
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

    public boolean setClientMeta(Meta meta){
        String account=null!=meta&&meta.isDeviceType(TAG_NAS_DEVICE)?meta.getAccount():null;
        if (null!=account) {
            mClientMeta.set(meta);
            refreshClientMeta("After meta set.");
            return true;
        }
        Debug.W(getClass(),"Can't set client meta,Check meta if valid?");
        return false;
    }

    private boolean refreshClientMeta(String debug){
        Meta meta=mClientMeta.get();
        String account=null!=meta?meta.getAccount():null;
        if (null!=account){
            return getAccountClientMeta(account,(Socket.OnRequestFinish)(succeed, what,note, frame)->{
                if (succeed&&null!=frame){
                    List<Meta> list= JSON.parseArray(frame.getBodyText(), Meta.class);
                    Meta newMeta=null!=list&&list.size()>0?list.get(0):null;
                    mClientMeta.set(newMeta);
                    String root=null!=newMeta?newMeta.getRoot():null;
//                    String curr=mCurrPath.get();
//                    String loading=mLoadingPath;
//                    if (null!=curr&&null!=root&&!curr.contains(root)&&(null==loading||(!loading.isEmpty()
//                            &&!loading.equals(root)))){//Check current if within root
//                       browser(root,"After root changed while meta updated.");
//                    }
                 }
            });
        }
        Debug.W(getClass(),"Can't refresh client meta "+(null!=debug?debug:".")+" account="+account);
        return false;
    }

    public ObservableField<Meta> getCurrentMeta() {
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
}
