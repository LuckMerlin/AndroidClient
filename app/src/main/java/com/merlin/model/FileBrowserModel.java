package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import com.merlin.activity.ConveyorActivity;
import com.merlin.activity.TransportActivity;
import com.merlin.adapter.Adapter;
import com.merlin.adapter.BrowserAdapter;
import com.merlin.adapter.ListAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.LocalFile;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.client.databinding.ClientDetailBinding;
import com.merlin.client.databinding.DeviceTextBinding;
import com.merlin.client.databinding.FileBrowserMenuBinding;
import com.merlin.debug.Debug;
import com.merlin.file.CoverMode;
import com.merlin.protocol.Tag;
import com.merlin.server.Retrofit;
import com.merlin.transport.TransportService;
import com.merlin.transport.litehttp.LiteHttpTransport;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;


import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class FileBrowserModel extends Model implements Label, ClientCallback, Tag, OnTapClick, OnLongClick, Model.OnActivityResume,Model.OnActivityBackPress {
    private Map<String,Object> mAllClientMetas;
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
    private final ObservableField<BrowserModel> mCurrent=new ObservableField<>();
    private final ObservableField<FolderData> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mCurrentMode=new ObservableField<>();
    private final ObservableField<ListAdapter> mCurrentAdapter=new ObservableField<>();
    private final ObservableField<ClientMeta> mCurrentMeta=new ObservableField<>();
    private Object mProcessing;
    private final ObservableField<String> mCurrentMultiChooseSummary=new ObservableField<>();

    public interface OnBrowserClientChange{
        void onBrowserClientChanged(BrowserModel last,BrowserModel current);
    }

    private interface Api{
        @POST(Address.PREFIX_USER+"/client/meta")
        Observable<Reply<ClientMeta>> queryClientMeta();

        @POST(Address.PREFIX_FILE+"/sync/check")
        @FormUrlEncoded
        Observable<Reply> checkFileSync(@Field(Label.LABEL_MD5) String md5s);
    }

    public FileBrowserModel(){
        mCurrentMode.set(BrowserModel.MODE_NORMAL);
    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        putClientMeta(ClientMeta.buildLocalClient(getContext()), "After mode create.");
//        String name,String url,String account,String imageUrl,String folder,String pathSep
        ClientMeta testClient=new ClientMeta("算法",Address.HOST,"none","",null,"///");
        putClientMeta(testClient, "After mode create.");
//        refreshClientMeta("After mode create.");
//        call(prepare(Api.class, Address.URL).checkFileSync("linqinagMD5"), new OnApiFinish<Void>() {
//            @Override
//            public void onApiFinish(int what, String note, Void data, Object arg) {
//                Debug.D(getClass(),"AAAAAAAA "+what+" "+note+" "+arg );
//            }
//        });
    }

    private boolean putClientMeta(ClientMeta meta,String debug){
        String url=null!=meta?meta.getUrl():null;
        if (null!=url&&url.length()>0){
            Map<String,Object> list=mAllClientMetas;
            Object object=(list=null!=list?list:(mAllClientMetas=new ConcurrentHashMap<>())).get(url);
            if (null!=object&&object instanceof BrowserModel){
                ((BrowserModel)object).setMeta(meta,"After meta put.");
            }else{
                object=meta;
            }
            Debug.D(getClass(),"Put client "+url+" "+(null!=debug?debug:"."));
            list.put(url,object);
            mClientCount.set(list.size());
            changeDevice(meta,false,"After client put "+(null!=debug?debug:"."));
            return true;
        }
        Debug.W(getClass(),"Can't put client meta "+(null!=debug?debug:"."));
        return false;
    }

    private BrowserModel createModel(ClientMeta meta){
        if (null!=meta){
            Context context=getViewContext();
            BrowserModel model=meta.isLocalClient()?new LocalBrowserModel(context,meta,this)
                    : new NasBrowserModel(context,meta,this);
            return model;
        }
        return null;
    }

    @Override
    public void onPageDataLoad(BrowserModel model, PageData folder) {
        if (isCurrentModel(model)&&(null==folder||folder instanceof FolderData)){
            mCurrentFolder.set((FolderData)folder);
        }
    }

    @Override
    public void onBrowserModeChange(BrowserModel model, int lase, int curr) {
        if (isCurrentModel(model)){
            mCurrentMode.set(curr);
        }
    }

    private boolean isCurrentModel(BrowserModel model){
        BrowserModel curr=mCurrent.get();
        return null!=curr&&null!=model&&model==curr;
    }

    @Override
    public final boolean onProcessSet(Object object, String debug) {
        mProcessing=object;
        Debug.D(getClass(),"Set processing "+mProcessing+" "+(null!=debug?debug:"."));
        return true;
    }

    private boolean changeDevice(ClientMeta client, boolean force, String debug){
        final String url=null!=client?client.getUrl():null;
        if (null!=url&&url.length()>0){
            if (force||null==mCurrent.get()){
                Debug.D(getClass(),"Change browser device "+client.getName()+" "+(null!=debug?debug:"."));
                Map<String,Object> map=mAllClientMetas;
                BrowserModel model;
                if (null!=map){
                    Object object=map.get(url);
                    model=null!=object&&object instanceof BrowserModel?(BrowserModel)object: createModel(client);
                    if (null!=model){
                         BrowserModel curr=mCurrent.get();
                         BrowserAdapter<FileMeta> adapter=model.getBrowserAdapter();
                         mCurrentAdapter.set(adapter);
                         PageData pageData=null!=adapter?adapter.getLastPage():null;
                         mCurrentFolder.set(null!=pageData&&pageData instanceof FolderData?(FolderData)pageData:null);
                         mCurrentMeta.set(model.getClientMeta());
                         mCurrent.set(model);
                         mCurrentMode.set(model.getMode());
                         if (null!=model&&model instanceof BrowserModel){
                             model.onBrowserClientChanged(curr,model);
                         }
                         return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean refreshClientMeta(String debug){
        Debug.D(getClass(),"Refresh client meta "+(null!=debug?debug:"."));
        return null!=call(prepare(Api.class,Address.HOST).queryClientMeta(),(OnApiFinish<Reply<ClientMeta>>)(what, note, data, arg)->{
            ClientMeta meta=what==WHAT_SUCCEED&&null!=data?data.getData():null;
            if(null!=meta){
                putClientMeta(meta,"After client meta response.");
//                ArrayList<CharSequence> list=new ArrayList<>();
//                list.add("/sdcard/PictureseditedJPEG_20200108_153014.jpg");
//                list.add("/sdcard/kgmusic/download/刘昊霖、Kidult - Landing Guy.mp3");
                //test
//                TransportService.upload(getViewContext(),true,list,meta,"./data",FMode.MODE_COVER,debug);
//                launchTransportList("ddd");
            }
        });
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data)  {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.id.fileBrowser_deviceNameTV:
                        return (null!=view&&view instanceof TextView&&showClientMenu((TextView)view,"After tap click."))||true;
                    case R.string.upload:
                        return upload(data,"After tap click.");
                    case R.string.transportList:
                        return launchTransportList("After transport list tap click.");
                    case R.drawable.selector_menu:
                        return showBrowserMenu(view,"After tap click.");
                    case R.drawable.selector_back:
                        return onBackIconPressed(view,"After back pressed.");
                    case R.drawable.cancel_selector:
                        BrowserModel model=getCurrentModel();
                        return null!=model&&(!model.isMode(BrowserModel.MODE_NORMAL)&&model.entryMode(BrowserModel.MODE_NORMAL,"After cancel tap click."));
                    case R.drawable.choose_all_selector:
                        model=getCurrentModel();
                        BrowserAdapter adapter=null!=model&&model.isMode(BrowserModel.MODE_MULTI_CHOOSE)?model.getBrowserAdapter():null;
                        return null!=adapter&&adapter.chooseAll(true);
                    case R.drawable.ic_menu_alls:
                        model=getCurrentModel();
                        adapter=null!=model&&model.isMode(BrowserModel.MODE_MULTI_CHOOSE)?model.getBrowserAdapter():null;
                        return null!=adapter&&adapter.chooseAll(false);
                    default:
                        break;
                }
                break;
            case 2:
                switch (resId){
                    case R.id.fileBrowser_deviceNameTV:
                        return (null!=view&&null!=data&&data instanceof ClientMeta&&showClientDetail(view,(ClientMeta)data,"After tap click."))||true;
                }
        }
        BrowserModel model=getCurrentModel();
        return null!=model&&model.onTapClick(view,clickCount,resId,data);
    }

    private boolean upload(Object obj,String debug){
        Object processing=mProcessing;
        LocalFile localFile=null!=processing&&processing instanceof LocalFile?(LocalFile)processing:null;
        String localFilePath=null!=localFile?localFile.getPath():null;
        FolderData folderData=mCurrentFolder.get();
        String folder=null!=folderData?folderData.getPath():null;
        ClientMeta client=getCurrentModelMeta();
        if (null!=folder&&null!=localFile&&null!=client){
            if (client.isLocalClient()){
                return toast(R.string.canNotOperateOnLocalDevice);
            }
            return TransportService.upload(getContext(),true,localFilePath,client,folder,null, CoverMode.COVER_MODE_NONE,debug);
        }
        return true;
    }

    private boolean showClientDetail(View view,ClientMeta meta,String debug){
        ClientDetailBinding binding=null!=view&&null!=meta?inflate(R.layout.client_detail):null;
        if (null!=binding){
            binding.setClient(meta);
            return showAtLocation(view,binding,Gravity.CENTER,0,0,null);
        }
        return false;
    }

    private boolean showClientMenu(TextView tv,String debug){
        Map<String,Object> map=mAllClientMetas;
        Context context=null!=tv?tv.getContext():null;
        Set<String> set=null!=map?map.keySet():null;
        final int size=null!=context&&null!=set?set.size():0;
        if (size<=1){
            toast(R.string.canNotSwitch);
            return false;
        }
        LinearLayout ll=new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        final OnTapClick click=( view, clickCount, resId, data)-> {
            return (null!=data&&data instanceof ClientMeta&&changeDevice((ClientMeta)data,true,"After device choose."))||true;
        };
        BrowserModel currentModel=mCurrent.get();
        ClientMeta current=null!=currentModel?currentModel.getClientMeta():null;
        for (String child:set) {
            Object object= null!=child?map.get(child):null;
            object=null!=object?object instanceof BrowserModel?((BrowserModel)object).getClientMeta():object:null;
            ClientMeta meta=null!=object&&object instanceof ClientMeta?(ClientMeta)object:null;
            if (null!=meta&&(null==current||!current.equals(meta))){
                DeviceTextBinding binding=inflate(R.layout.device_text);
                View root=null!=binding?binding.getRoot():null;
                if (null!=root){
                    binding.setDevice(meta);
                    ll.addView(root);
                }
                continue;
            }
        }
        return showAsDropDown(tv,ll,0,0,click,null);
    }

    private boolean showBrowserMenu(View view,String debug){
        FileBrowserMenuBinding binding=null!=view?inflate(R.layout.file_browser_menu):null;
        if (null!=binding){
            binding.setFolder(mCurrentFolder.get());
            binding.setClient(getCurrentModelMeta());
            return showAtLocationAsContext(view,binding);
        }
        return false;
    }

    private boolean launchTransportList(String debug){
        Context context=getContext();
        if (null!=context){
            Intent intent=new Intent(context, ConveyorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public Collection<Object> getAllClients() {
        Map<String,Object> metas=mAllClientMetas;
        return null!=metas?metas.values():null;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        BrowserModel model=getCurrentModel();
        return null!=model&&model instanceof OnLongClick&&((OnLongClick)model).onLongClick(view,clickCount,resId,data);
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        BrowserModel model=getCurrentModel();
        return null!=model&&model instanceof OnActivityBackPress&&((OnActivityBackPress)model).onActivityBackPressed(activity);
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
          BrowserModel model=getCurrentModel();
          if (null!=model&&model instanceof OnActivityResume){
              ((OnActivityResume)model).onActivityResume(activity,intent);
          }
    }

    private boolean onBackIconPressed(View view,String debug){
        BrowserModel model=getCurrentModel();
        return null!=model&&model.onBackIconPressed(view,debug);
    }

    private ClientMeta getCurrentModelMeta(){
        BrowserModel model=getCurrentModel();
        return null!=model?model.getClientMeta():null;
    }

    private BrowserModel getCurrentModel(){
       return mCurrent.get();
    }

    public ObservableField<Integer> getMode() {
        return mCurrentMode;
    }

    public ObservableField<FolderData> getCurrentFolder(){
        return mCurrentFolder;
    }

    public ObservableField<ListAdapter> getCurrentAdapter() {
        return mCurrentAdapter;
    }

    public ObservableField<ClientMeta> getCurrentMeta() {
        return mCurrentMeta;
    }

    public ObservableField<String> getCurrentMultiChooseSummary() {
        return mCurrentMultiChooseSummary;
    }

    public ObservableField<Integer> getClientCount() {
        return mClientCount;
    }
}
