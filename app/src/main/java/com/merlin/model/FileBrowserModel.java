package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import com.merlin.activity.ConveyorActivity;
import com.merlin.adapter.ListAdapter;
import com.merlin.api.Address;
import com.merlin.api.CoverMode;
import com.merlin.api.Label;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.Document;
import com.merlin.bean.FolderData;
import com.merlin.bean.LocalFile;
import com.merlin.bean.NasFile;
import com.merlin.browser.Collector;
import com.merlin.browser.Mode;
import com.merlin.client.R;
import com.merlin.client.databinding.ClientDetailBinding;
import com.merlin.client.databinding.DeviceTextBinding;
import com.merlin.client.databinding.FileBrowserMenuBinding;
import com.merlin.client.databinding.FileContextMenuBinding;
import com.merlin.client.databinding.SingleEditTextBinding;
import com.merlin.conveyor.ConveyorBinder;
import com.merlin.conveyor.ConveyorService;
import com.merlin.conveyor.FileUploadConvey;
import com.merlin.conveyor.LocalFileUploadConvey;
import com.merlin.debug.Debug;
import com.merlin.browser.FileBrowser;
import com.merlin.browser.LocalFileBrowser;
import com.merlin.browser.NasFileBrowser;
import com.merlin.dialog.Dialog;
import com.merlin.protocol.Tag;
import com.merlin.server.Retrofit;
import com.merlin.transport.Status;
import com.merlin.transport.TransportBinder;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class FileBrowserModel extends Model implements Label, Tag, OnTapClick, Model.OnBindChange, OnLongClick, Model.OnActivityResume,Model.OnActivityBackPress {
    private final Map<String,FileBrowser> mAllClientMetas=new HashMap<>();
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
    private final ObservableField<FileBrowser> mCurrent=new ObservableField<>();
    private final ObservableField<FolderData> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mCurrentMode=new ObservableField<>(FileBrowser.MODE_NORMAL);
    private final ObservableField<ListAdapter> mCurrentAdapter=new ObservableField<>();
    private final ObservableField<ClientMeta> mCurrentMeta=new ObservableField<>();
    private final ObservableField<Integer> mCurrentMultiChooseCount=new ObservableField<>();
    private ConveyorBinder mTransportBinder;

    private final FileBrowser.Callback mBrowserCallback=new FileBrowser.Callback() {
        @Override
        public void onFolderPageLoaded(PageData page, String debug) {
            mCurrentFolder.set(null!=page&&page instanceof FolderData?(FolderData)page:null);
        }

        @Override
        public boolean onTapClick(View view, int clickCount, int resId, Object data) {
            return FileBrowserModel.this.onTapClick(view,clickCount,resId,data);
        }
    };

    private Collector mProcessing;

    private interface Api{
        @POST(Address.PREFIX_USER+"/client/meta")
        Observable<Reply<ClientMeta>> queryClientMeta();

        @POST(Address.PREFIX_FILE+"/sync/check")
        @FormUrlEncoded
        Observable<Reply> checkFileSync(@Field(Label.LABEL_MD5) String md5s);
    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        putClientMeta(ClientMeta.buildLocalClient(getContext()), "After mode create.");
        ClientMeta testClient=new ClientMeta("算法","",Address.URL,"",null,"///");
//        ClientMeta testClient=new ClientMeta("算法","/volume1",Address.URL,null,"","/");
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
            Map<String,FileBrowser> list=mAllClientMetas;
            Debug.D(getClass(),"Put client "+url+" "+(null!=debug?debug:"."));
            boolean local=meta.isLocalClient();
            list.put(url,local?new LocalFileBrowser(meta,mBrowserCallback): new NasFileBrowser(meta,mBrowserCallback));
            mClientCount.set(list.size());
            changeDevice(meta,false,"After client put "+(null!=debug?debug:"."));
            return true;
        }
        Debug.W(getClass(),"Can't put client meta "+(null!=debug?debug:"."));
        return false;
    }

    private boolean changeDevice(ClientMeta client, boolean force, String debug){
        final String url=null!=client?client.getUrl():null;
        if (null!=url&&url.length()>0){
            if (force||null==mCurrent.get()){
                Debug.D(getClass(),"Change browser device "+client.getName()+" "+(null!=debug?debug:"."));
                Map<String,FileBrowser> map=mAllClientMetas;
                if (null!=map){
                    FileBrowser browser=map.get(url);
                    if (null!=browser){
                         browser.setMultiCollector(isMode(Mode.MODE_MULTI_CHOOSE)?mProcessing:null,"While browser switched.");
                         mCurrentAdapter.set(browser);
                         PageData page=null!=browser?browser.getLastPage():null;
                         mCurrentFolder.set(null!=page&&page instanceof FolderData?(FolderData)page:null);
                         if (null==page){
                             browser.loadPage(client.getRoot(),"While browser switched.");
                         }
                         mCurrentMeta.set(browser.getMeta());
                         mCurrent.set(browser);
                         return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data)  {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.string.open:
                        return openPath(data,"After tao click.")||true;
                    case R.id.fileBrowser_deviceNameTV:
                        return (null!=view&&view instanceof TextView&&showClientMenu((TextView)view, "After tap click."))||true;
                    case R.string.detail:
                        return showFileDetail(data,"After detail tap click.");
                    case R.string.createFile:
                        return createPath(false,"After create file tap click.");
                    case R.string.createFolder:
                        return createPath(true,"After create folder tap click.");
                    case R.string.setAsHome:
                        return setAsHome(data,"After set as home tap click.");
                    case R.string.rename:
                        return null!=data&&data instanceof Document &&renamePath((Document)data, CoverMode.NONE,"After rename tap click.");
                    case R.string.transportList:
                        return launchTransportList("After transport list tap click.");
                    case R.string.multiChoose:
                        return (!isMode(Mode.MODE_MULTI_CHOOSE)&&entryMode(Mode.MODE_MULTI_CHOOSE, new Collector(null,null),"After multi choose tap click."))||true;
                    case R.string.goTo:
                        return launchGoTo("After tap click");
                    case R.string.exit:
                        return finishActivity("After tap click.")||true;
                    case R.drawable.selector_menu:
                        return showBrowserMenu(view,"After tap click.");
                    case R.drawable.selector_back:
                        return onBackIconPressed(view,"After back pressed.");
                    case R.drawable.cancel_selector:
                        return !isMode(FileBrowser.MODE_NORMAL)&&entryMode(FileBrowser.MODE_NORMAL,null, "After cancel tap click.");
                    case R.drawable.selector_choose_all:
                        return chooseAll(true,"After tap click.");
                    case R.drawable.selector_choose_none:
                        return chooseAll(false,"After tap click.");
                    default:
                        break;
                }
                break;
            case 2:
                 switch (resId){
                     case R.id.fileBrowser_deviceNameTV:
                         return (null != view && null != data && data instanceof ClientMeta &&
                                 showClientDetail(view, (ClientMeta) data, "After tap click.")) || true;
                         default:
                             if (null!=data&&data instanceof Document){
                                 return showFileContextMenu((Document)data,"After 2 tap click.");
                             }
                         break;
                 }
        }
        switch (resId){
            case R.string.move:
                return isMode(Mode.MODE_MOVE) ? move(getCollected(null),CoverMode.tapClickCountToMode(clickCount), "After tap click.")
                        : collectFile(Mode.MODE_MOVE, data, null,"After tap click.");
            case R.string.upload:
                return isMode(Mode.MODE_UPLOAD) ? upload(getCollected(LocalFile.class),CoverMode.tapClickCountToMode(clickCount), "After tap click.")
                        : collectFile(Mode.MODE_UPLOAD, data, LocalFile.class,"After tap click.");
            case R.string.download:
                return isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_DOWNLOAD) ? download(getCollected(NasFile.class),CoverMode.tapClickCountToMode(clickCount), "After tap click.")
                        : collectFile(Mode.MODE_DOWNLOAD, data, NasFile.class,"After tap click.");
            case R.string.copy:
                return isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_COPY) ? copy(getCollected(null),CoverMode.tapClickCountToMode(clickCount), "After tap click.")
                        : collectFile(Mode.MODE_COPY, data, null,"After tap click.");
            case R.string.delete:
                return (isMode(Mode.MODE_MULTI_CHOOSE)?deletePaths(getCollected(null),"After delete tap click")&&entryMode(Mode.MODE_NORMAL,null,"After delete tap click"):
                        deletePath(data,"After delete tap click"))||true;
            default:
                if (clickCount==1){
                    if (null != data && data instanceof Document) {
                        Document file = (Document) data;
                        FileBrowser browser=getCurrentModel();
                        if (isMode(Mode.MODE_MULTI_CHOOSE)) {
                            return (null!=browser&&browser.multiChoose(data,null,"After tap click.")&&
                                    (refreshMultiSummary("After multi choose tap click")||true))||(toast(R.string.fail)&&false);
                        } else if (file.isAccessible()) {
                            return null!=browser&&(file.isDirectory()? browser.browserPath(file.getPath(null),
                                    "After directory click."):openPath(data, "After item tap click."));
                        } else {
                            toast(R.string.nonePermission);
                        }
                        return true;
                    }
                }
        }
        FileBrowser model=getCurrentModel();
        return null!=model&&model.onTapClick(view,clickCount,resId,data);
    }

    private boolean copy(ArrayList<Document> files,int coverMode, String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }else if (isMode(Mode.MODE_COPY)){
            FileBrowser browser=getCurrentModel();
            FolderData data=mCurrentFolder.get();
            return null!=browser&&browser.copyPaths(files,null!=data?data.getPath():null,coverMode,debug)&&
                    entryMode(Mode.MODE_NORMAL,null,"After start copy files "+(null!=debug?debug:"."));
        }
        return entryMode(Mode.MODE_COPY,new Collector(files),"While copy files "+(null!=debug?debug:"."));
    }

    private boolean move(ArrayList<Document> files,int coverMode,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }else if (isMode(Mode.MODE_MOVE)){
            FileBrowser browser=getCurrentModel();
            FolderData data=mCurrentFolder.get();
            return null!=browser&&browser.movePaths(files,null!=data?data.getPath():null,coverMode,debug)&& entryMode(Mode.MODE_NORMAL,null,"After start move files "+(null!=debug?debug:"."));
        }
        return entryMode(Mode.MODE_MOVE,new Collector(files),"While start move files "+(null!=debug?debug:"."));
    }

//    private boolean applyCollectedFiles(int mode,ArrayList<FileMeta> files,int coverMode,String debug){
//        if (null==files||files.size()<=0){
//            return toast(R.string.noneDataToOperate)&&false;
//        }else if (isMode(mode)){
//            FileBrowser browser=getCurrentModel();
//            FolderData data=mCurrentFolder.get();
//            return null!=browser&&browser.executePathsModify(mode,files,null!=data?data.getPath():null,coverMode,debug)&&entryMode(Mode.MODE_NORMAL,null,"After start apply collect files "+(null!=debug?debug:"."));
//        }
//        return entryMode(mode,new Collector(files),"While start apply collect files "+(null!=debug?debug:"."));
//    }

    private boolean upload(ArrayList<LocalFile> files,int coverMode,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        FolderData folder=mCurrentFolder.get();
        String folderPath=null!=folder?folder.getPath():null;
        ClientMeta meta=mCurrentMeta.get();
        if (null==folderPath||folderPath.length()<=0||null==meta||meta.isLocalClient()){
            toast(null==meta?R.string.canNotOperateHere:R.string.targetFolderInvalid);
            return false;
        }
        final ConveyorBinder binder=mTransportBinder;
        if (null==binder){
            toast(R.string.serverUnConnect);
            return false;
        }
        if (binder.run(Status.ADD,null,"While upload file.",new LocalFileUploadConvey(files,meta.getUrl()
                ,folder.getPath(),coverMode))){
            entryMode(FileBrowser.MODE_NORMAL,null,"After upload start succeed.");
            return toast(R.string.succeed)||true;
        }
        return toast(R.string.fail);
    }

    private boolean download(ArrayList<NasFile> files,int coverMode,String debug){
        Debug.D(getClass(),"下载 "+files);
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        FolderData folder=mCurrentFolder.get();
        String folderPath=null!=folder?folder.getPath():null;
        ClientMeta meta=mCurrentMeta.get();
        if (null==folderPath||folderPath.length()<=0||null==meta||!meta.isLocalClient()){
            toast(null==meta?R.string.canNotOperateHere:R.string.targetFolderInvalid);
            return false;
        }
        if (ConveyorService.download(getViewContext(),files,meta,folderPath,coverMode,debug)){
            entryMode(FileBrowser.MODE_NORMAL,null,"After download start succeed.");
            launchTransportList("");
            return toast(R.string.succeed)||true;
        }
        return toast(R.string.fail);
    }

    private boolean collectFile(int mode,Object obj,Class<? extends Document> targetCls,String debug){
        Collector collector=mProcessing;
        return (isMode(mode)||entryMode(mode,(null==collector||!collector.isTargetClassEqual(targetCls))?
                new Collector(targetCls):collector,debug))&&addToCollector(obj,debug);
    }

    private <T extends Document>ArrayList<T> getCollected(Class<T> cls){
        Collector collector = mProcessing;
        return null!=collector?collector.getFiles(cls):null;
    }

    private boolean addToCollector(Object meta,String debug){
        Collector collector=null!=meta&&meta instanceof Document?mProcessing:null;
        boolean succeed=null!=collector&&collector.add((Document) meta,debug);
        return succeed?true:(toast(R.string.fail)&&false);
    }

    private boolean openPath(Object object,String debug){
        FileBrowser browser=null!=object&&object instanceof Document?getCurrentModel():null;
        boolean succeed=null!=browser&&browser.openPath((Document)object,debug);
        return succeed||(toast(R.string.fail)&&false);
    }

    private boolean chooseAll(boolean choose,String debug){
        FileBrowser browser=getCurrentModel();
        return (null!=browser&&isMode(FileBrowser.MODE_MULTI_CHOOSE)&& browser.chooseAll(choose,debug)
                &&refreshMultiSummary("After choose all change succeed."));
    }

    private boolean launchGoTo(String debug){
        final Dialog dialog=new Dialog(getViewContext());
        SingleEditTextBinding binding=inflate(R.layout.single_edit_text);
        return dialog.setContentView(binding,true).title(R.string.goTo).left(R.string.sure).right(R.string.cancel).show(
                (View view, int clickCount, int resId, Object data) ->{
                    switch (resId){
                        case R.string.sure:
                            String text=binding.singleET.getText().toString();
                            if (null==text||text.length()<=0){
                                return toast(R.string.pathInvalid)||true;
                            }
                            return browserPath(text,"While from go to "+(null!=debug?debug:"."));
                    }
                    dialog.dismiss();
                return true;
        });
    }

    private boolean deletePath(Object data,String debug){
        if (null==data||!(data instanceof Document)){
            return toast(R.string.fail)&&false;
        }
        ArrayList<Document> list=new ArrayList<>(1);
        list.add((Document)data);
        return deletePaths(list,debug);
    }

    private boolean deletePaths(ArrayList<Document> files,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.deletePaths(files,debug);
    }

    private boolean renamePath(Document meta,int coverMode,String debug){
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.renamePath(meta,coverMode,debug);
    }

    private boolean createPath(boolean directory,String debug){
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.createPath(directory,debug);
    }

    private boolean showFileDetail(Object data,String debug){
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.showFileDetail(data,debug);
    }

    private boolean setAsHome(Object data,String debug){
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.setAsHome(data,debug);
    }

    public final boolean isMode(int ...models){
        if (null!=models&&models.length>0){
            int curr=mCurrentMode.get();
            for (int mode:models) {
                if (mode==curr){
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean entryMode(int mode,Collector collector,String debug){
        mProcessing=collector;//Clean processing while each mode change
        if (!isMode(mode)){
            mCurrentMode.set(mode);
            refreshMultiSummary("After entry multi mode.");
            FileBrowser browser=getCurrentModel();
            if (null!=browser&&browser.setMultiCollector(mode==Mode.MODE_MULTI_CHOOSE?collector:null,debug)){//Do nothing
            }
            return true;
        }
        return false;
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
        if (isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_COPY,Mode.MODE_MOVE)){
            return toast(R.string.currentModeNotSupport)&&false;
        }
        Map<String,FileBrowser> map=mAllClientMetas;
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
        FileBrowser currentModel=mCurrent.get();
        ClientMeta current=null!=currentModel?currentModel.getMeta():null;
        for (String child:set) {
            Object object= null!=child?map.get(child):null;
            object=null!=object?object instanceof FileBrowser ?((FileBrowser)object).getMeta():object:null;
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
            binding.setMode(getMode().get());
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

    private boolean refreshMultiSummary(String debug){
        Collector collector=mProcessing;
        int size=null!=collector?collector.size():-1;
        mCurrentMultiChooseCount.set(isMode(Mode.MODE_MULTI_CHOOSE)?(size<=0?0:size):-1);
        return true;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        FileBrowser model=getCurrentModel();
        return null!=model&&model instanceof OnLongClick&&((OnLongClick)model).onLongClick(view,clickCount,resId,data);
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        return !isMode(FileBrowser.MODE_NORMAL)? entryMode(FileBrowser.MODE_NORMAL,null,"After activity  back press."):
                browserParent("After back pressed called.");
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
          FileBrowser model=getCurrentModel();
          if (null!=model&&model instanceof OnActivityResume){
              ((OnActivityResume)model).onActivityResume(activity,intent);
          }
    }

    public final boolean onBackIconPressed(View view, String debug){
        return browserParent(debug);
    }

    private boolean browserPath(String path,String debug){
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.browserPath(path,debug);
    }

    private boolean showFileContextMenu(Document meta,String debug){
        View view=getRoot();
        FileContextMenuBinding binding=null!=view&&null!=meta?DataBindingUtil.inflate(LayoutInflater.
                from(view.getContext()), R.layout.file_context_menu,null,false):null;
        if (null!=binding){
            binding.setMode(mCurrentMode.get());
            binding.setFile(meta);
            return showAtLocationAsContext(view,binding);
        }
        return false;
    }

    private boolean browserParent(String debug){
        FileBrowser browser=getCurrentModel();
        return null!=browser&&browser.browserParent(debug);
    }

    private ClientMeta getCurrentModelMeta(){
        FileBrowser model=getCurrentModel();
        return null!=model?model.getMeta():null;
    }

    private FileBrowser getCurrentModel(){
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

    public ObservableField<Integer> getCurrentMultiChooseCount() {
        return mCurrentMultiChooseCount;
    }

    public ObservableField<Integer> getClientCount() {
        return mClientCount;
    }

    @Override
    public boolean onBindChanged(Object obj, String debug) {
        mTransportBinder=null!=obj&&obj instanceof ConveyorBinder?(ConveyorBinder)obj:null;
        return true;
    }
}
