package com.merlin.api;

import com.merlin.debug.Debug;
import com.merlin.lib.Cancel;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public final class ProcessingFetcher extends Cancel {

    public interface OnProcessingFetch{
        void onProcessingFetched(Reply<Processing> reply);
    }

    public final Reply<Processing> fetch(Call<Reply<Processing>> call, OnProcessingFetch callback){
        call=null!=call?call.isExecuted()?call.clone():call:null;
        if (null==call){
            return new Reply<>(true,What.WHAT_ARGS_INVALID,"Processing fetch call invalid.",null);
        }
        if (isCanceled()){
            Debug.D(getClass(),"Finish fetch process while canceled");
            return new Reply<>(true,What.WHAT_CANCEL,"Process canceled",null);
        }
        Reply<Processing> processingReply=null;
        try {
            Response<Reply<Processing>> response=call.execute();
            Reply<Processing> reply=processingReply=null!=response?response.body():null;
            if (null!=reply){
                notify(reply,callback);
                int what=reply.getWhat();
                if (what==What.WHAT_NOT_EXIST){
                    return processingReply;
                }
                Processing processing=reply.getData();
                if (null==processing){
                    Debug.D(getClass(),"Finish fetch process while reply processing NULL ");
                    processingReply= new Reply<>(reply.isSuccess(),reply.getWhat(),reply.getNote(),null);
                }else{
                    int delay=processing.getPosition();
                    if (delay>0){
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            Debug.D(getClass(),"Exception fetch processing.e="+e);
            e.printStackTrace();
        }
        return null!=processingReply?processingReply:fetch(call,callback);
    }

    protected final void notify(Reply<Processing> reply,OnProcessingFetch callback){
        if (null!=callback){
            callback.onProcessingFetched(reply);
        }
    }
}
