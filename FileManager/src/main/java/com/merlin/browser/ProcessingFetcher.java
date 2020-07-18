package com.merlin.browser;

import com.merlin.api.Label;
import com.merlin.api.Processing;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.retrofit.Retrofit;
import com.merlin.retrofit.RetrofitCanceler;

import java.io.IOException;

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

    public interface OnPathDeleteProcessingChange{

    }

    private interface Api{
        @POST("/file/processing")
        @FormUrlEncoded
        Call<Reply<Processing<Path,Path,Reply<Path>>>> fetchProcessing(@Field(Label.LABEL_ID) String processingId);
    }

    public ProcessingFetcher(String processingId){
        mProcessingId=processingId;
    }

    protected abstract void onProcessingUpdate(Processing<Path,Path,Reply<Path>> pr);

    public Reply delete(Retrofit retrofit) throws IOException, InterruptedException {
        String processingId=mProcessingId;
        if (null!=retrofit&&null!=processingId&&processingId.length()>0){
            while (true){
                Call<Reply<Processing<Path,Path,Reply<Path>>>> call =retrofit.prepare(Api.class,null,null)
                        .fetchProcessing(processingId);
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
                            Reply<Path> terminal=data.getTerminal();
                            if (null!=terminal) {//If terminal
                                return terminal;
                            }
                        }
                    }
                }
                Thread.sleep(100);
            }
        }
        return null;
    }
}
