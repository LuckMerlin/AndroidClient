package com.merlin.server;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.api.Callback;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit {
    private final retrofit2.Retrofit.Builder mBuilder;

    public interface Callback{

    }

    public interface OnApiFinish<T> extends Callback{
        void onApiFinish(boolean succeed,int what,String note,T data, Object arg);
    }

    public Retrofit(){
        OkHttpClient.Builder okHttp = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS).addInterceptor(new LogInterceptor());
        OkHttpClient client = okHttp.build();
        mBuilder = new retrofit2.Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public final <T>T prepare(Class<T>  cls,String url){
        retrofit2.Retrofit.Builder builder=mBuilder;
        retrofit2.Retrofit retrofit=null!=builder?builder.baseUrl(url).build():null;
        return null!=retrofit?retrofit.create(cls):null;
    }

    public final<T> boolean call(Observable<T> observable,Callback ...callbacks){
        if (null!=observable){
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new InnerCallback<>(callbacks));
            return true;
        }
        Debug.W(getClass(),"Can't call retrofit, Observable is NULL.");
        return false;
    }

    private static final class InnerCallback <T>implements Observer<T> {
        private final Callback[] mCallbacks;

        private InnerCallback(Callback ...callbacks){
            mCallbacks=callbacks;
        }

        @Override
        public void onSubscribe(Disposable d) {
//            Debug.D(getClass(),"SonSubscribeSS "+d);
        }

        @Override
        public void onNext(T t) {
            finishCall(null,t,mCallbacks,null,"After next.");
        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {
            finishCall(What.WHAT_ERROR_UNKNOWN,null,mCallbacks,e,"After error.");
        }

        private void finishCall(Integer what,Object data,Callback[] callbacks,Object arg,String debug){
            if (null!=callbacks&&callbacks.length>0){
                for (Callback callback:callbacks) {
                    if(null==callback||!(callback instanceof OnApiFinish)){
                        continue;
                    }
                    String note=null;int childWhat=0;boolean succeed=false;
                    data=data!=null&&checkDataGeneric(data,callback)?data:null;
                    if (null!=data&&data instanceof Reply){
                        Reply reply=(Reply)data;
                        childWhat=null!=what?what:reply.getWhat();
                        note=reply.getNote();
                        succeed=reply.isSuccess();
                    }else{
                        data=null;
                    }
                    ((OnApiFinish) callback).onApiFinish(succeed,childWhat,note,data,arg);
                }
            }
        }

        private boolean checkDataGeneric(Object data, Callback callback){
            return null!=data&&null!=callback&&!(data instanceof Class);
        }

    }

}
