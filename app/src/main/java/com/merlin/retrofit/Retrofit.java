package com.merlin.retrofit;

import android.app.Activity;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.api.Address;
import com.merlin.api.Response;
import com.merlin.debug.Debug;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Retrofit implements What{
    private WeakReference<LifecycleProvider> mLifecycleProvider;
    private final String mUrl;
    private final int mTimeout=10;
    public Retrofit(){
        this(null);
    }

    public interface ApiCallback<T>{

    }

    public interface OnApiFinish<T> extends ApiCallback<T>{
        void onApiFinish(int what,String note,T data,Object arg);
    }

    public Retrofit(String url){
        mUrl=null!=url&&url.length()>0?url: Address.URL;
    }

    public final <T> T prepare(Class<T> cls){
        return prepare(cls,null);
    }

    public final <T> T prepare(Class<T> cls,String url){
        final String uri=null!=url&&url.length()>0?url:mUrl;
        final int timeout =mTimeout>=0?mTimeout:10;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(timeout, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(timeout, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(timeout,TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new LogInterceptor())//添加打印拦截器
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().client(client)
                .baseUrl(uri).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(cls);
    }

    public final <T> T call(Class<T> cls,ApiCallback ...callbacks){
        if (null!=cls){
            return (T)Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls},(proxy, method,args)->{
                        T instance=prepare(cls);
                        Object ret=null!=instance?method.invoke(instance,args):null;
                        if (null!=ret&&ret instanceof Observable) {
                            subscribe((Observable) ret, callbacks);
                        }
                        return ret;
                    }
            );
        }
        return null;
    }

    public final <M> boolean subscribe(Observable observable,ApiCallback ...callbacks){
        if (null==observable){
            onFinish(WHAT_ARGS_INVALID,"Observable is Null.",null,callbacks);
            return false;
        }
        observable=observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        WeakReference<LifecycleProvider> reference=mLifecycleProvider;
        LifecycleProvider provider=null!=reference?reference.get():null;
        if (null!=provider&&provider instanceof Activity){
            observable.compose(provider.bindUntilEvent(ActivityEvent.DESTROY));
        }
        if (null!=observable){
            observable.subscribe(new Callback<M>(callbacks));
        }
        return null!=observable;
    }

    private class Callback<M> extends DisposableObserver<M> {
        private final ApiCallback[] mCallbacks;
        private String mMessage;
        private int mWhat;
        private M mData;

        public Callback(ApiCallback ...callbacks){
            mCallbacks=callbacks;
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            mWhat=WHAT_UNKNOWN_ERROR;
            mMessage=e.toString();
            if (e instanceof HttpException) {
                HttpException httpException = (HttpException) e;
                //httpException.response().errorBody().string()
                int code = httpException.code();
                mMessage = httpException.getMessage();
                if (code == 504) {
                    mMessage=" 网速慢";
                    mWhat=WHAT_NETWORK_POOR;
                } else if (code == 502) {
                    mMessage = "服务器异常，请稍后再试";
                    mWhat=WHAT_SERVER_EXCEPTION;
                } else if (code == 408) {
                    mMessage = "请求超时";
                    mWhat=WHAT_TIMEOUT;
                } else if (code == 403) {
                    mMessage = "没有权限访问";
                    mWhat=WHAT_NONE_PERMISSION;
                } else if (code == 401) {
                    mMessage="token失败";
                    mWhat=WHAT_TOKEN_INVALID;
                }
            }
            onFinish(mWhat,mMessage,e,mCallbacks);
        }

        @Override
        public void onNext(M model) {
            mWhat=WHAT_RESPONSE;
            mData=model;
            mMessage="收到数据了";
        }

        @Override
        public void onComplete() {
            onFinish(mWhat,mMessage="结束了",mData,mCallbacks);
        }

    }

    private boolean onFinish(int what,String note,Object data,ApiCallback ...callbacks){
        if (null!=callbacks&&callbacks.length>0){
            for (ApiCallback callback:callbacks) {
                if (null!=callback&&callback instanceof OnApiFinish){
                    Type ddd=callback.getClass().getGenericSuperclass();
                    Debug.D(getClass(),"QQQQQQQQQQQQQQ "+ddd+" "+data);
                    ((OnApiFinish)callback).onApiFinish(what,note,null,null);
                }
            }
            return true;

        }
        return false;
    }

}
