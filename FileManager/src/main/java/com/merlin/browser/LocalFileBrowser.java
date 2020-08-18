package com.merlin.browser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Folder;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.lib.Canceler;
import com.merlin.server.Client;
import com.merlin.task.OnTaskUpdate;
import com.merlin.task.Task;
import com.merlin.task.file.Cover;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LocalFileBrowser extends FileBrowser implements OnTaskUpdate {
    public LocalFileBrowser(Client meta, Callback callback) {
        super(meta, callback);
    }

    @Override
    protected boolean onReboot(String debug) {
//        String[] arrayRestart = {"su","-c","reboot"};
        String[] arrayShutDown = {"su","-c","reboot -p"};
//        return execCommand(arrayShutDown);
//        Debug.D(getClass(),"EEEEEEEEEE "+getAdapterContext());
//        PowerManager pManager=(PowerManager) getAdapterContext().getSystemService(Context.POWER_SERVICE);
//        pManager.reboot("重启");
//        Intent reboot = new Intent(Intent.ACTION_REBOOT);
//        reboot.putExtra("nowait", 1);
//        reboot.putExtra("interval", 1);
//        reboot.putExtra("window", 0);
//        Context context=getAdapterContext();
//        context.sendBroadcast(reboot);
        return false;
    }

    @Override
    protected boolean onOpenPath(Path meta, String debug) {
        String path=null!=meta&&meta.isLocal()?meta.getPath():null;
        final Context context=getContext();
        if (null!=path&&path.length()>0){
            String mime=meta.getMime();
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                uri = FileProvider.getUriForFile(context, "com.merlin.browser.fileProvider", new File(path));
            } else {
                uri = Uri.fromFile(new File(path));
            }
            intent.setDataAndType(uri, null!=mime&&mime.length()>2?mime:"*/*");
            try {
                context.startActivity(intent);
                return true;
            }catch (Exception e){
                //Do nothing
            }
        }
        return false;
    }

    @Override
    protected boolean onShowPathDetail(Path meta, String debug) {

        return false;
    }

    @Override
    protected boolean onSetAsHome(String path, OnApiFinish<Reply<String>> finish, String debug) {

        return false;
    }

    @Override
    protected boolean onCreatePath(boolean dir, int coverMode, String folder, String name, OnApiFinish<Reply<Path>> finish, String debug) {
        Reply<Path>  reply=null;
        if (null == folder || folder.length() <= 0 || null == name || name.length() <= 0) {
            reply=new Reply<>(true,What.WHAT_ARGS_INVALID,"Path or name invalid",null);
        }else{
            File file=new File(folder,name);
            if (file.exists()&&coverMode==Cover.COVER_REPLACE){
                file.delete();
            }
            if (file.exists()){
                reply = new Reply<>(true, What.WHAT_ALREADY_DONE, "Path already exist", null);
            } else {
                try {
                    if ((dir? file.mkdirs():file.createNewFile())&&file.exists()){
                        reply = new Reply<>(true, What.WHAT_SUCCEED, "Create path succeed", null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    reply = new Reply<>(true, What.WHAT_FAIL, "Exception create path", null);
                }
            }
        }
        reply=null!=reply?reply:new Reply<>(true, What.WHAT_FAIL, "Fail create path", null);
        if (null!=finish){
            finish.onApiFinish(reply.getWhat(),reply.getNote(),reply,null);
        }
        return true;
    }

    @Override
    protected boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug) {
        if (null!=finish) {
            Reply<Path> reply=null;
            if (null == path || path.length() <= 0 || null == name || name.length() <= 0) {
                reply=new Reply<>(true,What.WHAT_ARGS_INVALID,"Path or name invalid",null);
            }else{
                File file=new File(path);
                if (!file.exists()){
                    reply=new Reply<>(true,What.WHAT_NOT_EXIST,"Path not exist",null);
                }else if (!file.canWrite()){
                    reply=new Reply<>(true,What.WHAT_NONE_PERMISSION,"Path none permission",null);
                }else{
                    File parent=file.getParentFile();
                    if (null==parent){
                        reply=new Reply<>(true,What.WHAT_ERROR_UNKNOWN,"Path none parent",null);
                    }else if (new File(parent,name).exists()&&coverMode!= Cover.COVER_REPLACE){
                        reply=new Reply<>(true,What.WHAT_ALREADY_DONE,"Path already exist",null);
                    }else {
                        file.renameTo(new File(parent,name));
                        if (file.exists()){
                            reply=new Reply<>(true,What.WHAT_ERROR_UNKNOWN,"Path rename fail",null);
                        }else{
                            reply=new Reply<>(true,What.WHAT_SUCCEED,"Path rename succeed",null);
                        }
                    }
                }
            }
            reply=null!=reply?reply:new Reply<>(true,What.WHAT_ERROR_UNKNOWN,"Path rename fail",null);
            finish.onApiFinish(reply.getWhat(),reply.getNote(),reply,null);
            return true;
        }
        return false;
    }

    @Override
    protected Canceler onPageLoad(String path, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
        path=null!=path&&path.length()>0?path:getHome();
        File file=null!=path&&path.length()>0?new File(path):null;
        Reply<PageData<Path>> reply=null;
        if (null==file){
            reply=new Reply<>(true,What.WHAT_ARGS_INVALID,"Args invalid",null);
        }else if (!file.exists()){
            reply=new Reply<>(true, What.WHAT_NOT_EXIST,"Directory not exist.",null);
        }else if (!file.isDirectory()){
            reply=new Reply<>(true,What.WHAT_NOT_DIRECTORY,"Not directory",null);
        }else if (from<0){
            reply=new Reply<>(true,What.WHAT_ARGS_INVALID,"From index invalid",null);
        }else if (!file.canRead()){
            reply=new Reply<>(true,What.WHAT_NONE_PERMISSION,"Folder none permission",null);
        }else{
            Path folderPath=Path.build(file);
            final File[] files=file.listFiles();
            final int length=null!=files?files.length:0;
            final ArrayList<Path> list=new ArrayList<>();
            Folder<Path> pageData=new Folder<>(folderPath,from,list,length);
            if (length<=0){
                reply=new Reply<>(true,What.WHAT_SUCCEED,"Directory empty",pageData);
            }else if (from>=length){
                reply=new Reply<>(true,What.WHAT_OUT_OF_BOUNDS,"Out of bounds",pageData);
            }else{
                Arrays.sort(files,(File o1, File o2)->null!=o1&&o1.isDirectory()?-1:null!=o2&&o2.isDirectory()?1:0);
                Path childPath=null;
                for (int i = from; i < Math.min(length, from+50); i++) {
                    if (null!=(childPath=Path.build(files[i]))){
                        list.add(childPath);
                    }else{
                        reply=new Reply<>(true,What.WHAT_FAIL,"One child path generate fail",pageData);
                        break;
                    }
                }
                reply=null!=reply?reply:new Reply<>(true,What.WHAT_SUCCEED,"Load succeed",pageData);
            }
        }
        reply=null!=reply?reply:new Reply<>(true,What.WHAT_FAIL,"Error unknown.",null);
        finish.onApiFinish(reply.getWhat(), reply.getNote(), reply, null);
        return (cancel,debug)->false;
    }

    @Override
    public void onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder, Remover remover) {

    }

    @Override
    public void onTaskUpdate(int status, int what, String note, Object obj, Task task) {
//            null!=task?task.getProgress()
    }

    private String getHome(){
        return "/sdcard";
    }

}
