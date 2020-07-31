package com.merlin.browser;

import com.merlin.api.Label;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.retrofit.Retrofit;

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

    private Reply copyLocalFileToLocal(File from,File to,int coverMode,OnProcessUpdate update){

        return null;
    }

    private Reply downloadToLocal(String fromHostUri,String fromPath,File toFolder,int coverMode,OnProcessUpdate update,Retrofit retrofit){
        return null;
    }


}
