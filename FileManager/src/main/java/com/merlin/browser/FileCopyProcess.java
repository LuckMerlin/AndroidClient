package com.merlin.browser;

import com.merlin.api.Label;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.retrofit.Retrofit;
import com.merlin.task.file.Cover;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class FileCopyProcess extends FileProcess<Path> {
    private final Path mFolder;
    private final int mCoverMode;

    public FileCopyProcess(String title,Path folder,int coverMode, ArrayList<Path> files){
        super(title,files);
        mFolder=folder;
        mCoverMode=coverMode;
    }

    private interface Api{
        @POST("/file/delete")
        @FormUrlEncoded
        Call<Reply<Processing>> copy(@Field(Label.LABEL_PATH) String path);
    }

    @Override
    protected Reply onProcess(Path from,OnProcessUpdate update, Retrofit retrofit) {
        return copyFile(from,mFolder,mCoverMode,update,retrofit);
    }

    private Reply copyFile(Path from,Path to,int coverMode,OnProcessUpdate update, Retrofit retrofit){
        final String fromPath=null!=from?from.getPath():null;
        final String toFolder=null!=to?to.getPath():null;
        if (null==fromPath|fromPath.length()<=0||null==toFolder||toFolder.length()<=0){
            return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Path invalid.",fromPath);
        }
        if (from.isLocal()){//Copy local file
            File fromFile=new File(fromPath);
            return to.isLocal()?copyLocalFileToLocal(fromFile,new File(toFolder),coverMode,update):
                    copyLocalFileToNas(fromFile,to.getHostUri(),toFolder,coverMode,update,retrofit);
        }
        return to.isLocal()?downloadToLocal(from.getHostUri(),fromPath,new File(toFolder),coverMode,update,retrofit):
                copyFromNasToNas(from,to,coverMode,update,retrofit);
    }

    private Reply copyFromNasToNas(Path from,Path toFolder,int coverMode,OnProcessUpdate update, Retrofit retrofit){
        return null;
    }

    private Reply copyLocalFileToNas(File from,String toHostUri,String toFolder,int coverMode,OnProcessUpdate update,Retrofit retrofit){

        return null;
    }

    private Reply<Path> copyLocalFileToLocal(File from,File toFolder,int coverMode,OnProcessUpdate update){
        final String name=null!=from?from.getName():null;
        if (null==name||name.length()<=0||null==toFolder){
            return new Reply(true,What.WHAT_INVALID,"File invalid",null);
        }else if (!from.exists()){
            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",null);
        }else if (!from.canRead()){
            return new Reply(true,What.WHAT_NONE_PERMISSION,"File none read permission",null);
        }
        final File toFile=new File(toFolder,name);
        if (toFile.exists()&&(from.isDirectory()==toFile.isDirectory())){
            if (coverMode!= Cover.COVER_REPLACE) {
                return new Reply(true, What.WHAT_EXIST, "File already exist", null);
            }
            File temp=null;
            while ((temp=new File(toFolder,"."+name+"_"+(Math.random()*10000)+".temp")).exists()){
                    //Do nothing
            }
            toFile.renameTo(temp);//Move exist to temp
            if (toFile.exists()){
                return new Reply(true, What.WHAT_FAIL, "Fail delete already exist", null);
            }
            Reply<Path> copyResult=copyLocalFileToLocal(from,toFolder,coverMode,update);
            if (null!=copyResult&&copyResult.isSuccess()&&copyResult.getWhat()==What.WHAT_SUCCEED){
                notifyProgress("Deleting exist file ", Path.build(temp),0f,update);
                deleteFile(temp,update);
            }else{//Copy fail,Rollback just copied file(s)
                notifyProgress("Rollback delete just copied file ", Path.build(temp),0f,update);
                deleteFile(toFile,update);
                if (!toFile.exists()&&temp.exists()){//Rollback delete succeed
                    temp.renameTo(toFile);//Move backup back
                }
            }
            return copyResult;
        }
        if (from.isDirectory()){//Copy file

        }
        return null;
    }

    private Reply downloadToLocal(String fromHostUri,String fromPath,File toFolder,int coverMode,OnProcessUpdate update,Retrofit retrofit){
        return null;
    }


    private Reply<Path> deleteFile(File file,OnProcessUpdate update) {
        if (file == null || !file.exists()) {
            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",null);
        }
        File[] files=file.isDirectory()?file.listFiles():null;
        if (null!=files&&files.length>0){//Delete child
            for (File child : files) {
                deleteFile(child,update); // 递规的方式删除文件夹
            }
        }
        Path path=Path.build(file);
        notifyProgress("Deleting file ",path,null,update);
        file.delete();
        return file.exists()?new Reply(true,What.WHAT_EXCEPTION,"Fail delete file",path):
                new Reply<>(true,What.WHAT_SUCCEED,"Succeed delete file",path);
    }

}
