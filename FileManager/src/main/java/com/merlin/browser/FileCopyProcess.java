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

    public FileCopyProcess(){
        this(null,null);
    }

    public FileCopyProcess(String title, ArrayList<Path> files){
        super(title,files);
    }

    private interface Api{
        @POST("/file/delete")
        @FormUrlEncoded
        Call<Reply<Processing>> copy(@Field(Label.LABEL_PATH) String path);
    }

    @Override
    protected Reply onProcess(Path pathObj,OnProcessUpdate update, Retrofit retrofit) {
        final String path=null!=pathObj?pathObj.getPath():null;
        if (null==path|path.length()<=0){
            return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Path invalid.",path);
        }else if (pathObj.isLocal()){//Delete local file
            return deleteLocalFile(new File(path),update);
        }
        return deleteCloudFile(retrofit, pathObj, update);//Delete cloud file
    }

    private Reply deleteCloudFile(Retrofit retrofit, Path path, OnProcessUpdate update){
        try {
            update.onProcessUpdate(R.string.delete,path,null, path,null,null);
            Response<Reply<Processing>> response = null!=retrofit&&null!=path?retrofit.prepare(Api.class, path
                    .getHostUri(), null).delete(path.getPath()).execute():null;
            Reply<Processing> reply = null != response ? response.body() : null;
            if (null == reply) {
                update.onProcessUpdate(R.string.deleteFail, path, null, null,0,null);
                return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Process return NUll",null);
            }
            if (isCancel()){
                return new Reply(true,What.WHAT_CANCEL,"Delete cancel",null);
            }
            Processing processing = reply.getData();
            String processingId = null != processing && reply.isSuccess() && reply.getWhat() == What.WHAT_SUCCEED ? processing.getId() : null;
            if (null == processingId || processingId.length() <= 0) {//Delete launch fail
                update.onProcessUpdate(reply.getNote(), path, null, null, null != processing ? processing.getPosition() : null,null);
                return reply;
            }
            ProcessingFetcher fetcher = new ProcessingFetcher(processingId) {
                @Override
                protected boolean isCanceled() {
                    return FileCopyProcess.this.isCancel();
                }

                @Override
                protected void onProcessingUpdate(Processing<Path, Path, Reply<Path>> process) {
                    if (null!=process){
                        update.onProcessUpdate(null, null , null, process.getPath(),process.getPosition(),null);
                    }
                }
            };
            return fetcher.delete(retrofit);
        } catch (Exception e) {
            Debug.E(getClass(), "Exception delete nas file.e=" + e, e);
            update.onProcessUpdate(R.string.exception, path, null, null,null,null);
            return new Reply(true, What.WHAT_ERROR_UNKNOWN, "Exception " + e, null);
        }
    }

    private Reply<Path> deleteLocalFile(File file,OnProcessUpdate update){
        Path filePath=null;
        if (null==file||!file.exists()||null==(filePath=Path.build(file))){
            return new Reply<>(true,What.WHAT_NOT_EXIST,"File not exist",filePath);
        }else if (!file.canWrite()){
            return new Reply<>(true,What.WHAT_NONE_PERMISSION,"File none permission",filePath);
        }else if (isCancel()){
            return new Reply<>(true,What.WHAT_CANCEL,"Cancel delete",filePath);
        }else{
            File[] files=file.isDirectory()?file.listFiles():null;
            if (null!=files&&files.length>0){
                for (File child:files) {
                    deleteLocalFile(child,update);
                }
            }
            file.delete();
            if (null!=update){
                update.onProcessUpdate("Delete file",filePath,null,filePath,0,null);
            }
        }
        boolean exist=file.exists();
        Debug.D(getClass(),"Delete file "+exist+" "+file);
        return new Reply<>(true,exist?What.WHAT_ERROR_UNKNOWN:What.WHAT_SUCCEED,
                exist?"Delete fail":"Fail delete file",filePath);
    }

}
