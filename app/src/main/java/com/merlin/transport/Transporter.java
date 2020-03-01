package com.merlin.transport;

import android.content.Context;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.bean.File;
import com.merlin.classes.Classes;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public abstract class Transporter {
    protected abstract Context getContext();
    private final Retrofit.Builder mBuilder;

    public Transporter(){
        OkHttpClient.Builder okHttp = new OkHttpClient.Builder() .connectTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = okHttp.build();
        mBuilder = new Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
    }

    public final <T>T prepare(Class<T>  cls,String url){
        Retrofit.Builder builder=mBuilder;
        Retrofit retrofit=null!=builder?builder.baseUrl(url).build():null;
        return null!=retrofit?retrofit.create(cls):null;
    }

    public final<T> boolean call(Observable<T> observable){
        if (null!=observable){
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Callback<>());
            return true;
        }
        return false;
    }

    protected final boolean toast(int textId){
        Context context=getContext();
        String text=null!=context?context.getString(textId):null;
        return null!=text&&null!=context&&toast(text);
    }

    protected final boolean toast(String text){
        Context context=getContext();
        if (null!=context&&null!=text){
            Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private static final class Callback <T>implements Observer<T>{
        @Override
        public void onSubscribe(Disposable d) {

        }

        @Override
        public void onNext(T t) {

        }

        @Override
        public void onComplete() {

        }

        @Override
        public void onError(Throwable e) {

        }

    }
}
