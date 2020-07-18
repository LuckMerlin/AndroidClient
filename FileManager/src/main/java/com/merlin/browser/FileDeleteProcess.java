package com.merlin.browser;

import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.file.R;
import com.merlin.debug.Debug;
import com.merlin.lib.Cancel;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;
import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class FileDeleteProcess extends FileProcess<Path> {

    public FileDeleteProcess(){
        this(null,null);
    }

    public FileDeleteProcess(String title,ArrayList<Path> files){
        super(title,files);
    }

    private interface Api{
        @POST("/file/delete")
        @FormUrlEncoded
        Call<Reply<Processing>> delete(@Field(Label.LABEL_PATH) String path);
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
            update.onProcessUpdate(R.string.delete,path,null, path,null);
            Response<Reply<Processing>> response = null!=retrofit&&null!=path?retrofit.prepare(Api.class, path
                    .getHostUri(), null).delete(path.getPath()).execute():null;
            Reply<Processing> reply = null != response ? response.body() : null;
            if (null == reply) {
                update.onProcessUpdate(R.string.deleteFail, path, null, null,0);
                return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Process return NUll",null);
            }
            Processing processing = reply.getData();
            String processingId = null != processing && reply.isSuccess() && reply.getWhat() == What.WHAT_SUCCEED ? processing.getId() : null;
            if (null == processingId || processingId.length() <= 0) {//Delete launch fail
                update.onProcessUpdate(reply.getNote(), path, null, null, null != processing ? processing.getPosition() : null);
                return reply;
            }
            ProcessingFetcher fetcher = new ProcessingFetcher(processingId) {
                @Override
                protected void onProcessingUpdate(Processing<Path, Path, Reply<Path>> process) {
                    if (null!=process){
                        update.onProcessUpdate(null, null , null, process.getPath(),process.getPosition());
                    }
                }
            };
            return fetcher.delete(retrofit);
        } catch (Exception e) {
            Debug.E(getClass(), "Exception delete nas file.e=" + e, e);
            update.onProcessUpdate(R.string.exception, path, null, null,null);
            return new Reply(true, What.WHAT_ERROR_UNKNOWN, "Exception " + e, null);
        }
    }

    private Reply deleteLocalFile(File file,OnProcessUpdate update){
//        Path filePath=null;
//        if (null==file||!file.exists()||null==(filePath=Path.build(file,false))){
//            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.fileNotExist,filePath,null,file);
//        }else if (!file.canWrite()){
//            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.nonePermission,filePath,null,file);
//        }else{
//            File[] files=file.isDirectory()?file.listFiles():null;
//            if (null==files||files.length<=0){
//                file.delete();
//                Debug.D(getClass(),"Delete file "+file);
//                boolean succeed=!file.exists();
//                update.onProcessUpdate(succeed?What.WHAT_SUCCEED:What.WHAT_FAIL_UNKNOWN,
//                        R.string.nonePermission,filePath,null,file);
//                return succeed;
//            }
//            for (File child:files) {
//                if (null==child||!deleteLocalFile(child,update)){
//                    break;
//                }
//            }
//            files=file.isDirectory()?file.listFiles():null;
//            if (null==files||files.length<=0){
//                Debug.D(getClass(),"Delete folder "+file);
//                file.delete();
//            }
//            boolean succeed=!file.exists();
//            update.onProcessUpdate(succeed?What.WHAT_SUCCEED:What.WHAT_FAIL_UNKNOWN,R.string.nonePermission,filePath,null,file);
//            return succeed;
//        }
        return null;
    }

}
