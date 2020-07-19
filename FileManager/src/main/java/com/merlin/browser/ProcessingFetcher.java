package com.merlin.browser;

import com.merlin.api.Label;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.retrofit.Retrofit;
import com.merlin.retrofit.RetrofitCanceler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public abstract class ProcessingFetcher{
    private final String mProcessingId;
    private List<Path> mProcessed;

    protected abstract boolean isCanceled();

    public interface OnPathDeleteProcessingChange{

    }

    private interface Api{
        @POST("/file/processing")
        @FormUrlEncoded
        Call<Reply<Processing<Path,Path,Reply<Path>>>> fetchProcessing(@Field(Label.LABEL_ID) String processingId,@Field(Label.LABEL_ACCESS) boolean resetProcessedList);

        @POST("/file/processing")
        @FormUrlEncoded
        Call<Reply<Processing>> cancelProcess(@Field(Label.LABEL_ID) String processingId,@Field(Label.LABEL_CANCEL)boolean cancel);
    }

    public ProcessingFetcher(String processingId){
        mProcessingId=processingId;
    }

    protected final Reply<Processing> cancelProcess(String processId,Retrofit retrofit) {
        Response<Reply<Processing>> response= null;
        try {
            response = null!=retrofit&&null!=processId&&processId.length()>0?retrofit.prepare
                    (Api.class,null,null).cancelProcess(processId,true).execute():null;
            Reply<Processing> reply= null!=response?response.body():null;
            return reply;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public final List<Path> getProcessed() {
        return mProcessed;
    }

    protected abstract void onProcessingUpdate(Processing<Path,Path,Reply<Path>> pr);

    public Reply delete(Retrofit retrofit) throws IOException {
        String processingId=mProcessingId;
        if (null!=retrofit&&null!=processingId&&processingId.length()>0){
            while (true){
                Reply<Processing> cancelReply=isCanceled()?cancelProcess(processingId,retrofit):null;
                int cancelWhat=null!=cancelReply?cancelReply.getWhat():What.WHAT_INVALID;
                if (cancelWhat==What.WHAT_ALREADY_DONE||cancelWhat== What.WHAT_SUCCEED){
                    return new Reply(true,What.WHAT_CANCEL,"Canceled",null);
                }
                Call<Reply<Processing<Path,Path,Reply<Path>>>> call =retrofit.prepare(Api.class,null,null)
                        .fetchProcessing(processingId,true);
                if (null!=call) {
                    Response<Reply<Processing<Path,Path,Reply<Path>>>> response = call.execute();
                    Reply<Processing<Path,Path,Reply<Path>>> reply = null != response ? response.body() : null;
                    if (null != reply) {
                        if (reply.getWhat()==What.WHAT_NOT_EXIST){//Processing not exist
                            return reply;
                        }
                        Processing<Path,Path,Reply<Path>> data = reply.getData();
                        if (null != data) {
                            onProcessingUpdate(data);
                            List<Path> paths=data.getData();
                            int size=null!=paths?paths.size():-1;
                            List<Path> result=mProcessed;
                            if (size>0){
                                ((null==result?(mProcessed=new ArrayList<>(size)):result)).addAll(paths);
                            }
                            Reply<Path> terminal=data.getTerminal();
                            if (null!=terminal) {//If terminal
                                return terminal;
                            }
                        }
                    }
                }
            }

        }
        return null;
    }
}
