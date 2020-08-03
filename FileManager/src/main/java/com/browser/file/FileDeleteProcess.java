package com.browser.file;

import com.merlin.api.Label;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;
import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class FileDeleteProcess extends FileProcess<Path> {
    private Canceler mCanceler=null;

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
    protected void onCanceled(boolean cancel, String debug) {
        super.onCanceled(cancel, debug);
    }

    @Override
    protected Reply onProcess(Path pathObj, ProcessProgress update, Retrofit retrofit) {
        Reply<Path> reply;
        final String path=null!=pathObj?pathObj.getPath():null;
        if (null==path|path.length()<=0){
            reply= new Reply(true,What.WHAT_ERROR,"Path invalid.",path);
        }else if (pathObj.isLocal()){//Delete local file
            LocalFileDelete localFileDelete=new LocalFileDelete();
            mCanceler=localFileDelete;
            reply=localFileDelete.deleteFile(new File(path),update);
        }else{
            reply=deleteCloudFile(retrofit, pathObj, update);//Delete cloud file
        }
        mCanceler=null;
        return reply;
    }

    private Reply deleteCloudFile(Retrofit retrofit, Path path, ProcessProgress update){
//        try {
//            update.onProcessUpdate(R.string.delete,path,null, path,null,null);
//            Response<Reply<Processing>> response = null!=retrofit&&null!=path?retrofit.prepare(Api.class, path
//                    .getHostUri(), null).delete(path.getPath()).execute():null;
//            Reply<Processing> reply = null != response ? response.body() : null;
//            if (null == reply) {
//                update.onProcessUpdate(R.string.deleteFail, path, null, null,0,null);
//                return new Reply(true,What.WHAT_ERROR,"Process return NUll",null);
//            }
//            if (isCancel()){
//                return new Reply(true,What.WHAT_CANCEL,"Delete cancel",null);
//            }
//            Processing processing = reply.getData();
//            String processingId = null != processing && reply.isSuccess() && reply.getWhat() == What.WHAT_SUCCEED ? processing.getId() : null;
//            if (null == processingId || processingId.length() <= 0) {//Delete launch fail
//                update.onProcessUpdate(reply.getNote(), path, null, null, null != processing ? processing.getPosition() : null,null);
//                return reply;
//            }
//            ProcessingFetcher fetcher = new ProcessingFetcher(processingId) {
//                @Override
//                protected boolean isCanceled() {
//                    return FileDeleteProcess.this.isCancel();
//                }
//
//                @Override
//                protected void onProcessingUpdate(Processing<Path, Path, Reply<Path>> process) {
//                    if (null!=process){
//                        update.onProcessUpdate(null, null , null, process.getPath(),process.getPosition(),null);
//                    }
//                }
//            };
//            return fetcher.delete(retrofit);
//        } catch (Exception e) {
//            Debug.E(getClass(), "Exception delete nas file.e=" + e, e);
//            update.onProcessUpdate(R.string.exception, path, null, null,null,null);
//            return new Reply(true, What.WHAT_ERROR, "Exception " + e, null);
//        }
        return null;
    }

}
