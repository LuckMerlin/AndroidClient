package com.merlin.retrofit;

import android.app.Activity;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.api.Address;
import com.merlin.api.Callback;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Retrofit implements What {
    private WeakReference<LifecycleProvider> mLifecycleProvider;
    private final String mUrl;
    private final int mTimeout=10;
    private List<Object> mDitherList;

    public Retrofit(){
        this(null);
    }

    public Retrofit(String url){
        mUrl=null!=url&&url.length()>0?url: Address.URL;
    }

    public final <T> T prepare(Class<T> cls,Interceptor ...interceptors){
        return prepare(cls,null,interceptors);
    }

    public final <T> T prepare(Class<T> cls, String url, Interceptor ...interceptors){
        final String uri=null!=url&&url.length()>0?url:mUrl;
        final int timeout =mTimeout>=0?mTimeout:10;
        OkHttpClient.Builder builder=new OkHttpClient().newBuilder()
                .readTimeout(timeout, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(timeout, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(timeout,TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new LogInterceptor());//添加打印拦截器
        if (null!=interceptors&&interceptors.length>0){
            for (Interceptor interceptor:interceptors) {
                if (null!=interceptor){
                    builder.addInterceptor(interceptor);
                }
            }
        }
        builder.retryOnConnectionFailure(true);//设置出现错误进行重新连接。
        OkHttpClient client = builder.build();
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder().client(client)
                .baseUrl(uri).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(cls);
    }

    public final <T> T call(Class<T> cls,Object dither, Callback...callbacks){
        return call(cls,null,dither,callbacks);
    }

    public final <T> T call(Class<T> cls,Scheduler observeOn,Object dither, Callback...callbacks){
        return call(cls,null,observeOn,dither,callbacks);
    }

    public final <T> T call(Class<T> cls, Interceptor[] interceptors, Scheduler observeOn, Object dither, Callback...callbacks){
        if (null!=cls){
            return (T)Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls},(proxy, method,args)->{
                        if (null==dither||!isExistDither(dither)){
                            T instance=prepare(cls,interceptors);
                            Object ret=null!=instance?method.invoke(instance,args):null;
                            if (null!=ret&&ret instanceof Observable) {
                                final OnApiFinish finish=null!=dither?(what, note, data, arg)->{
                                        removeDither(dither);
                                }:null;
                                if (null!=dither){
                                    addDither(dither);
                                }
                                subscribe((Observable) ret,observeOn,finish, callbacks);
                            }
                            return ret;
                        }
                        finish(WHAT_ALREADY_DONE,"Already exist equal query.",null,null,null,callbacks);
                        return null;
                    }
            );
        }
        return null;
    }

    public final <M> boolean subscribe(Observable observable, Scheduler observeOn,OnApiFinish innerFinish, Callback ...callbacks){
        if (null==observable){
            finish(WHAT_ARGS_INVALID,"Observable is Null.",null,null,innerFinish,callbacks);
            return false;
        }
        observable=observable.subscribeOn(Schedulers.newThread()).observeOn(null!=observeOn?observeOn:AndroidSchedulers.mainThread());
        WeakReference<LifecycleProvider> reference=mLifecycleProvider;
        LifecycleProvider provider=null!=reference?reference.get():null;
        if (null!=provider&&provider instanceof Activity){
            observable.compose(provider.bindUntilEvent(ActivityEvent.DESTROY));
        }
        if (null!=observable){
            observable.subscribe(new InnerCallback(innerFinish,callbacks));
        }
        return null!=observable;
    }

    private class InnerCallback<M> extends DisposableObserver<M> {
        private final Callback[] mCallbacks;
        private final OnApiFinish mInnerFinish;
        private String mMessage;
        private Integer mWhat;
        private M mData;

        public InnerCallback(OnApiFinish innerFinish,Callback ...callbacks){
            mCallbacks=callbacks;
            mInnerFinish=innerFinish;
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            mWhat=WHAT_ERROR_UNKNOWN;
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
            finish(mWhat,mMessage,null,e,mInnerFinish,mCallbacks);
        }

        @Override
        public void onNext(M model) {
            mData=model;
            mMessage=null;
        }

        @Override
        public void onComplete() {
            finish(mWhat,mMessage=null,mData,null,mInnerFinish,mCallbacks);
        }

    }

    private boolean finish(Integer what,String note,Object data,Object arg,OnApiFinish finish,Callback ...callbacks){
        if (null!=callbacks&&callbacks.length>0){
            if (null!=finish){
                data=data!=null&&checkDataGeneric(data,finish)?data:null;
                if (null!=data&&data instanceof Reply){
                    what=null!=what?what:((Reply)data).getWhat();
                    note=((Reply)data).getNote();
                }else{
                    data=null;
                }
                finish.onApiFinish(what,note,data,arg);
            }
            for (Callback callback:callbacks) {
                if (null!=callback&&callback instanceof OnApiFinish){
                    data=data!=null&&checkDataGeneric(data,callback)?data:null;
                    if (null!=data&&data instanceof Reply){
                        what=null!=what?what:((Reply)data).getWhat();
                        note=((Reply)data).getNote();
                    }else{
                        data=null;
                    }
                    ((OnApiFinish) callback).onApiFinish(what,note,data,arg);
                }
            }
            return true;

        }
        return false;
    }

    private boolean checkDataGeneric(Object data,Callback callback){
        if (null!=data&&null!=callback&&!(data instanceof Class)){
            return true;
        }
        Debug.W(getClass(),"Can't  check data generic type if valid."+data+" "+callback);
        return false;
    }

    private synchronized boolean addDither(Object dither){
        List<Object> list=null!=dither?(null!=mDitherList?mDitherList:(mDitherList=new ArrayList<>())):null;
        if (null!=list){
            synchronized (list) {
                if (!list.contains(dither) && list.add(dither)) {
                    return true;
                }
            }
        }
        return false;
    }

    private synchronized boolean removeDither(Object dither){
        List<Object> list=null!=dither?mDitherList:null;
        if (null!=list){
            List<Object> needRemove=new ArrayList<>();
            synchronized (list){
                for (Object child:list){
                    if (null!=child&&child.equals(dither)){
                        needRemove.add(child);
                    }
                }
                list.removeAll(needRemove);
            }
            return null!=list&&list.size()>0;
        }
        return false;
    }

    private synchronized boolean isExistDither(Object querying){
        List<Object> list=null!=querying?mDitherList:null;
        if (null!=list){
            synchronized (list){
                for (Object child:list){
                    if (null!=child&&child.equals(querying)){
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
