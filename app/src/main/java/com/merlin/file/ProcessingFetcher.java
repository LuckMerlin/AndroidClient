package com.merlin.api;

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

    public interface Api{
        @POST("/file/processing")
        @FormUrlEncoded
        Call<Reply<Processing<,T,V>>> fetchProcessing(@Field(Label.LABEL_ID) String processingId);
    }

    public ProcessingFetcher(String processingId){
        mProcessingId=processingId;
    }

    protected abstract void onProcessingUpdate(Processing pr);

    public boolean fetch(Retrofit retrofit) throws IOException, InterruptedException {
        String processingId=mProcessingId;
        if (null!=retrofit&&null!=processingId&&processingId.length()>0){
            while (true){
                Call<Reply<Processing<M,T,V>>> call =retrofit.prepare(Api<M,T,V>.class,null,null)
                        .fetchProcessing(processingId);
                if (null!=call) {
                    Response<Reply<Processing>> response = call.execute();
                    Reply<Processing> reply = null != response ? response.body() : null;
                    if (null != reply) {
                        Processing data = reply.getData();
                        if (null != data) {
                            onProcessingUpdate(data);
                        }
                    }
                }
                Thread.sleep(4000);
            }
        }
        return false;
    }
}
