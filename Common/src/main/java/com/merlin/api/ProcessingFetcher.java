package com.merlin.api;

import com.merlin.debug.Debug;
import com.merlin.lib.Cancel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public final class ProcessingFetcher<T> extends Cancel {

    public interface OnProcessingFetch<T>{
        void onProcessingFetched(Reply<Processing<T>> reply);
    }

    public final Reply<Processing<T>> fetch(Call<Reply<Processing<T>>> call,OnProcessingFetch callback){
        call=null!=call?call.isExecuted()?call.clone():call:null;
        if (null==call){
            return new Reply<>(true,What.WHAT_ARGS_INVALID,"Processing fetch call invalid.",null);
        }
        Reply<Processing<T>> processingReply=null;
        try {
            Response<Reply<Processing<T>>> response=call.execute();
            Reply<Processing<T>> reply=processingReply=null!=response?response.body():null;
            if (null!=reply){
                notify(reply,callback);
                int what=reply.getWhat();
                if (what==What.WHAT_NOT_EXIST){
                    return processingReply;
                }
                Processing processing=reply.getData();
                int delay=null!=processing?processing.getPosition():1000;
                if (delay>0){
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            Debug.D(getClass(),"Exception fetch processing.e="+e);
            e.printStackTrace();
        }
        return null!=processingReply?processingReply:fetch(call,callback);
    }

    protected final void notify(Reply<Processing<T>> reply,OnProcessingFetch<T> callback){
        if (null!=callback){
            callback.onProcessingFetched(reply);
        }
    }
}
