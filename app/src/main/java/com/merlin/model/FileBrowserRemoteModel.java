package com.merlin.model;

import android.view.View;

import androidx.databinding.ObservableField;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.File_;
import com.merlin.bean.FModify;
import com.merlin.bean.FilePaste;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasFolder;
import com.merlin.client.R;
import com.merlin.client.databinding.FileDetailBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.browser.FileBrowser;
import com.merlin.media.MediaPlayService;
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

/**
 * @deprecated
 */
public final class FileBrowserRemoteModel extends Model implements Label, OnTapClick, OnLongClick {
    private final ObservableField<Boolean> mAllChoose=new ObservableField<>(false);
    private final ObservableField<Integer> mMode=new ObservableField<>();
    private final ObservableField<NasFolder> mCurrent=new ObservableField<>();
    private final ObservableField<String> mMultiCount=new ObservableField<>();
    private final static int MODE_NORMAL= FileBrowser.MODE_NORMAL;
    private final static int MODE_MULTI_CHOOSE= FileBrowser.MODE_MULTI_CHOOSE;
    private final static int MODE_COPY= FileBrowser.MODE_COPY;
    private final static int MODE_MOVE= FileBrowser.MODE_MOVE;

    private Object mProcessing;
    private interface Api {
        @POST(Address.PREFIX_FILE+"/directory/browser")
        @FormUrlEncoded
        Observable<Reply<NasFolder>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                @Field(LABEL_TO) int to);

        @POST(Address.PREFIX_FILE+"/delete")
        @FormUrlEncoded
        Observable<Reply<ApiList<String>>> deleteFile(@Field(LABEL_PATH) List<String> paths);

        @POST(Address.PREFIX_FILE+"/rename")
        @FormUrlEncoded
        Observable<Reply<FModify>> renameFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name);

        @POST(Address.PREFIX_USER_REBOOT)
        Observable<Reply> rebootClient();

        @POST(Address.PREFIX_FILE+"/create")
        @FormUrlEncoded
        Observable<Reply<FModify>> createFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name, @Field(LABEL_FOLDER) boolean folder);

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
    BrowserAdapter mNasBrowserAdapter;

//    private final BrowserAdapter mNasBrowserAdapter=new BrowserAdapter(){
//        @Override
//        protected boolean onPageLoad(String path, int from, OnApiFinish<Reply<NasFolder>> finish) {
//            return null!=path&&null!=call(Api.class,(OnApiFinish<Reply<NasFolder>>)(what, note, data, arg)->{
//                if (what==WHAT_SUCCEED){
//                    mCurrent.set(null!=data?data.getData():null);
//                }
//                if (null!=finish){
//                    finish.onApiFinish(what,note,data,arg);
//                }
//            }).queryFiles(path, from,from+50);
//        }
//    };

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
//            case R.string.open:
//                return null!=data&&data instanceof File_ &&open((File_)data,"After open tap click.");
//            case R.string.reboot:
//                return rebootClient("After reboot tap click.");
//            case R.string.scan:
//                return null!=data&&data instanceof NasFile&&scan((NasFile)data,false)||true;
//            case R.string.detail:
//                return null!=data&&data instanceof NasFile &&showFileDetail((NasFile)data);
//            case R.string.createFile:
//                return createFile(false);
//            case R.string.createFolder:
//                return createFile(true);
//            case R.string.delete:
//                List<NasFile> list=null!=data&&data instanceof NasFile ?new ArrayList<>():null;
//                return null!=list&&list.add((NasFile)data)&&deleteFile(list,"After delete tap click.");
//            case R.string.rename:
//                return null!=data&&data instanceof NasFile &&renameFile((NasFile)data);
//            case R.id.fileBrowser_bottom_cancel_TV:
//                return cancel("After cancel tap click.");
//            case R.string.download:
//                return downloadFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After cancel tap click.");
//            case R.id.fileBrowser_bottom_paste_TV:
//                return pasteFileOnCurrent("After paste tap click.")&&entryMode(MODE_NORMAL);
//            case R.string.copy:
//                return copyFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After copy tap click.");
//            case R.string.move:
//                return moveFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After move tap click.");
//            case R.string.upload:
//                return null!=data&&data instanceof NasFile &&uploadFile((NasFile)data);

            default:
                if (null!=data){
                    if (data instanceof NasFile){
//                        NasFile file=(NasFile)data;
//                        if (isMode(MODE_MULTI_CHOOSE)) {
//                            multiChoose(file);
//                        } else if(file.isAccessible()){
//                            if (file.isDirectory()) {
//                                browserPath(file.getPath(), "After directory click.");
//                            } else{//Open file
//                            }
//                        }else{
//                            toast(R.string.nonePermission);
//                        }
                    }else if (data instanceof File_){
                        open((File_)data,"After file item tap.");
                    }
                }
                break;
        }
        return false;
    }

    private boolean open(File_ file, String debug){
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
        return false;
    }

    private boolean scan(NasFile file, boolean recursive){
        String path=null!=file?file.getPath(false):null;
        return null!=path&&path.length()>0&&null!=call(prepare(Api.class,Address.HOST).scan(path,recursive),(OnApiFinish<Reply>)(what, note, data2, arg)->toast(note));
    }

    private boolean pasteFiles(int mode, List<FilePaste> processes, String debug){
//        if(null!=processes&&processes.size()>0){
//            String modeValue=mode== MODE_COPY?Label.LABEL_COPY:mode==MODE_MOVE?LABEL_MODE:null;
//            if (null==modeValue){
//                return false;
//            }
//            return null!=call(Api.class,(OnApiFinish<Reply<ApiList<Reply<FilePaste>>>>)(what, note, data, arg)->{
//                toast(note);
//                ApiList<Reply<FilePaste>> list=what!=WHAT_SUCCEED&&null!=data?data.getData():null;
//                if (null!=list&&list.size()>0){
//                    for (Reply<FilePaste> child:list) {
//                        if (null!=child&&child.isSuccess()){
//                            FilePaste paste=child.getData();
////                              if (null!=paste){
////                                  Debug.D(getClass(),"QQQQQ "+paste.getMode()+" "+paste.getFrom()+" "+paste.getTo()+" ");
////                              }
//                        }
//                    }
//                }
//            }).pasteFile(modeValue,processes);
//        }
        return false;
    }

    private boolean showFileDetail(NasFile meta){
        String path=null!=meta?meta.getPath(false):null;
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
        return null!=call(prepare(Api.class,Address.HOST).getDetail(path),(OnApiFinish<Reply<NasFile>>)(what, note, data2, arg)->{
            NasFile detail=what==WHAT_SUCCEED&&null!=data2?data2.getData():null;
            if (null!=detail){
                binding.setFile(detail);
            }
            binding.setLoadState(what);
        });
    }

    private boolean downloadFile(NasFile meta, String debug){
        String path=null!=meta?meta.getPath(false):null;
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
        String path=null!=meta?meta.getPath(false):null;
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
//        final int mode=mMode.get();
//        if (mode== MODE_COPY||mode==MODE_MOVE){
//            NasFolder folder=mCurrent.get();
//            String folderPath=null!=folder?folder.getPath():null;
//            if (null!=folderPath&&folderPath.length()>0){
//                Object object=mProcessing;
//                NasFile file=null!=object&&object instanceof NasFile ?(NasFile)object:null;
//                String path=null!=file?file.getPath():null;
//                if (null!=path&&path.length()>0){
//                    List<FilePaste> list=new ArrayList<>();
//                    list.add(new FilePaste(path,folderPath,What.WHAT_NORMAL));
//                    return pasteFiles(mode,list,debug);
//                }
//                toast(R.string.pathInvalid);
//                return false;
//            }
//            toast(R.string.pathInvalid);
//            return false;
//        }
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

    private boolean entryMode(int mode){
//        if (!isMode(mode)){
//            mProcessing=null;
//            mMode.set(mode);
//            BrowserAdapter adapter=mNasBrowserAdapter;
//            if (null!=adapter){
//                adapter.setMode(mode);
//            }
//            switch (mode){
//                case MODE_MULTI_CHOOSE:
//                    return refreshMultiChooseCount();
//                case MODE_COPY:
//                    break;
//                case MODE_MOVE:
//                    break;
//            }
//            return true;
//        }
        return false;
    }


    public final boolean chooseAll(boolean choose){
//        BrowserAdapter adapter=mNasBrowserAdapter;
//        if (isMode(MODE_MULTI_CHOOSE)&&null!=adapter&&adapter.chooseAll(choose)){
//            refreshMultiChooseCount();
//            return true;
//        }
        return false;
    }

    private boolean multiChoose(NasFile meta){
//        BrowserAdapter adapter=mNasBrowserAdapter;
//        if (null!=meta&&isMode(MODE_MULTI_CHOOSE)&&adapter.multiChoose(meta)){
//            refreshMultiChooseCount();
//            return true;
//        }
        return false;
    }

    public ObservableField<Boolean> isAllChoose() {
        return mAllChoose;
    }

    private boolean refreshMultiChooseCount() {
//        NasFolder folderMeta=mCurrent.get();
//        int length=null!=folderMeta?folderMeta.getLength():0;
//        BrowserAdapter adapter=mNasBrowserAdapter;
//        int count=null!=adapter?adapter.getChooseCount():0;
//        mMultiCount.set(count<=0?"None selected(0/"+length+")":"Selected("+count+"/"+length+")");
//        if (null!=adapter){
//            List<NasFile> data=adapter.getData();
//            int size=null!=data?data.size():0;
//            mAllChoose.set(size==count&&size>0);
//            return true;
//        }
        return false;
    }

    public ObservableField<String> getMultiChooseCount() {
        return mMultiCount;
    }


    public ObservableField<Integer> getMode() {
        return mMode;
    }

    private boolean isMode(int mode){
        ObservableField<Integer> current=mMode;
        Integer curr=null!=current?current.get():null;
        return null!=curr&&mode==curr;
    }

    private boolean resetBrowserCurrentFolder(String debug){
        BrowserAdapter adapter=mNasBrowserAdapter;
        return null!=adapter&&adapter.reset(debug);
    }

    private boolean browserPath(String pathValue, String debug){
        BrowserAdapter adapter=mNasBrowserAdapter;
        return null!=adapter&&adapter.loadPage(pathValue,debug);
    }

    private final boolean refreshCurrentPath(String debug){
        NasFolder meta=mCurrent.get();
        return browserPath(null!=meta?meta.getPath():null,debug);
    }

    public ObservableField<NasFolder> getCurrent() {
        return mCurrent;
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

    private boolean rebootClient(String debug){
        Dialog dialog=new Dialog(getViewContext());
        dialog.create().title(R.string.reboot).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId,data)-> {
            if (resId==R.string.sure){
                Debug.D(getClass(),"Reboot client meta "+(null!=debug?debug:"."));
                call(prepare(Api.class,Address.HOST).rebootClient(),(OnApiFinish<Reply>)(what, note, data2, arg)-> toast(note));
            }
            dialog.dismiss();
            return true;
        });
        return true;
    }

    private boolean deleteFile(List<NasFile> files, String debug){
        final int length=null!=files?files.size():-1;
        if (length>0){
            Dialog dialog=new Dialog(getViewContext());
            NasFile first=files.get(0);
            String name=null!=first?first.getName(false):null;
            String message=""+(length==1?(null!=name?(""+getText(first.isDirectory()? R.string.folder:R.string.file)+" "+name):""):getText(R.string.items,length));
            return dialog.create().title(R.string.delete).message(getText(R.string.deleteSure,message)).left(R.string.sure).right(R.string.cancel).show((view, clickCount,  resId, data)->{
                dialog.dismiss();
                if (resId ==R.string.sure){
                    List<String> paths=new ArrayList<>();
                    Map<String, NasFile> map=new HashMap<>(length);
                    for (NasFile meta:files) {
                        String path=null!=meta?meta.getPath(false):null;
                        if (null!=path&&path.length()>0){
                            paths.add(path);
                            map.put(path,meta);
                        }
                    }
                    if (null!=paths&&paths.size()>0){
                        return null!=call(prepare(Api.class,Address.HOST).deleteFile(paths),(OnApiFinish<Reply<ApiList<String>>>)(what, note, data3, arg)->{
                            toast(note);
                            if (what==WHAT_SUCCEED){
                                List<String> deletedPaths=null!=data3?data3.getData():null;
                                BrowserAdapter adapter=mNasBrowserAdapter;
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
                        });
                    }
                }
                return true;
            },false);
        }
        Debug.D(getClass(),"Can't delete file.");
        return false;
    }

    public final boolean createFile(boolean dir){
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
                            return null!=call(prepare(Api.class,Address.HOST).createFile(parent,input,dir),(OnApiFinish<Reply<FModify>>)(what, note, data2, arg)->{
                                if(what==WHAT_SUCCEED){
                                    resetBrowserCurrentFolder("After file create succeed.");
                                }
                                toast(note);
                            });
                        }
                    }
                    dialog.dismiss();
                    return true;
                });
    }

    private boolean renameFile(NasFile meta){
        final String path=null!=meta?meta.getPath(false):null;
        if (null!=path&&path.length()>0){
            final String name=meta.getName(false);
            return new SingleInputDialog(getViewContext()).show(R.string.rename,(dlg, text)->{
                if (null==text||text.length()<=0){
                    toast(R.string.inputNotNull);
                }else if (null!=name&&text.equals(name)){
                    toast(R.string.noneChanged);
                }else{
                    if (null!=dlg){
                        dlg.dismiss();
                    }
                    call(prepare(Api.class,Address.HOST).renameFile(path,text),(OnApiFinish<Reply<FModify>>)(what, note, data, arg)->{
                        boolean succeed=what==WHAT_SUCCEED;
                        toast(succeed?R.string.succeed : what==WHAT_FILE_EXIST?R.string.fileAlreadyExist:R.string.fail);
                        BrowserAdapter adapter=mNasBrowserAdapter;
                        FModify modify=succeed&&null!=data&&null!=adapter?data.getData():null;
                        if (succeed&&null!=modify&&null!=adapter){
//                            adapter.renamePath(meta,modify);
                        }
                    });
                }
            });
        }
        Debug.W(getClass(),"Can't rename file.path="+path);
        return false;
    }


    public BrowserAdapter getNasBrowserAdapter() {
        return mNasBrowserAdapter;
    }
}
