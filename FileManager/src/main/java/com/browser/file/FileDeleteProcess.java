package com.browser.file;

import com.merlin.api.Label;
import com.merlin.api.OnProcessChange;
import com.merlin.api.Processing;
import com.merlin.api.ProcessingFetcher;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
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
        Call<Reply<Path>> delete(@Field(Label.LABEL_PATH) String path);
    }

    @Override
    protected void onCancelChange(boolean cancel, String debug) {
        super.onCancelChange(cancel, debug);
        Canceler canceler=mCanceler;
        if (null!=canceler){
            canceler.cancel(cancel,debug);
        }
    }

    @Override
    protected Reply<Path> onProcess(Path pathObj, OnProcessChange update, Retrofit retrofit) {
        Reply<Path> reply=null;
        final String filePath=null!=pathObj?pathObj.getPath():null;
        if (null==filePath|filePath.length()<=0){
            reply= new Reply(true,What.WHAT_EXCEPTION,"Path invalid.",pathObj);
        }else if (pathObj.isLocal()){//Delete local file
            LocalFileDelete localFileDelete=new LocalFileDelete();
            mCanceler=localFileDelete;
            reply=localFileDelete.deleteFile(new File(filePath),update);
        }else{//Delete cloud file
            final String hostUri=pathObj.getHostUri();
            if (null==hostUri||hostUri.length()<=0){
                reply = new Reply<>(true,What.WHAT_EXCEPTION,"Cloud path host uri invalid",pathObj);
            }else if (null==retrofit){
                reply= new Reply<>(true,What.WHAT_INTERRUPT,"Retrofit invalid",pathObj);
            }else{
                Call<Reply<Path>> call=retrofit.prepare(Api.class,hostUri).delete(filePath);
                try {
                   Response<Reply<Path>> response= null!=call?call.execute():null;
                   reply=null!=response?response.body():null;
                } catch (IOException e) {
                    reply= new Reply<>(true, What.WHAT_EXCEPTION, "Exception delete", null);
                    e.printStackTrace();
                }
            }
        }
        mCanceler=null;
        return reply;
    }

}
