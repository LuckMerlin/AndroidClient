package com.merlin.browser;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.lib.Canceler;
import com.merlin.server.Client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LocalFileBrowser extends FileBrowser {
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
        return false;
    }

    @Override
    protected boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug) {
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
        }else{
            final File[] files=file.listFiles();
            final int length=null!=files?files.length:0;
            final PageData<Path> pageData=new PageData<>();
            pageData.setLength(length);
            if (length<=0){
                reply=new Reply<>(true,What.WHAT_SUCCEED,"Directory empty",pageData);
            }else if (from>=length){
                reply=new Reply<>(true,What.WHAT_OUT_OF_BOUNDS,"Out of bounds",pageData);
            }else{
                Arrays.sort(files,(File o1, File o2)->null!=o1&&o1.isDirectory()?0:-1);
                ArrayList<Path> list=new ArrayList<>();
                pageData.setData(list);
                Path childPath=null;
                for (int i = from; i < Math.min(length, from+50); i++) {
                    if (null!=(childPath=Path.build(files[i]))){
                        list.add(childPath);
                    }else{
                        reply=new Reply<>(true,What.WHAT_ERROR_UNKNOWN,"One child path generate fail",pageData);
                        break;
                    }
                }
                reply=null!=reply?reply:new Reply<>(true,What.WHAT_SUCCEED,"Load succeed",pageData);
            }
        }
        reply=null!=reply?reply:new Reply<>(true,What.WHAT_ERROR_UNKNOWN,"Error unknown.",null);
        finish.onApiFinish(reply.getWhat(), reply.getNote(), reply, null);
        return (cancel,debug)->false;
    }

    @Override
    public void onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder, Remover remover) {
    }

    @Override
    protected FileProcess onCreateFileProcess(int mode, ArrayList<Path> files, String target, Integer coverMode, String debug) {
        return null;
    }

    private String getHome(){
        return "/sdcard";
    }

    private boolean execCommand(String[] shutdown){
        try {
            Process	 process = null!=shutdown&&shutdown.length>0?Runtime.getRuntime().exec(shutdown):null;
            return null!=process;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}