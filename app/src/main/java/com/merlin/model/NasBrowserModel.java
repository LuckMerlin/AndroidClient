package com.merlin.model;

import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.adapter.NasBrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.File;
import com.merlin.bean.FileModify;
import com.merlin.bean.FolderData;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasFolder;
import com.merlin.client.R;
import com.merlin.client.databinding.FileDetailBinding;
import com.merlin.client.databinding.NasFileContextMenuBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.media.MediaPlayService;
import com.merlin.retrofit.Retrofit;

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

public class NasBrowserModel extends BrowserModel<NasFile> implements Label {
    private interface Api {
        @POST(Address.PREFIX_FILE_BROWSER)
        @FormUrlEncoded
        Observable<Reply<NasFolder>> queryFiles(@Field(LABEL_PATH) String path, @Field(LABEL_FROM) int from,
                                                @Field(LABEL_TO) int to);

        @POST(Address.PREFIX_USER_REBOOT)
        Observable<Reply> rebootClient();

        @POST(Address.PREFIX_FILE+"/delete")
        @FormUrlEncoded
        Observable<Reply<ApiList<String>>> deleteFile(@Field(LABEL_PATH) List<String> paths);

        @POST(Address.PREFIX_FILE+"/scan")
        @FormUrlEncoded
        Observable<Reply<NasFile>> scan(@Field(LABEL_PATH) String path,@Field(LABEL_ENABLE) boolean recursive);

        @POST(Address.PREFIX_FILE+"/detail")
        @FormUrlEncoded
        Observable<Reply<NasFile>> getDetail(@Field(LABEL_PATH) String path);

        @POST(Address.PREFIX_FILE+"/create")
        @FormUrlEncoded
        Observable<Reply<FileModify>> createFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name, @Field(LABEL_FOLDER) boolean folder);

        @POST(Address.PREFIX_FILE+"/rename")
        @FormUrlEncoded
        Observable<Reply<FileModify>> renameFile(@Field(LABEL_PATH) String path, @Field(LABEL_NAME) String name);

    }

    public NasBrowserModel(ClientMeta meta,String url){
        super(meta);
        final Retrofit retrofit=new Retrofit();
        setAdapter(new NasBrowserAdapter(){
            @Override
            protected final boolean onPageLoad(String path, int from, OnApiFinish<Reply<FolderData<NasFile>>> finish) {
                if (null==url||url.length()<=0){
                    Debug.W(getClass(),"Can't load nas folder with NULL url."+url);
                    return false;
                }
                return null!=path&&null!=retrofit&&null!=retrofit.call(url,Api.class,null,null,null,null,(OnApiFinish<Reply<FolderData<NasFile>>>)(what, note, data, arg)->{
                    if (what== What.WHAT_SUCCEED){
                        getCurrentFolder().set(null!=data?data.getData():null);
                    }
                    if (null!=finish){
                        finish.onApiFinish(what,note,data,arg);
                    }
                }).queryFiles(path, from,from+50);
            }
        });

    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        if (super.onTapClick(view,clickCount,resId,data)){
            switch (clickCount){
                case 1:
                    return onSingleTapClick(view,resId,data)||true;
                default:
                    if (null!=data&&data instanceof NasFile){
                        NasFileContextMenuBinding binding= DataBindingUtil.inflate(LayoutInflater.from(view.getContext()), R.layout.nas_file_context_menu,null,false);
                        if (null!=binding){
                            binding.setFile((NasFile)data);
                            showAtLocationAsContext(view,binding);
                        }
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    protected boolean onSingleTapClick(View view, int resId, Object data) {
            switch (resId){
                case R.string.open:
                    return null!=data&&data instanceof File &&open((File)data,"After open tap click.");
                case R.string.reboot:
                    return rebootClient("After reboot tap click.");
                case R.string.scan:
                    return null!=data&&data instanceof NasFile&&scan((NasFile)data,false)||true;
                case R.string.detail:
                    return null!=data&&data instanceof NasFile &&showFileDetail((NasFile)data);
                case R.string.createFile:
                    return createFile(false);
                case R.string.createFolder:
                    return createFile(true);
                case R.string.delete:
                    List<NasFile> list=null!=data&&data instanceof NasFile ?new ArrayList<>():null;
                    return null!=list&&list.add((NasFile)data)&&deleteFile(list,"After delete tap click.");
                case R.string.rename:
                    return null!=data&&data instanceof NasFile &&renameFile((NasFile)data);
                case R.string.download:
                    return downloadFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After cancel tap click.");
                case R.string.copy:
                    return copyFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After copy tap click.");
                case R.string.move:
                    return moveFile(null!=data&&data instanceof NasFile ?(NasFile)data:null,"After move tap click.");
                case R.string.upload:
                    return null!=data&&data instanceof NasFile &&uploadFile((NasFile)data);
                default:
                    return super.onSingleTapClick(view,resId,data);

            }
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        if (!super.onLongClick(view,clickCount,resId,data)){
            switch (resId){
                case R.string.scan:
                    return null!=data&&data instanceof NasFile&&scan((NasFile)data,true)||true;
            }
        }
        return true;
    }

    private boolean moveFile(NasFile meta, String debug){
        if (null!=meta&&isMode(MODE_MOVE)||entryMode(MODE_MOVE,debug)){
            setProcessing(meta,"While start move file "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

    private boolean copyFile(NasFile meta, String debug){
        if (null!=meta&&isMode(MODE_COPY)||entryMode(MODE_COPY,debug)){
            setProcessing(meta,"While start copyx file "+(null!=debug?debug:"."));
            return true;
        }
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
                    call(Api.class,(OnApiFinish<Reply<FileModify>>)(what, note, data, arg)->{
                        boolean succeed=what==WHAT_SUCCEED;
                        toast(succeed?R.string.succeed : what==WHAT_FILE_EXIST?R.string.fileAlreadyExist:R.string.fail);
                        BrowserAdapter adapter=getBrowserAdapter();
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

    private final boolean createFile(boolean dir){
        FolderData folderMeta=getCurrentFolderData();
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
                            return null!=call(Api.class,(OnApiFinish<Reply<FileModify>>)(what, note, data2, arg)->{
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

    private boolean deleteFile(List<NasFile> files, String debug){
        final int length=null!=files?files.size():-1;
        if (length>0){
            Dialog dialog=new Dialog(getViewContext());
            NasFile first=files.get(0);
            String name=null!=first?first.getName():null;
            String message=""+(length==1?(null!=name?(""+getText(first.isDirectory()? R.string.folder:R.string.file)+" "+name):""):getText(R.string.items,length));
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
                        return null!=call(Api.class,(OnApiFinish<Reply<ApiList<String>>>)(what, note, data3, arg)->{
                            toast(note);
                            if (what==WHAT_SUCCEED){
                                List<String> deletedPaths=null!=data3?data3.getData():null;
                                BrowserAdapter adapter=getBrowserAdapter();
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

    private boolean scan(NasFile file, boolean recursive){
        String path=null!=file?file.getPath():null;
        return null!=path&&path.length()>0&&null!=call(Api.class,(OnApiFinish<Reply>)(what, note, data2, arg)->toast(note)).scan(path,recursive);
    }


    private boolean open(File file,String debug){
        String path=null!=file?file.getPath():null;
        if (null!=path&&path.length()>0){
            return  MediaPlayService.play(getContext(), file, 0, false);
        }
        toast(R.string.pathInvalid);
        return false;
    }
}
