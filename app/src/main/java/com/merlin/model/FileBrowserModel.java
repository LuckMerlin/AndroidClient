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
import androidx.databinding.ViewDataBinding;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.File;
import com.merlin.bean.NasFile;
import com.merlin.bean.FileModify;
import com.merlin.bean.FilePaste;
import com.merlin.bean.NasFolder;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.client.databinding.DeviceTextBinding;
import com.merlin.client.databinding.FileBrowserMenuBinding;
import com.merlin.client.databinding.FileContextMenuBinding;
import com.merlin.client.databinding.FileDetailBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.media.MediaPlayService;
import com.merlin.protocol.Tag;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;


import java.util.ArrayList;
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
    private final ObservableField<NasFolder> mCurrent=new ObservableField();
    private final ObservableField<ClientMeta> mCurrentClientMeta=new ObservableField<>();
    private final ObservableField<String> mMultiCount=new ObservableField<>();
    private List<ClientMeta> mAllClientMetas;
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>(false);
    private final ObservableField<Integer> mMode=new ObservableField<>();
    private Object mProcessing;
    public final static int MODE_NORMAL=1212;
    public final static int MODE_MULTI_CHOOSE=1213;
    public final static int MODE_COPY=1214;
    public final static int MODE_MOVE=1215;
    private final BrowserAdapter mBrowserAdapter=new BrowserAdapter(){
        @Override
        protected boolean onPageLoad(String path, int from, OnApiFinish<Reply<NasFolder>> finish) {
            return null!=path&&null!=call(Api.class,(OnApiFinish<Reply<NasFolder>>)(what, note, data, arg)->{
                if (what==WHAT_SUCCEED){
                    mCurrent.set(null!=data?data.getData():null);
                }
                if (null!=finish){
                  finish.onApiFinish(what,note,data,arg);
              }
            }).queryFiles(path, from,from+50);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<NasFolder>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                @Field(LABEL_TO) int to);
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
        Observable<Reply<NasFile>> getDetail(@Field(LABEL_PATH) String path);

        @POST(Address.PREFIX_FILE+"/scan")
        @FormUrlEncoded
        Observable<Reply<NasFile>> scan(@Field(LABEL_PATH) String path,@Field(LABEL_ENABLE) boolean recursive);

        @POST(Address.PREFIX_FILE+"/paste")
        @FormUrlEncoded
        Observable<Reply<ApiList<Reply<FilePaste>>>> pasteFile(@Field(LABEL_MODE) String mode,@Field(LABEL_PATH) List<FilePaste> paths);
    }

    private interface OnChooseExist{
        void onChooseExist(List<NasFile> list);
    }

    public FileBrowserModel(){
        entryMode(MODE_NORMAL);
        refreshClientMeta("While model create.");
        browserPath("","While model create.");
    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        addClientMeta(ClientMeta.buildLocalClient(getContext()),"After mode create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.id.fileBrowser_deviceNameTV:
                        return (null!=view&&view instanceof TextView&&showClientMenu((TextView)view,"After tap click."))||true;
                    case R.drawable.selector_menu:
                        return showBrowserMenu(view,"After tap click.");
                    case R.drawable.selector_back:
                        return browserParent("After back pressed.");
                    case R.id.fileBrowser_bottom_cancel_TV:
                        return cancel("After cancel tap click.");
                    case R.string.open:
                        return null!=data&&data instanceof File&&open((File)data,"After open tap click.");
                    case R.string.download:
                        return downloadFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After cancel tap click.");
                    case R.id.fileBrowser_bottom_paste_TV:
                        return pasteFileOnCurrent("After paste tap click.")&&entryMode(MODE_NORMAL);
                    case R.string.reboot:
                        return rebootClient("After reboot tap click.");
                    case R.string.copy:
                        return copyFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After copy tap click.");
                    case R.string.move:
                        return moveFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After move tap click.");
                    case R.string.createFile:
                         return createFile(false);
                    case R.string.createFolder:
                        return createFile(true);
                    case R.string.scan:
                        return null!=data&&data instanceof NasFile&&scan((NasFile)data,false)||true;
                    case R.string.upload:
                        return null!=data&&data instanceof NasFile &&uploadFile((NasFile)data);
                    case R.string.detail:
                        return null!=data&&data instanceof NasFile &&showFileDetail((NasFile)data);
                    case R.drawable.cancel_selector:
                        return !isMode(MODE_NORMAL)&&entryMode(MODE_NORMAL);
                    case R.drawable.choose_all_selector:
                        BrowserAdapter adapter=isMode(MODE_MULTI_CHOOSE)?mBrowserAdapter:null;
                        return null!=adapter&&adapter.chooseAll(true);
                    case R.drawable.ic_menu_alls:
                         adapter=isMode(MODE_MULTI_CHOOSE)?mBrowserAdapter:null;
                        return null!=adapter&&adapter.chooseAll(false);
                    case R.string.delete:
                        List<NasFile> list=null!=data&&data instanceof NasFile ?new ArrayList<>():null;
                        return null!=list&&list.add((NasFile)data)&&deleteFile(list,"After delete tap click.");
                    case R.string.rename:
                        return null!=data&&data instanceof NasFile &&renameFile((NasFile)data);
                    default:
                        if (null!=data){
                            if (data instanceof NasFile){
                                NasFile file=(NasFile)data;
                                if (isMode(MODE_MULTI_CHOOSE)) {
                                    multiChoose(file);
                                } else if(file.isAccessible()){
                                    if (file.isDirectory()) {
                                        browserPath(file.getPath(), "After directory click.");
                                    } else{//Open file
                                    }
                                }else{
                                    toast(R.string.nonePermission);
                                }
                            }else if (data instanceof File){
                                open((File)data,"After file item tap.");
                            }
                        }
                        break;
                }
                break;
            case 2:
                switch (resId){
                    default:
                        if (null!=data&&data instanceof NasFile){
                            FileContextMenuBinding  binding=DataBindingUtil.inflate(LayoutInflater.from(view.getContext()),R.layout.file_context_menu,null,false);
                            if (null!=binding){
                                binding.setFile((NasFile)data);
                                showAtLocationAsContext(view,binding);
                                return true;
                            }
                        }
                        break;
                }
        }
        return false;
    }

    private boolean addClientMeta(ClientMeta meta,String debug){
        if (null!=meta){
            List<ClientMeta> list=mAllClientMetas;
            list=null!=list?list:(mAllClientMetas=new ArrayList<>());
            Debug.D(getClass(),"添加  "+meta);
            if (!list.contains(meta)&&list.add(meta)){
                return changeDevice(meta,false,debug)||true;
            }
        }
        return false;
    }

    private boolean changeDevice(ClientMeta client,boolean force,String debug){
        if (null!=client){
            if (force||null==mCurrentClientMeta.get()){
                Debug.D(getClass(),"Change browser device "+client.getName()+" "+(null!=debug?debug:"."));
                mCurrentClientMeta.set(client);
                return true;
            }
        }
        return false;
    }

    private boolean showClientMenu(TextView tv,String debug){
        List<ClientMeta> list=mAllClientMetas;
        Context context=null!=tv?tv.getContext():null;
        final int size=null!=context&&null!=list?list.size():0;
        if (size>0){
            LinearLayout ll=new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            final OnTapClick click=( view, clickCount, resId, data)-> {
                return (null!=data&&data instanceof ClientMeta&&changeDevice((ClientMeta)data,true,"After device choose."))||true;
            };
            ClientMeta current=mCurrentClientMeta.get();
            for (int i = 0; i < size; i++) {
                ClientMeta meta=list.get(i);
                if (null!=meta&&(null==current||!current.equals(meta))){
                    DeviceTextBinding binding=inflate(R.layout.device_text);
                    View root=null!=binding?binding.getRoot():null;
                    if (null!=root){
                        binding.setDevice(meta);
                        ll.addView(root);
                    }
                }
            }
            return showAsDropDown(tv,ll,0,0,click,null);
        }
        return false;
    }

    private boolean showBrowserMenu(View view,String debug){
        FileBrowserMenuBinding binding=null!=view?inflate(R.layout.file_browser_menu):null;
        if (null!=binding){
            binding.setFolder(mCurrent.get());
            return showAtLocationAsContext(view,binding);
        }
        return false;
    }

    private boolean open(File file,String debug){
        String path=null!=file?file.getPath():null;
        if (null!=path&&path.length()>0){
            return  MediaPlayService.play(getContext(), file, 0, false);
        }
        toast(R.string.pathInvalid);
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.string.scan:
                return null!=data&&data instanceof NasFile&&scan((NasFile)data,true)||true;
        }
        if (null!=data&&data instanceof NasFile){
            return entryMode(MODE_MULTI_CHOOSE);
        }
        return false;
    }

    private boolean scan(NasFile file,boolean recursive){
        String path=null!=file?file.getPath():null;
        return null!=path&&path.length()>0&&null!=call(Api.class,(OnApiFinish<Reply>)(what, note, data2, arg)->toast(note)).scan(path,recursive);
    }

    private boolean downloadFile(NasFile meta, String debug){
        String path=null!=meta?meta.getPath():null;
        if (null!=path&&path.length()>0){
            ArrayList<NasFile> list=new ArrayList<>();
            list.add(meta);
            return downloadFile(list,debug);
        }
        toast(R.string.pathInvalid);
        return false;
    }

    private boolean downloadFile(ArrayList<NasFile> files, String debug){
        if (null==files||files.size()<=0){
            return false;
        }
//        return TransportService.download(getContext(),files,debug);
        return false;
    }

    private boolean uploadFile(NasFile meta){
        String path=null!=meta?meta.getPath():null;
        if (null==path||path.length()<=0 ||!meta.isDirectory()){
            toast(R.string.pathInvalid);
            return false;
        }
        return false;
    }

    private boolean cancel(String debug){
        if (!isMode(MODE_NORMAL)&&entryMode(MODE_NORMAL)){
            return true;
        }
        return false;
    }

    private boolean pasteFileOnCurrent(String debug){
        final int mode=mMode.get();
        if (mode== MODE_COPY||mode==MODE_MOVE){
            NasFolder folder=mCurrent.get();
            String folderPath=null!=folder?folder.getPath():null;
            if (null!=folderPath&&folderPath.length()>0){
                Object object=mProcessing;
                NasFile file=null!=object&&object instanceof NasFile ?(NasFile)object:null;
                String path=null!=file?file.getPath():null;
                if (null!=path&&path.length()>0){
                    List<FilePaste> list=new ArrayList<>();
                    list.add(new FilePaste(path,folderPath,What.WHAT_NORMAL));
                    return pasteFiles(mode,list,debug);
                }
                toast(R.string.pathInvalid);
                return false;
            }
            toast(R.string.pathInvalid);
            return false;
        }
        return false;
    }

    private boolean pasteFiles(int mode,List<FilePaste> processes, String debug){
        if(null!=processes&&processes.size()>0){
            String modeValue=mode== MODE_COPY?Label.LABEL_COPY:mode==MODE_MOVE?LABEL_MODE:null;
            if (null==modeValue){
                return false;
            }
            return null!=call(Api.class,(OnApiFinish<Reply<ApiList<Reply<FilePaste>>>>)(what,note,data,arg)->{
                  toast(note);
                  ApiList<Reply<FilePaste>> list=what!=WHAT_SUCCEED&&null!=data?data.getData():null;
                  if (null!=list&&list.size()>0){
                      for (Reply<FilePaste> child:list) {
                          if (null!=child&&child.isSuccess()){
                              FilePaste paste=child.getData();
//                              if (null!=paste){
//                                  Debug.D(getClass(),"QQQQQ "+paste.getMode()+" "+paste.getFrom()+" "+paste.getTo()+" ");
//                              }
                          }
                      }
                  }
            }).pasteFile(modeValue,processes);
        }
        return false;
    }

    private boolean moveFile(NasFile meta, String debug){
        if (null!=meta&&isMode(MODE_MOVE)||entryMode(MODE_MOVE)){
            mProcessing=meta;
            return true;
        }
        return false;
    }

    private boolean copyFile(NasFile meta, String debug){
        if (null!=meta&&isMode(MODE_COPY)||entryMode(MODE_COPY)){
            mProcessing=meta;
            return true;
        }
        return false;
    }

    private boolean showFileDetail(NasFile meta){
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
        return null!=call(Api.class,(OnApiFinish<Reply<NasFile>>)(what, note, data2, arg)->{
            NasFile detail=what==WHAT_SUCCEED&&null!=data2?data2.getData():null;
            if (null!=detail){
                binding.setFile(detail);
            }
            binding.setLoadState(what);
        }).getDetail(path);
    }

    private boolean resetBrowserCurrentFolder(String debug){
        BrowserAdapter adapter=mBrowserAdapter;
        return null!=adapter&&adapter.reset(debug);
    }

    private boolean browserPath(String pathValue, String debug){
        BrowserAdapter adapter=mBrowserAdapter;
        return null!=adapter&&adapter.loadPage(pathValue,debug);
    }

    private boolean refreshClientMeta(String debug){
        Debug.D(getClass(),"Refresh client meta "+(null!=debug?debug:"."));
        return null!=call(Api.class,(OnApiFinish<Reply<ClientMeta>>)(what, note, data, arg)->{
            if(what==WHAT_SUCCEED){
                ClientMeta meta=null!=data?data.getData():null;
                addClientMeta(meta,"After client meta responed.");
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

    private boolean createFile(boolean dir){
        NasFolder folderMeta=mCurrent.get();
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

    private boolean renameFile(NasFile meta){
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

    private boolean deleteFile(List<NasFile> files, String debug){
        final int length=null!=files?files.size():-1;
        if (length>0){
            Dialog dialog=new Dialog(getViewContext());
            NasFile first=files.get(0);
            String name=null!=first?first.getName():null;
            String message=""+(length==1?(null!=name?(""+getText(first.isDirectory()?R.string.folder:R.string.file)+" "+name):""):getText(R.string.items,length));
            return dialog.create().title(R.string.delete).message(getText(R.string.deleteSure,message)).left(R.string.sure).right(R.string.cancel).show((view, clickCount,  resId, data)->{
                dialog.dismiss();
                if (resId ==R.string.sure){
                     List<String> paths=new ArrayList<>();
                     Map<String, NasFile> map=new HashMap<>(length);
                     for (NasFile meta:files) {
                         String path=null!=meta?meta.getPath():null;
                         if (null!=path&&path.length()>0){
                             paths.add(path);
                             map.put(path,meta);
                         }
                     }
                     if (null!=paths&&paths.size()>0){
                         return null!=call(Api.class,(OnApiFinish<Reply<ApiList<String>>>)(what,note,data3,arg)->{
                             toast(note);
                             if (what==WHAT_SUCCEED){
                                 List<String> deletedPaths=null!=data3?data3.getData():null;
                                 BrowserAdapter adapter=mBrowserAdapter;
                                 int size=null!=deletedPaths&&null!=adapter?deletedPaths.size():-1;
                                 if (size>0){
                                     List<NasFile> deleted=new ArrayList<>(size);
                                     for (String  path:deletedPaths) {
                                         NasFile child=null!=path?map.get(path):null;
                                         if (null!=child){
                                             deleted.add(child);
                                         }
                                     }
                                     adapter.remove(deleted,debug);
                                 }
                             }
                         }).deleteFile(paths);
                     }
                 }
                return true;
            },false);
        }
        Debug.D(getClass(),"Can't delete file.");
        return false;
    }

    private boolean browserParent(String debug){
        NasFolder current=mCurrent.get();
        String parent=null!=current?current.getParent():null;
        String curr=null!=current?current.getPath():null;
        if (null==parent||parent.length()<=0||(null!=curr&&curr.equals(parent))){
            toast(R.string.alreadyArrivedRoot);
            return false;
        }
        return browserPath(parent,debug);
    }

    private boolean isMode(int mode){
        ObservableField<Integer> current=mMode;
        Integer curr=null!=current?current.get():null;
        return null!=curr&&mode==curr;
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        if (!isMode(MODE_NORMAL)){
            return entryMode(MODE_NORMAL);
        }
        return browserParent("After back pressed called.");
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
        refreshCurrentPath("After activity onResume.");
    }

    private final boolean refreshCurrentPath(String debug){
        NasFolder meta=mCurrent.get();
        return browserPath(null!=meta?meta.getPath():null,debug);
    }

    private boolean entryMode(int mode){
        if (!isMode(mode)){
            mProcessing=null;
            mMode.set(mode);
            BrowserAdapter adapter=mBrowserAdapter;
            if (null!=adapter){
                adapter.setMode(mode);
            }
            switch (mode){
                case MODE_MULTI_CHOOSE:
                    return refreshMultiChooseCount();
                case MODE_COPY:
                    break;
                case MODE_MOVE:
                    break;
            }
            return true;
        }
        return false;
    }

    public final boolean chooseAll(boolean choose){
        BrowserAdapter adapter=mBrowserAdapter;
        if (isMode(MODE_MULTI_CHOOSE)&&null!=adapter&&adapter.chooseAll(choose)){
            refreshMultiChooseCount();
            return true;
        }
        return false;
    }

    public ObservableField<ClientMeta> getCurrentClientMeta() {
        return mCurrentClientMeta;
    }

    public ObservableField<Integer> getMode() {
        return mMode;
    }

    private boolean multiChoose(NasFile meta){
        BrowserAdapter adapter=mBrowserAdapter;
        if (null!=meta&&isMode(MODE_MULTI_CHOOSE)&&adapter.multiChoose(meta)){
            refreshMultiChooseCount();
            return true;
        }
        return false;
    }

    public ObservableField<Boolean> isAllChoose() {
        return mAllChoose;
    }

    private boolean refreshMultiChooseCount() {
        NasFolder folderMeta=mCurrent.get();
        int length=null!=folderMeta?folderMeta.getLength():0;
        BrowserAdapter adapter=mBrowserAdapter;
        int count=null!=adapter?adapter.getChooseCount():0;
        mMultiCount.set(count<=0?"None selected(0/"+length+")":"Selected("+count+"/"+length+")");
        if (null!=adapter){
            List<NasFile> data=adapter.getData();
            int size=null!=data?data.size():0;
            mAllChoose.set(size==count&&size>0);
            return true;
        }
        return false;
    }

    public ObservableField<String> getMultiChooseCount() {
        return mMultiCount;
    }

    private void runChoose(OnChooseExist exit,boolean emptyToast){
        BrowserAdapter adapter=mBrowserAdapter;
        List<NasFile> list=null!=adapter?adapter.getChoose():null;
        if (null==list||list.size()<=0){
            if (emptyToast){
                toast("Choose nothing.");
            }
        }else if(null!=exit){
            exit.onChooseExist(list);
        }
    }

    public ObservableField<NasFolder> getCurrent() {
        return mCurrent;
    }

    public BrowserAdapter getBrowserAdapter() {
        return mBrowserAdapter;
    }
}
