package com.merlin.server;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.api.Callback;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit {
    private final retrofit2.Retrofit.Builder mBuilder;

    public static class Canceler{
        Disposable mDisposable;
        public boolean cancel(boolean cancel,String debug){
            Disposable disposable=mDisposable;
            if (null!=disposable){
                if (cancel&&!disposable.isDisposed()){
                    disposable.dispose();
                    Debug.D(getClass(),"Cancel api call "+(null!=debug?debug:"."));
                    return true;
                }
            }
            return false;
        }
    }

    public Retrofit(){
        OkHttpClient.Builder okHttp = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).addInterceptor(new LogInterceptor());
        OkHttpClient client = okHttp.build();
        mBuilder = new retrofit2.Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public final <T>T prepare(Class<T>  cls,String url){
            return prepare(cls,url,null);
    }

    public final <T>T prepare(Class<T>  cls,String url,Executor callbackExecutor){
        retrofit2.Retrofit.Builder builder=mBuilder;
        builder=null!=builder?builder.baseUrl(url):null;
        if (null!=callbackExecutor&&null!=builder){
            builder.callbackExecutor(callbackExecutor);
        }
        retrofit2.Retrofit retrofit=null!=builder?builder.build():null;
        return null!=retrofit?retrofit.create(cls):null;
    }

    public final<T> Canceler call(Observable<T> observable, Callback ...callbacks){
        return call(observable,null,callbacks);
    }

    public final<T> Canceler call(Observable<T> observable, Scheduler observeOn, Callback ...callbacks){
        if (null!=observable){
            final Canceler canceler=new Canceler();
            observable.subscribeOn(Schedulers.io()).observeOn(null!=observeOn?observeOn:AndroidSchedulers.mainThread())
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
        private Canceler mCanceler;

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
            finishCall(What.WHAT_ERROR_UNKNOWN,null,mCallbacks,e,"After error.");
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
