package com.merlin.api;

import com.merlin.debug.Debug;
import com.merlin.lib.Cancel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class ProcessingFetcher<T,M> extends Cancel {

    protected abstract Reply<Processing<T,M> > onFetchProgressing(String processingId)throws IOException;

    public final Reply<M> fetch(Call<Reply<Processing>> call, OnProcessChange callback){
        call=null!=call&&call.isExecuted()?call.clone():call;
        if (null==call){
            return new Reply<>(true,What.WHAT_ARGS_INVALID,"Processing fetch call invalid.",null);
        }
        if (isCanceled()){
            Debug.D(getClass(),"Finish fetch process while canceled");
            return new Reply<>(true,What.WHAT_CANCEL,"Process canceled",null);
        }
        try {
            Response<Reply<Processing>> response=call.execute();
            if (null==response){
                return fetch(call,callback);
            }
            Reply<Processing> reply=response.body();
            if (null==reply){
                Debug.D(getClass(),"Finish fetch process while reply NULL");
                return new Reply<>(true,What.WHAT_ERROR,"Process reply NULL",null);
            }
            Processing processing=reply.getData();
            final String processingId=null!=processing?processing.getId():null;
            if (null==processingId||processingId.length()<=0){
                Debug.D(getClass(),"Finish fetch process while reply processing ID invalid");
                return new Reply<>(true,What.WHAT_ERROR,"Reply processing ID invalid",null);
            }
            return fetch(processingId,callback);
        } catch (IOException e) {
            Debug.D(getClass(),"Exception fetch processing.e="+e);
            e.printStackTrace();
            return new Reply<>(true,What.WHAT_ERROR,"Reply Exception "+e,null);
        }
    }

    public final Reply<M> fetch(String processingId,OnProcessChange callback){
        Reply<Processing<T,M> > response= null;
        try {
            response = onFetchProgressing(processingId);
            if (null!=response){
                int what=response.getWhat();
                if (what==What.WHAT_NOT_EXIST){
                    return new Reply<>(true,What.WHAT_NOT_EXIST,"Processing not exist",null);
                }
                Processing processing=response.getData();
                if (null!=processing){
                    notify(processing.getPosition(),processing.getData(),callback);
                    Reply<M> reply=processing.getTerminal();
                    if (null!=reply){//Terminal attached
                        return reply;
                    }
                    int delay=processing.getDuration();
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
            e.printStackTrace();
        }
        return fetch(processingId,callback);
    }

    protected final void notify(Float progress,List<T> processed, OnProcessChange callback){
        if (null!=callback){
            callback.onProcessChanged(progress,null,null!=processed&&processed.size()>0?processed.get(0):null,processed);
        }
    }
}
