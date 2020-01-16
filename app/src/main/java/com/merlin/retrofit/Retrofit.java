package com.merlin.retrofit;

import android.app.Activity;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
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

    public interface Callback{

    }

    public Retrofit(String url){
        mUrl=null!=url&&url.length()>=0?url:"http://172.16.20.215:2008";
    }

    public final <T> T prepare(Class<T> cls){
        return prepare(cls,null);
    }

    public final <T> T prepare(Class<T> cls,String url){
        final String uri=url;
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

    public final <M> boolean subscribe(Observable observable,Callback ...callbacks){
        if (null==observable){
            onFinish(WHAT_ARGS_INVALID,"Observable is Null.",null,callbacks);
            return false;
        }
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        WeakReference<LifecycleProvider> reference=mLifecycleProvider;
        LifecycleProvider provider=null!=reference?reference.get():null;
        if (null!=provider&&provider instanceof Activity){
            observable.compose(provider.bindUntilEvent(ActivityEvent.DESTROY));
        }
        observable.subscribe(new ApiCallback<M>(){

        });
        return true;
    }


    private class ApiCallback<M> extends DisposableObserver<M> {

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            if (e instanceof HttpException) {
                HttpException httpException = (HttpException) e;
                //httpException.response().errorBody().string()
                int code = httpException.code();
                String msg = httpException.getMessage();
                int what=-1;
                if (code == 504) {
                    what=;
                } else if (code == 502) {
                    msg = "服务器异常，请稍后再试";
                } else if (code == 408) {
                    msg = "请求超时";
                } else if (code == 403) {
                    msg = "没有权限访问";
                } else if (code == 401) {
                }
                onFailure(msg);
            } else {
                onFailure(e.getMessage());
            }
            onFinish();
        }

        @Override
        public void onNext(M model) {
            Class modelClass = model.getClass();
            Field field = null;
            try {
                field = modelClass.getDeclaredField("resultHint");
                field.setAccessible(true);
                if (field.get(model) != null && "unLogin".equals(field.get(model).toString())) {
//                Intent intent = MyApplication.getInstance().getPackageManager().getLaunchIntentForPackage("com.tgdz.gkpttj");
//                MyApplication.getInstance().startActivity(intent);
//                AppManager.getAppManager().finishAllActivity();
//                SPUtil.clear("class com.tgdz.gkpttj.entity.SysUser");
//                SPUtil.clear("hasloggedin");
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gkpttj");
//                FileUtil.deleteAllFilesOfDir(file);
                    return;
                } else if (field.get(model) != null && "Token失效".equals(field.get(model).toString())) {
//                Intent intent = MyApplication.getInstance().getPackageManager().getLaunchIntentForPackage("com.tgdz.gkpttj");
//                MyApplication.getInstance().startActivity(intent);
//                AppManager.getAppManager().finishAllActivity();
//                SPUtil.clear("class com.tgdz.gkpttj.entity.SysUser");
//                SPUtil.clear("hasloggedin");
//                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/gkpttj");
//                FileUtil.deleteAllFilesOfDir(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            onSuccess(model);

        }

        @Override
        public void onComplete() {
            onFinish();
        }


    }

    private boolean onFinish(int what,String note,Object data,Callback ...callbacks){

        return false;
    }

}
