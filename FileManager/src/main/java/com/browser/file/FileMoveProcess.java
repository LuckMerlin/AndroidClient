package com.browser.file;

import com.merlin.api.OnProcessChange;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.file.transport.FileUploadNasTask;
import com.merlin.file.transport.NasFileDownloadTask;
import com.merlin.retrofit.Retrofit;
import com.merlin.task.Result;
import com.merlin.task.file.HttpDownloadTask;
import com.merlin.task.file.HttpUploadTask;

import java.io.File;
import java.util.ArrayList;

public class FileMoveProcess extends FileProcess<Path> {
    private final Path mFolder;
    private final int mCoverMode;

    public FileMoveProcess(String title, ArrayList<Path> files, Path folder, int coverMode){
        super(title,files);
        mFolder=folder;
        mCoverMode=coverMode;
    }

    @Override
    protected Reply<Path> onProcess(Path pathObj, OnProcessChange<Path> processProgress, Retrofit retrofit) {
        final Path toFolder=mFolder;
        final String toFolderPath=null!=toFolder?toFolder.getPath():null;
        final String name=null!=pathObj?pathObj.getName(true):null;
        Reply<Path> reply=null;
        final String filePath=null!=pathObj?pathObj.getPath():null;
        if (null==filePath||filePath.length()<=0||null==toFolderPath||toFolderPath.length()<=0){
            reply= new Reply(true, What.WHAT_ARGS_INVALID,"Path invalid.",pathObj);
        }else if (null==name||name.length()<=0){
            reply= new Reply(true, What.WHAT_ARGS_INVALID,"Path name invalid.",pathObj);
        }else if (!toFolder.isDirectory()){
            reply= new Reply(true, What.WHAT_ARGS_INVALID,"Target path not folder",pathObj);
        }else if (pathObj.isLocal()){//Move local file
            if (toFolder.isLocal()){
               reply = move(new File(filePath),new File(toFolderPath, name),mCoverMode);
            }else{

            }
        }else{//Copy cloud file
            if (toFolder.isLocal()){//Download cloud to local
                reply= downloadFile(pathObj,new File(toFolderPath,name),mCoverMode);
            }else{//Upload local to cloud
                reply= new Reply(true, What.WHAT_NOT_SUPPORT,"Path not support from cloud to cloud",pathObj);
            }
        }
        return reply;
    }

    private Reply<Path> downloadFile(Path cloudFile,File target,int coveMode){
        if (null==cloudFile||null==target){
            return new Reply(true, What.WHAT_ARGS_INVALID,"File invalid",null);
        }
        HttpDownloadTask task=new NasFileDownloadTask(cloudFile,target.getAbsolutePath());
        task.enableBreakPoint(true,"While download cloud file");
        task.execute(null,null);
        Result result=task.getResult();
        return null;
    }


    private Reply<Path> uploadFile(File file,Path toFolder,String name,int coverMode){
        if (null==file||null==toFolder||null==name||name.length()<=0){
            return new Reply(true, What.WHAT_ARGS_INVALID,"File invalid",null);
        }
//        String name,String from, String to,String method,String toFolder,String toName
//        new HttpUploadTask(file.getName(),file.getAbsolutePath(),tof);
//        FileUploadNasTask task=new FileUploadNasTask(file.getName(),file.getAbsolutePath(),toFolder,name);
//        task.enableBreakPoint(true,"While upload to cloud");
//        task.execute(new Htt);
        return null;
    }

    private Reply<Path> move(File file,File to,int coverMode){
        if (null==file||null==to){
            return new Reply(true, What.WHAT_ARGS_INVALID,"File invalid",null);
        }else if (!file.exists()){
            return new Reply(true, What.WHAT_NOT_EXIST,"File not exist",null);
        }else if (to.exists()){
            return new Reply(true, What.WHAT_INTERRUPT,"File already exist",Path.build(to));
        }
        file.renameTo(to);
        return (!file.exists()&&to.exists())?new Reply(true, What.WHAT_SUCCEED,"Move succeed",Path.build(to)):
                new Reply(true, What.WHAT_FAIL,"Move fail",Path.build(to));
    }
}
