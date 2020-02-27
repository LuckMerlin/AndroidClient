package com.merlin.model;

import android.content.Context;
import android.os.Environment;

import com.merlin.adapter.LocalBrowserAdapter;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.LocalFile;
import com.merlin.client.R;
import com.merlin.debug.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalBrowserModel extends BrowserModel {

    public LocalBrowserModel(Context context,ClientMeta meta,ClientCallback callback){
        super(context,meta,callback);
        setAdapter(new LocalBrowserAdapter()
        {
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

    private boolean browserFolder(String path,int from,int to,OnApiFinish<Reply<FolderData<LocalFile>>> finish){
        File folder=null!=path&&path.length()>0?new File(path): Environment.getRootDirectory();
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
        }else if(!folder.canRead()){
            note=getText(R.string.nonePermission);
            what=What.WHAT_NONE_PERMISSION;
        }
        if (null==what){
            succeed=true;
            File[] files=folder.listFiles();
            int length=null!=files?files.length:0;
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
                        list.add(new LocalFile(child));
                    }
                }
                folderData.setData(list);
            }
            folderData.setFrom(from);
            folderData.setTo(to);
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
        return false;
    }


}
