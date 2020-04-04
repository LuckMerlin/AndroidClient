package com.merlin.server;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.api.Callback;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import java.net.ConnectException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit {
    private final retrofit2.Retrofit.Builder mBuilder;

    public Retrofit(){
        OkHttpClient.Builder okHttp = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new LogInterceptor()).connectionPool(new ConnectionPool(5,1,TimeUnit.SECONDS));
        OkHttpClient client = okHttp.build();
        mBuilder = new retrofit2.Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public final <T>T prepare(Class<T>  cls,String url){
            return prepare(cls,url,null);
    }

    public final <T>T prepare(Class<T>  cls,String url,Executor callbackExecutor){
        if (null==url||url.length()<=0){
            Debug.E(getClass(),"None url to prepare.");
            throw new RuntimeException("None url to prepare.");
        }
        retrofit2.Retrofit.Builder builder=mBuilder;
        builder=null!=builder?builder.baseUrl(url):null;
        if (null!=callbackExecutor&&null!=builder){
            builder.callbackExecutor(callbackExecutor);
        }
        retrofit2.Retrofit retrofit=null!=builder?builder.build():null;
        return null!=retrofit?retrofit.create(cls):null;
    }

    public final<T> RetrofitCanceler call(Observable<T> observable, Callback ...callbacks){
        return call(observable,null,null,callbacks);
    }

    public final<T> RetrofitCanceler call(Observable<T> observable,Scheduler subscribeOn, Scheduler observeOn, Callback ...callbacks){
        if (null!=observable){
            final RetrofitCanceler canceler=new RetrofitCanceler();
            observable.subscribeOn(null!=subscribeOn?subscribeOn:Schedulers.io()).observeOn(null!=observeOn?observeOn:AndroidSchedulers.mainThread())
                    .subscribe(new InnerCallback<T>(callbacks){
                        @Override
                        public void onSubscribe(Disposable d) {
                            canceler.mDisposable=d;
                        }

                        @Override
                        public void onComplete() {
                            canceler.mDisposable=null;
                        }
                    });
            return canceler;
        }
        Debug.W(getClass(),"Can't call retrofit, Observable is NULL.");
        return null;
    }

    private static abstract class InnerCallback <T>implements Observer<T> {
        private final Callback[] mCallbacks;
        private RetrofitCanceler mCanceler;

        private InnerCallback(Callback ...callbacks){
            mCallbacks=callbacks;
        }


        @Override
        public void onNext(T t) {
            finishCall(null,t,mCallbacks,null,"After next.");
        }

        @Override
        public void onError(Throwable e) {
            Debug.E(getClass(),"Error on api "+e,e);
            int what=What.WHAT_ERROR_UNKNOWN;
            String note="After error.";
            if (null!=e){
                if (e instanceof ConnectException){
                    what=What.WHAT_NONE_NETWORK;
                    note="None network";
                }
            }
            finishCall(what,null,mCallbacks,e,note);
        }

        private void finishCall(Integer what,Object data,Callback[] callbacks,Object arg,String debug){
            if (null!=callbacks&&callbacks.length>0){
                for (Callback callback:callbacks) {
                    if(null==callback||!(callback instanceof OnApiFinish)){
                        continue;
                    }
                    String note=null;int childWhat=What.WHAT_FAIL_UNKNOWN;boolean succeed=false;
                    data=data!=null&&checkDataGeneric(data,callback)?data:null;
                    if (null!=data&&data instanceof Reply){
                        Reply reply=(Reply)data;
                        childWhat=null!=what?what:reply.getWhat();
                        note=reply.getNote();
                        succeed=reply.isSuccess();
                    }else{
                        data=null;
                    }
                    ((OnApiFinish) callback).onApiFinish(childWhat,note,data,arg);
                }
            }
        }

        private boolean checkDataGeneric(Object data, Callback callback){
            return null!=data&&null!=callback&&!(data instanceof Class);
        }

    }

}
