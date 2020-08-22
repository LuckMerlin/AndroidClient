package com.file.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.file.activity.TaskActivity;
import com.luckmerlin.databinding.text.OnEditActionChange;
import com.luckmerlin.databinding.text.OnTextChangeBefore;
import com.luckmerlin.databinding.text.OnTextChanged;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.merlin.adapter.ListAdapter;
import com.merlin.api.Label;
import com.merlin.api.PageData;
import com.merlin.bean.Folder;
import com.merlin.bean.Path;
import com.merlin.browser.Collector;
import com.merlin.browser.LocalFileBrowser;
import com.merlin.browser.Mode;
import com.merlin.browser.NasFileBrowser;
import com.merlin.click.OnLongClick;
import com.merlin.click.OnTapClick;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.file.R;
import com.merlin.file.databinding.ClientDetailBinding;
import com.merlin.file.databinding.DeviceTextBinding;
import com.merlin.file.databinding.FileBrowserMenuBinding;
import com.merlin.file.databinding.FileContextMenuBinding;
import com.merlin.file.databinding.SearchFolderBinding;
import com.merlin.file.databinding.SingleEditTextBinding;
import com.merlin.browser.FileBrowser;
import com.merlin.file.transport.FileTaskService;
import com.merlin.model.Model;
import com.merlin.model.OnServiceBindChange;
import com.merlin.server.Client;
import com.merlin.task.OnTaskUpdate;
import com.merlin.task.Task;
import com.merlin.task.TaskBinder;
import com.merlin.task.file.Cover;
import com.merlin.util.Preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class FileBrowserModel extends BaseModel implements Label, OnTapClick, OnViewClick,
        OnLongClick, Model.OnActivityResume,Model.OnActivityBackPress, OnTaskUpdate, OnServiceBindChange,Model.OnActivityIntentChange {
    private final Map<String, FileBrowser> mAllClientMetas=new HashMap<>();
    private final ObservableField<Integer> mClientCount=new ObservableField<>();
    private final ObservableField<FileBrowser> mCurrent=new ObservableField<>();
    private final ObservableField<Folder> mCurrentFolder=new ObservableField<>();
    private final ObservableField<Integer> mCurrentMode=new ObservableField<>(FileBrowser.MODE_NORMAL);
    private final ObservableField<ListAdapter> mCurrentAdapter=new ObservableField<>();
    private final ObservableField<Client> mCurrentMeta=new ObservableField<>();
    private final static int SELECT_RESULT_CODE=2020;
    private TaskBinder mTaskBinder;
    private final ObservableField<Integer> mCurrentMultiChooseCount=new ObservableField<>();
    private final FileBrowser.Callback mBrowserCallback=new FileBrowser.Callback() {
        @Override
        public void onFolderPageLoaded(String arg,PageData page, String debug) {
            mCurrentFolder.set(null!=page&&page instanceof Folder ?((Folder)page):null);
        }

        @Override
        public boolean onTapClick(View view, int clickCount, int resId, Object data) {
            return FileBrowserModel.this.onTapClick(view,clickCount,resId,data);
        }
    };

    private Collector mCollecting;

    @Override
    public void onViewClick(View view, int count, Object tag) {
        Debug.D(getClass(),"QQQQQQQQQQ  "+count+"  "+view+" "+tag);
    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
//        putClientMeta(ClientMeta.buildLocalClient(getContext()), "After mode create.");
        Client testClient1=new Client("算法",getServerUri(),"",null,"","/");
        Client testClient=Client.buildLocalClient(getContext());
        testClient.setHome(new Preference(getContext()).getString(LocalFileBrowser.LABEL_HOME,"/sdcard"));
//        Client testClient2=new ClientMeta("算法","/volume1",Address.URL,null,"","/");
//        putClientMeta(testClient1, "After mode create.");
        putClientMeta(testClient, "After mode create.");
        //
//        refreshClientMeta("After
//        mode create.");
//        call(prepare(Test.class, null).delete("linqinagMD5"));
    }

    private boolean putClientMeta(Client meta,String debug){
        String host=null!=meta?meta.getHost():null;
        if (null!=host&&host.length()>0){
            Map<String,FileBrowser> list=mAllClientMetas;
            Debug.D(getClass(),"Put client "+host+" "+(null!=debug?debug:"."));
            boolean local=meta.isLocalClient();
            list.put(host,local?new LocalFileBrowser(meta,mBrowserCallback): new NasFileBrowser(meta,mBrowserCallback));
            mClientCount.set(list.size());
            changeDevice(meta,false,"After client put "+(null!=debug?debug:"."));
            return true;
        }
        Debug.W(getClass(),"Can't put client meta "+(null!=debug?debug:"."));
        return false;
    }

    private boolean changeDevice(Client client, boolean force, String debug){
        final String host=null!=client?client.getHost():null;
        if (null!=host&&host.length()>0){
            if (force||null==mCurrent.get()){
                Map<String,FileBrowser> map=mAllClientMetas;
                if (null!=map){
                    FileBrowser browser=map.get(host);
                    if (null!=browser){
                         browser.setMultiCollector(isMode(Mode.MODE_MULTI_CHOOSE)?mCollecting:null,"While browser switched.");
                         mCurrentAdapter.set(browser);
                         Debug.D(getClass(),"Change browser device "+client.getName()+" "+(null!=debug?debug:"."));
                         PageData page=null!=browser?browser.getLastPage():null;
                         mCurrentFolder.set(null!=page&&page instanceof Folder ?(Folder)page:null);
                         if (null==page){
                             browser.loadPage(client.getHome(),"While browser switched.");
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
                        return null!=data&&data instanceof Path &&renamePath((Path)data, Cover.COVER_NONE,"After rename tap click.");
                    case R.string.transportList:
                        return startActivity(TaskActivity.class,"After transport list tap click.");
                    case R.string.multiChoose:
                        return (!isMode(Mode.MODE_MULTI_CHOOSE)&&entryMode(Mode.MODE_MULTI_CHOOSE,
                                new Collector(null,null,null),"After multi choose tap click."))||true;
                    case R.string.goTo:
                        return launchGoTo("After tap click");
                    case R.string.exit:
                        return finishActivity("After tap click.")||true;
                    case R.string.terminal:
                        return addTerminal("After tap click")||true;
                    case R.string.reboot:
                        return rebootClient("After reboot tap click.");
                    case R.drawable.selector_menu:
                        return showBrowserMenu(view,"After tap click.");
                    case R.drawable.selector_back:
                        return onBackIconPressed(view,"After back pressed.");
                    case R.drawable.selector_cancel:
                        return !isMode(FileBrowser.MODE_NORMAL)&&entryMode(FileBrowser.MODE_NORMAL,null, "After cancel tap click.");
                    case R.drawable.selector_choose_all:
                        return chooseAll(true,"After tap click.");
                    case R.drawable.selector_choose_none:
                        return chooseAll(false,"After tap click.");
                    case R.string.sure:
                        if (isMode(Mode.MODE_SELECT)){
                           Collector collecting=mCollecting;
                           ArrayList<Parcelable> list=new ArrayList<>();
                           if (null!=collecting&&collecting.size()>0){
                               for (Object child: collecting){
                                    if (null!=child&&child instanceof Parcelable){
                                        list.add((Parcelable)child);
                                    }
                               }
                           }
                           return finishActivity(SELECT_RESULT_CODE,null!=list&&list.size()>0?new Intent().
                                   putParcelableArrayListExtra(Label.LABEL_DATA,list):null,"After select mode sure click.")||true;
                        }
                        return true;
                    default:
                        break;
                }
                break;
            case 2:
                 switch (resId){
                     case R.id.fileBrowser_deviceNameTV:
                         return (null != view && null != data && data instanceof Client &&
                                 showClientDetail(view, (Client) data, "After tap click.")) || true;
                     case R.drawable.selector_menu:
                         return searchCurrentFolder("After tap click.");
                         default:
                             if (null!=data&&data instanceof Path){
                                 return showFileContextMenu((Path)data,"After 2 tap click.");
                             }
                         break;
                 }
            case 3:
                switch (resId){
                    case R.drawable.selector_menu:
                        return startActivity(TaskActivity.class,"After menu double tap click.");
                }

        }
        switch (resId){
            case R.string.move:
                return isMode(Mode.MODE_MOVE) ? movePaths(getCollected(null),Cover.tapClickCountToMode(clickCount), "After tap click.")
                        &&entryMode(Mode.MODE_NORMAL,null,"After move process."): collectFile(Mode.MODE_MOVE, data, null,"After tap click.");
            case R.string.upload:
                return isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_UPLOAD) ? uploadPaths(getCollected(Path.class),Cover.tapClickCountToMode(clickCount), "After tap click.")
                        &&entryMode(Mode.MODE_NORMAL,null,"After upload process."): collectFile(Mode.MODE_UPLOAD, data, Path.class,"After tap click.");
            case R.string.download:
                return isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_DOWNLOAD) ? downloadPaths(getCollected(Path.class),Cover.tapClickCountToMode(clickCount), "After tap click.")
                        &&entryMode(Mode.MODE_NORMAL,null,"After download process."): collectFile(Mode.MODE_DOWNLOAD, data, Path.class,"After tap click.");
            case R.string.copy:
                return isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_COPY) ? copy(getCollected(null),Cover.tapClickCountToMode(clickCount), "After tap click.")
                        &&entryMode(Mode.MODE_NORMAL,null,"After copy process."): collectFile(Mode.MODE_COPY, data, null,"After tap click.");
            case R.string.delete:
                return (isMode(Mode.MODE_MULTI_CHOOSE)?deletePaths(getCollected(null),"After delete tap click")
                        &&entryMode(Mode.MODE_NORMAL,null,"After delete tap click"):
                        deletePath(data,"After delete tap click"))||true;
            default:
                if (clickCount==1){
                    if (null != data && data instanceof Path) {
                        Path file = (Path) data;
                        FileBrowser browser=getCurrentBrowser();
                        if (isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_SELECT)) {
                            return (null!=browser&&browser.multiChoose(data,null,"After tap click.")&&
                                    (refreshMultiSummary("After multi choose tap click")||true))||(toast(R.string.fail)&&false);
                        } else if (file.isAccessible()) {
                            return null!=browser&&(file.isDirectory()? browser.browserPath(file.getPath(),
                                    "After directory click."):openPath(data, "After item tap click."));
                        } else {
                            toast(R.string.nonePermission);
                        }
                    }
                }
        }
        FileBrowser model=getCurrentBrowser();
        return null!=model&&model.onTapClick(view,clickCount,resId,data);
    }

    private boolean addTerminal(String debug){
        ViewDataBinding binding=null;
        return new Dialog(getViewContext()).setContentView(binding,true).show((View view, int clickCount, int resId, Object data) ->{
                return false;
        },true);
    }

    private boolean copy(ArrayList<Path> files,int coverMode, String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }else if (isMode(Mode.MODE_COPY)){
            FileBrowser browser=getCurrentBrowser();
            Folder folder=mCurrentFolder.get();
            Path folderPath=null!=folder?folder.getPath():null;
            if (null==folderPath||null==browser){
                Debug.D(getClass(),"Can't copy file which model NULL or target not folder.");
                return toast(R.string.fail)&&false;
            }
            return browser.copyPaths(files,folderPath,coverMode,null,debug)&&
                    entryMode(Mode.MODE_NORMAL,null,"After start copy files "+(null!=debug?debug:"."));
        }
        return entryMode(Mode.MODE_COPY,new Collector(files),"While copy files "+(null!=debug?debug:"."));
    }

    private boolean collectFile(int mode,Object obj,Class<? extends Path> targetCls,String debug){
        Collector collector=mCollecting;
        return (isMode(mode)||entryMode(mode,(null==collector||!collector.isTargetClassEqual(targetCls))?
                new Collector(null,targetCls):collector,debug))&&addToCollector(obj,debug);
    }

    private <T extends Path>ArrayList<T> getCollected(Class<T> cls){
        Collector collector = mCollecting;
        return null!=collector?collector.getFiles(cls):null;
    }

    private boolean addToCollector(Object meta,String debug){
        Collector collector=null!=meta&&meta instanceof Path ?mCollecting:null;
        boolean succeed=null!=collector&&collector.add((Path) meta,debug);
        return succeed?true:(toast(R.string.fail)&&false);
    }

    private boolean openPath(Object object,String debug){
        FileBrowser browser=null!=object&&object instanceof Path?getCurrentBrowser():null;
        boolean succeed=null!=browser&&browser.openPath((Path)object,debug);
        return succeed||(toast(R.string.fail)&&false);
    }

    private boolean chooseAll(boolean choose,String debug){
        FileBrowser browser=getCurrentBrowser();
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

    private boolean searchCurrentFolder(String debug){
        Dialog dialog=new Dialog(getViewContext());
        ViewDataBinding binding=DataBindingUtil.inflate(LayoutInflater.from(getViewContext()),R.layout.search_folder,null,false);
        if (null!=binding&&binding instanceof SearchFolderBinding){
            SearchFolderBinding folderBinding=((SearchFolderBinding)binding);
//            folderBinding.setOnTextChanged(new Test());
            return dialog.setDimAmount(0).setContentView(binding,false).show();
        }
        return false;
    }

    private boolean deletePath(Object data,String debug){
        if (null==data||!(data instanceof Path)){
            return toast(R.string.fail)&&false;
        }
        ArrayList<Path> list=new ArrayList<>(1);
        list.add((Path)data);
        return deletePaths(list,debug);
    }

    private boolean uploadPaths(ArrayList<Path> files,int coverMode,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        Path currentFolder=getCurrentFolderPath();
        if (null==currentFolder||currentFolder.isLocal()){
            return toast(R.string.noneSupportOpenFileType)&&false;
        }
        return FileTaskService.uploadPaths(getContext(),files,currentFolder,coverMode,debug);
    }

    private boolean downloadPaths(ArrayList<Path> files,int coverMode,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        Path currentFolder=getCurrentFolderPath();
        if (null==currentFolder||!currentFolder.isLocal()){
            return toast(R.string.noneSupportOpenFileType)&&false;
        }
        return FileTaskService.downloadPaths(getContext(),files,currentFolder,coverMode,debug);
    }

    private boolean movePaths(ArrayList<Path> files,int coverMode,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        Path currentFolder=getCurrentFolderPath();
        FileBrowser browser=null!=currentFolder?getCurrentBrowser():null;
        if (null==browser){
            return toast(R.string.noneSupportOpenFileType)&&false;
        }
        return browser.movePaths(files,currentFolder,coverMode,null,debug);
    }

    private boolean deletePaths(ArrayList<Path> files,String debug){
        if (null==files||files.size()<=0){
            return toast(R.string.noneDataToOperate)&&false;
        }
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.deletePath(files,null,debug);
    }

    private boolean renamePath(Path meta,int coverMode,String debug){
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.renamePath(meta,coverMode,debug);
    }

    private boolean createPath(boolean directory,String debug){
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.createPath(directory,debug);
    }

    private boolean showFileDetail(Object data,String debug){
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.showFileDetail(data,debug);
    }

    private boolean setAsHome(Object data,String debug){
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.setAsHome(data,debug);
    }

    private boolean rebootClient(String debug){
        FileBrowser browser=getCurrentBrowser();
        Dialog dialog=new Dialog(getViewContext());
        return null!=browser&&dialog.create().title(R.string.reboot).left(R.string.sure).
                right(R.string.cancel).show((view, clickCount, resId, data)-> {
            if (resId==R.string.sure){
                browser.reboot(debug);
            }
            dialog.dismiss();
            return true;
        });
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
        mCollecting=collector;//Clean processing while each mode change
        if (!isMode(mode)){
            mCurrentMode.set(mode);
            boolean multiMode=mode==Mode.MODE_MULTI_CHOOSE||mode==Mode.MODE_SELECT;
            refreshMultiSummary("After entry multi mode.");
            FileBrowser browser=getCurrentBrowser();
            if (null!=browser&&browser.setMultiCollector(multiMode?collector:null,debug)){//Do nothing

            }
            return true;
        }
        return false;
    }

    private boolean showClientDetail(View view,Client meta,String debug){
        ClientDetailBinding binding=null!=view&&null!=meta?inflate(R.layout.client_detail):null;
        if (null!=binding){
            binding.setClient(meta);
            return showAtLocation(view,binding,Gravity.CENTER,0,0,null);
        }
        return false;
    }

    protected final Path getCurrentFolderPath(){
        ObservableField<Folder> currentFolder=getCurrentFolder();
        Folder folder=null!=currentFolder?currentFolder.get():null;
        return null!=folder?folder.getPath():null;
    }

    private boolean showClientMenu(TextView tv,String debug){
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
            return (null!=data&&data instanceof Client&&changeDevice((Client)data,true,"After device choose."))||true;
        };
        FileBrowser currentModel=mCurrent.get();
        Client current=null!=currentModel?currentModel.getMeta():null;
        for (String child:set) {
            Object object= null!=child?map.get(child):null;
            object=null!=object?object instanceof FileBrowser ?((FileBrowser)object).getMeta():object:null;
            Client meta=null!=object&&object instanceof Client?(Client)object:null;
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

    private boolean refreshMultiSummary(String debug){
        Collector collector=mCollecting;
        int size=null!=collector?collector.size():-1;
        mCurrentMultiChooseCount.set(isMode(Mode.MODE_MULTI_CHOOSE,Mode.MODE_SELECT)?(size<=0?0:size):-1);
        return true;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        FileBrowser model=getCurrentBrowser();
        return null!=model&&model instanceof OnLongClick&&((OnLongClick)model).onLongClick(view,clickCount,resId,data);
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        return !isMode(FileBrowser.MODE_NORMAL)? entryMode(FileBrowser.MODE_NORMAL,null,"After activity  back press."):
                browserParent("After back pressed called.");
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
          FileBrowser model=getCurrentBrowser();
          if (null!=model&&model instanceof OnActivityResume){
              ((OnActivityResume)model).onActivityResume(activity,intent);
          }
    }

    public final boolean onBackIconPressed(View view, String debug){
        return browserParent(debug);
    }

    private boolean browserPath(String path,String debug){
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.browserPath(path,debug);
    }

    private boolean showFileContextMenu(Path meta,String debug){
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
        FileBrowser browser=getCurrentBrowser();
        return null!=browser&&browser.browserParent(debug);
    }

    private Client getCurrentModelMeta(){
        FileBrowser model=getCurrentBrowser();
        return null!=model?model.getMeta():null;
    }


    private FileBrowser getCurrentBrowser(){
       return mCurrent.get();
    }

    public ObservableField<Integer> getMode() {
        return mCurrentMode;
    }

    public ObservableField<Folder> getCurrentFolder(){
        return mCurrentFolder;
    }

    public ObservableField<ListAdapter> getCurrentAdapter() {
        return mCurrentAdapter;
    }

    public ObservableField<Client> getCurrentMeta() {
        return mCurrentMeta;
    }

    public ObservableField<Integer> getCurrentMultiChooseCount() {
        return mCurrentMultiChooseCount;
    }

    public ObservableField<Integer> getClientCount() {
        return mClientCount;
    }

    @Override
    public void onServiceBindChanged(ComponentName name, IBinder service) {
        TaskBinder currBinder=mTaskBinder;
        TaskBinder binder=mTaskBinder=null!=service&&service instanceof TaskBinder?((TaskBinder)service):null;
        if (null==binder&&null!=currBinder){
            currBinder.remove(this);
        }else if (null!=binder){
            binder.put(this,null);
        }
    }

    @Override
    public void onTaskUpdate(int status, int what, String note, Object obj, Task task) {
        FileBrowser browser=getCurrentBrowser();
        if (null!=browser&&browser instanceof OnTaskUpdate){
            ((OnTaskUpdate)browser).onTaskUpdate(status,what,note,obj,task);
        }
    }

    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        if (null!=intent){
            String action=intent.getAction();
            if (null!=action){
                if (action.equals("merlin.intent.action.FILE_SELECT")){
                    int max=intent.getIntExtra(LABEL_SIZE,-1);
                    entryMode(Mode.MODE_SELECT,new Collector(max,null),"After activity intent changed.");
                }
            }
        }
    }
}
