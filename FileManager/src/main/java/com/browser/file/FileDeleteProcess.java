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
        Call<Reply<Processing>> delete(@Field(Label.LABEL_PATH) String ...paths);

        @POST("/file/processing")
        @FormUrlEncoded
        Call<Reply<Processing<Path>>> fetchProcessing(@Field(Label.LABEL_ID) String processId,@Field(Label.LABEL_WHAT) Integer what);

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
                Call<Reply<Processing>> call=retrofit.prepare(Api.class,hostUri).delete(filePath);
                if (null==call){
                    reply= new Reply<>(true,What.WHAT_INTERRUPT,"Cloud path delete call NULL",pathObj);
                }else{
                    final ProcessingFetcher fetcher= new ProcessingFetcher<Path>() {
                        @Override
                        protected Reply<Processing<Path>> onFetchProgressing(String processingId) throws IOException {
                            Response<Reply<Processing<Path>>> response= retrofit.prepare(Api.class,hostUri).
                                    fetchProcessing(processingId,super.isCanceled()?What.WHAT_CANCEL:null).execute();
                            return null!=response?response.body():null;
                        }
                    };
                    mCanceler=fetcher;
                    Reply<Processing> processingReply=fetcher.fetch(call,null);
                    reply=null!=processingReply?new Reply<>(processingReply.isSuccess(),
                            processingReply.getWhat(),processingReply.getNote(),pathObj):
                            new Reply<>(true,What.WHAT_UNKNOWN_INVALID, "Unknown process reply",pathObj);
                }
            }
        }
        mCanceler=null;
        return reply;
    }

}
