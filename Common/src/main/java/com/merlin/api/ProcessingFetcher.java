package com.merlin.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public abstract class ProcessingFetcher<T extends Processing> {

    public interface OnProcessingFetch<T extends Processing>{
        void onProcessingFetched(Reply<T> reply);
    }

    private interface Api{
        @POST("/file/delete")
        @FormUrlEncoded
        Call<Reply<Processing>> delete(@Field(Label.LABEL_WHAT) Integer what, @Field(Label.LABEL_PATH) String ...paths);
    }

//    public final Reply<>

    protected abstract Reply<T> onFetchProcessing(String processingId);

    public final Reply<T> fetch(Reply<Processing> reply,OnProcessingFetch<T> callback){
        Processing processing=null!=reply?reply.getData():null;
        return fetch(processing,callback);
    }

    public final Reply<T> fetch(Processing processing,OnProcessingFetch<T> callback){
        final String processingId=null!=processing?processing.getId():null;
        if (null==processingId||processingId.length()<=0){
            return new Reply<>(true,What.WHAT_ARGS_INVALID,"Processing is invalid",null);
        }
        Reply<T> processingReply=onFetchProcessing(processingId);
        int what=null!=processingReply?processingReply.getWhat():What.WHAT_INVALID;
        if (what==What.WHAT_NOT_EXIST){
            return processingReply;
        }
        processing=processingReply.getData();
        notify(processingReply,callback);
        int delay=null!=processing?processing.getPosition():1000;
        if (delay>0){
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return fetch(processing, callback);
    }

    protected final void notify(Reply<T> reply,OnProcessingFetch<T> callback){
        if (null!=callback){
            callback.onProcessingFetched(reply);
        }
    }
}
