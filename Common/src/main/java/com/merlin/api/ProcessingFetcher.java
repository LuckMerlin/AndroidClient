package com.merlin.api;

import com.merlin.retrofit.Retrofit;
import com.merlin.retrofit.RetrofitCanceler;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class ProcessingFetcher {

    private interface Api{
        @POST("/file/processing")
        @FormUrlEncoded
        Observable<Reply<Processing>> fetchProcessing(@Field(Label.LABEL_ID) String processingId);
    }

    public  RetrofitCanceler call(Retrofit retrofit, Observable<Reply<Processing>> observable, OnApiFinish<Processing> finish){
        if (null!=retrofit&&null!=observable){
            return null!=retrofit?retrofit.call(observable,finish):null;
        }
        return null;
    }

    public boolean fetch(Retrofit retrofit,String processingId){
        if (null!=retrofit&&null!=processingId&&processingId.length()>0){
            Observable<Reply<Processing>> observable =retrofit.prepare(Api.class,null,null).fetchProcessing(processingId);
            return null!=retrofit.call(observable.subscribeOn(Schedulers.io()).doOnSubscribe((Disposable disposable) ->{
                retrofit.call(observable.subscribeOn(Schedulers.single()), new OnApiFinish<Reply<Processing>>() {
                    @Override
                    public void onApiFinish(int what, String note, Reply<Processing> data, Object arg) {

                    }
                });
                disposable.dispose();
            }));
        }
        return false;
    }
}
