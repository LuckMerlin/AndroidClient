package com.merlin.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.merlin.activity.PhotoPreviewActivity;
import com.merlin.activity.TransportActivity;
import com.merlin.adapter.LocalBrowserAdapter;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FMode;
import com.merlin.bean.FModify;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.LocalFile;
import com.merlin.client.R;
import com.merlin.client.databinding.ItemClientBinding;
import com.merlin.client.databinding.ServerChooseLayoutBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.transport.TransportService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalBrowserModel extends BrowserModel {
    private final Comparator<File> mComparator=( o1, o2)->{
       return null!=o1&&o1.isFile()?0:1;
    };

    public LocalBrowserModel(Context context,ClientMeta meta,ClientCallback callback){
        super(context,meta,callback);
        setAdapter(new LocalBrowserAdapter() {
            @Override
            protected boolean onPageLoad(String path, int from, OnApiFinish<Reply<FolderData<LocalFile>>> finish) {
                return null!=path&&browserFolder(path,from,from+50,(what, note, data, arg)->{
                    if (null!=finish){
                        finish.onApiFinish(what,note,data,arg);
                    }
                    if (what== What.WHAT_SUCCEED){
                        if (null!=callback){
                            callback.onPageDataLoad(LocalBrowserModel.this,null!=data?data.getData():null);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.string.upload:
                return (null!=data&&data instanceof LocalFile&&entryMode(MODE_UPLOAD,"After upload tap click.")&&setProcessing(data,"After upload tap click."))||true;
        }
        return super.onTapClick(view,clickCount,resId,data);
    }

    @Override
    protected boolean onOpenFile(FileMeta meta, String debug) {
        LocalFile localFile=null!=meta&&meta instanceof LocalFile?((LocalFile)meta):null;
        String extension=localFile.getExtension();
        String path=localFile.getPath();
        if (null!=extension&&null!=path&&path.length()>0){
            final File file=new File(path);
            if (!file.exists()){
                return toast(R.string.fileNotExist)&&false;
            }
            if (extension.equalsIgnoreCase(".png")|| extension.equalsIgnoreCase(".jpg")){
                Intent intent=new Intent(getViewContext(), PhotoPreviewActivity.class);
                intent.putExtra(Label.LABEL_DATA, Uri.fromFile(file));
                return startActivity(intent);
            }
        }
        return true;
    }

    @Override
    protected boolean onShowFileDetail(FileMeta meta, String debug) {

        return false;
    }

    @Override
    protected boolean onRenameFile(String path, String name, int mode, OnApiFinish finish, String debug) {
        Integer what=null;String note=null;boolean succeed=false;FModify data=null;Object arg=null;
        if (null==path||null==name||path.length()<=0||name.length()<=0){
            what=What.WHAT_ARGS_INVALID;
            note=getText(R.string.inputNotNull);
        }else{
           succeed=true;
           File file=new File(path);
           String currName=file.getName();
           int postfixIndex=null!=currName&&file.isFile()&&(mode&FModify.MODE_POSTFIX)==0?currName.lastIndexOf("."):-1;
           String postfix=postfixIndex>0?currName.substring(postfixIndex):null;
           name=null!=postfix&&postfix.length()>0?name+postfix:name;
           File folder=null!=name&&name.length()>0?file.getParentFile():null;
           File target=null!=folder?new File(folder,name):null;
           if (null==target){
               what=What.WHAT_ARGS_INVALID;
               note=getText(R.string.inputNotNull);
           }else if (!file.exists()){
               what=What.WHAT_FILE_NOT_EXIST;
               note=getText(R.string.fileNotExist);
           }else if (!file.canWrite()){
               what=What.WHAT_NONE_PERMISSION;
               note=getText(R.string.nonePermission);
           }else if (target.exists()&&(mode&FModify.MODE_COVER)==0){
               what=What.WHAT_FILE_EXIST;
               note=getText(R.string.fileAlreadyExist);
           }else if (target.exists()&&(target.delete()||target.exists())){
               what=What.WHAT_ERROR_UNKNOWN;
               note=getText(R.string.deleteFail);
           }else if(file.renameTo(target)&&target.exists()) {
               String[] fix=LocalFile.getPostfix(target);
               data=new FModify(folder.getAbsolutePath(),fix[0],fix[1],mode);
               what=What.WHAT_SUCCEED;
               note=getText(R.string.succeed);
           }
        }
        return invokeFinish(succeed,what,note,finish,data,arg);
    }

    @Override
    protected boolean onDeleteFile(List files, OnApiFinish finish, String debug) {
        Integer what=null;String note=null;boolean succeed=false;ApiList<String> data=null;Object arg=null;
        if (null==files||files.size()<=0){
            what=What.WHAT_ARGS_INVALID;
            note=getText(R.string.inputNotNull);
        }else{
            data=new ApiList<>();
            succeed=true;
            what=What.WHAT_SUCCEED;
            note=getText(R.string.deleteSucceed);
            for (Object obj:files) {
                String path=null!=obj&&obj instanceof String?(String)obj:null;
                File file=null!=path&&path.length()>0?new File(path):null;
                if (null!=file&&file.exists()&&(file.delete()||!file.exists())){
                    data.add(path);
                }
            }
        }
        return invokeFinish(succeed,what,note,finish,data,arg);

    }

    @Override
    protected boolean onCreateFile(boolean dir, int mode, String folder, String name, OnApiFinish finish, String debug) {
       Integer what=null;String note=null;boolean succeed=false;
        FModify modify=null;Object arg=null;
        if (null==folder||name==null||folder.length()<=0||name.length()<=0){
            what=What.WHAT_ARGS_INVALID;
            note=getText(R.string.inputNotNull);
        }else{
            final File file=new File(folder,name);
            final File parent=file.getParentFile();
            if (file.exists()&&(mode& FModify.MODE_COVER)==0){
                what=What.WHAT_FILE_EXIST;
                note=getText(R.string.fileAlreadyExist);
            }else if(null==parent||(!parent.exists()&&(!parent.mkdirs())||!parent.exists())){
                what=What.WHAT_ERROR_UNKNOWN;
                note=getText(R.string.createFail);
            }else if (!parent.canWrite()&&!parent.canExecute()){
                what=What.WHAT_NONE_PERMISSION;
                note=getText(R.string.nonePermission);
            }else if (file.exists()&&(!file.delete()||file.exists())){
                what=What.WHAT_ERROR_UNKNOWN;
                note=getText(R.string.deleteFail);
            }else{
                try {
                    succeed=dir?file.mkdir():file.createNewFile();
                    if (file.exists()) {
                        note=getText(R.string.createSucceed);
                        what=What.WHAT_SUCCEED;
                        String[] fix=LocalFile.getPostfix(file);
                        modify = new FModify(folder, fix[0],fix[1],mode);
                        arg = LocalFile.create(file,null);
                    }
                } catch (IOException e) {
                    what=What.WHAT_ERROR_UNKNOWN;
                    note=getText(R.string.createFail);
                }
            }
        }
        return invokeFinish(succeed,what,note,finish,modify,arg);
    }

    private boolean invokeFinish(boolean succeed,Integer what,String note,OnApiFinish finish,Object data,Object arg){
        if (null!=finish){
            what=null!=what?what:What.WHAT_ERROR_UNKNOWN;
            Reply reply=null;
            if (null!=data){
                reply=new Reply<>();
                reply.setSuccess(succeed);
                reply.setWhat(what);
                reply.setNote(note);
                reply.setData(data);
            }
            finish.onApiFinish(what,note,reply,arg);
            return true;
        }
        return false;
    }

    private File getDefaultRoot(){
        File file=new File("/sdcard");
        return file.exists()?file:Environment.getRootDirectory();
    }

    private boolean browserFolder(String path,int from,int to,OnApiFinish<Reply<FolderData<LocalFile>>> finish){
        File folder=null!=path&&path.length()>0?new File(path):getDefaultRoot();
        final Reply<FolderData<LocalFile>>  reply=new Reply<>();
        Integer what=null;
        boolean succeed=false;
        String note=null;
        Object arg=null;
        if (null==folder||folder.length()<=0){
            note=getText(R.string.pathInvalid);
            what=What.WHAT_ARGS_INVALID;
        }else if(from<0||to<=0){
            note=getText(R.string.inputNotNull);
            what=What.WHAT_ARGS_INVALID;
        }else if(!folder.exists()){
            note=getText(R.string.fileNotExist);
            what=What.WHAT_NOT_EXIST;
        }else if(!folder.exists()){
            note=getText(R.string.fileNotExist);
            what=What.WHAT_NOT_EXIST;
        }else if(!folder.isDirectory()){
            note=getText(R.string.pathInvalid);
            what=What.WHAT_NOT_DIRECTORY;
        } else if(!folder.canRead()){
            note=getText(R.string.nonePermission);
            what=What.WHAT_NONE_PERMISSION;
        }
        if (null==what){
            succeed=true;
            final File[] files=folder.listFiles();
            final int length=null!=files?files.length:0;
//            if (length>0){
//                Arrays.sort(files,mComparator);
//            }
            FolderData<LocalFile> folderData=new FolderData<>();
            folderData.setParent(folder.getParent());
            folderData.setPath(folder.getAbsolutePath());
            if (from>=length){
                what=What.WHAT_OUT_OF_BOUNDS;
                note=getText(R.string.outOfBounds);
            }
            if (what==null){
                what=What.WHAT_SUCCEED;
                succeed=true;
                to = Math.min(to,length);
                List<LocalFile> list=new ArrayList();
                for (int i = from; i < to; i++) {
                    File child=files[i];
                    if (null!=child){
                        list.add(LocalFile.create(child,null));
                    }
                }
                folderData.setData(list);
            }
            folderData.setFrom(from);
            folderData.setLength(length);
            reply.setData(folderData);
        }
        if (!succeed){
            Debug.D(getClass(),"Fail browser local folder."+note+" "+folder);
        }
        reply.setSuccess(succeed);
        reply.setNote(note);
        reply.setWhat(null!=what?what:What.WHAT_INVALID);
        if (null!=finish){
            finish.onApiFinish(reply.getWhat(),note,reply,arg);
        }
        return true;
    }

    private boolean upload(LocalFile file,String folder,String name,int mode, String debug){
        final Context context=getViewContext();
        if (null!=file&&null!=context){
            final String path=file.getPath();
            Dialog dialog=new Dialog(context);
            ServerChooseLayoutBinding binding=(ServerChooseLayoutBinding) inflate(R.layout.server_choose_layout);
            Collection<Object> values=getAllClients();
            if (null!=values&&values.size()>0){
                final Map<String, ViewDataBinding> added=new HashMap();
                for (Object obj:values) {
                    if (null!=(obj=null!=obj&&obj instanceof BrowserModel?((BrowserModel)obj).getClientMeta():obj)&&obj instanceof ClientMeta){
                        ClientMeta client=(ClientMeta)obj;
                        if (client.isLocalClient()){
                            continue;
                        }
                        String url=null!=client?client.getUrl():null;
                        if (null==url||url.length()<=0){
                            Debug.W(getClass(),"Skip add client into choose list.url="+url+" "+client);
                            continue;
                        }
                        ItemClientBinding clientBinding= !added.containsKey(url)?(ItemClientBinding) inflate(R.layout.item_client):null;
                        if(null!=clientBinding){
                            clientBinding.setClient(client);
                            added.put(url,clientBinding);
                        }
                    }
                }
                if (null==added||added.size()<=0){
                    toast(R.string.noneServerExist);
                }else{
                    binding.setChilds(added.values());
                }
            }
            return dialog.setContentView(binding).title(R.string.chooseServer).left(R.string.cancel).show(( view, clickCount, resId, data)-> {
                if (resId!=R.string.cancel&&null!=data&&data instanceof ClientMeta){
                    ClientMeta clientMeta=(ClientMeta)data;
                    final String url=null!=clientMeta?clientMeta.getUrl():null;
                    if (null==url||url.length()<=0){
                        toast(R.string.invalidServer);
                        return true;
                    }
                    context.startActivity(new Intent(context, TransportActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    TransportService.upload(context,true,path,clientMeta,folder,name,mode,debug);
                }
                dialog.dismiss();
                return true;},false)||true;
        }
        Debug.W(getClass(),"Can't  upload file with EMPTY list "+(null!=debug?debug:"."));
        return false;
    }

}
