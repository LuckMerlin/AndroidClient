package com.merlin.api;

import com.merlin.retrofit.Retrofit;
import com.merlin.retrofit.RetrofitCanceler;
import io.reactivex.Observable;

public class ProcessingFetcher {

    public  RetrofitCanceler call(Retrofit retrofit, Observable<Reply<Processing>> observable, OnApiFinish<Processing> finish){
        if (null!=retrofit&&null!=observable){
            return null!=retrofit?retrofit.call(observable,finish):null;
        }
        return null;
    }
}
