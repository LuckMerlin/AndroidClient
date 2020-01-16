package com.merlin.model;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.api.Address;
import com.merlin.bean.User;
import com.merlin.media.Sheet;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ActivityMediaSheetModel extends DataListModel<Sheet> implements BaseAdapter.OnItemClickListener<Sheet> {
    private final String mCloudAccount;

    private interface ApiUrl{
        //get请求
        @GET(Address.PREFIX_USER+"login")
        Observable<User> login();
    }


    public class LogInterceptor implements Interceptor {
        private String TAG = "okhttp";

        @Override
        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request()
//                .newBuilder()
//                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
//                .addHeader("Accept-Encoding", "gzip, deflate")
//                .addHeader("Connection", "keep-alive")
//                .addHeader("Accept", "*/*")
//                .addHeader("Cookie", "add cookies here")
//                .build()
                    ;

            Log.e(TAG,"request:" + request.toString());
            long t1 = System.nanoTime();
            okhttp3.Response response = chain.proceed(chain.request());
            long t2 = System.nanoTime();
            Log.e(TAG,String.format(Locale.getDefault(), "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Log.e(TAG, "response body:" + content);
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
//                .header("Authorization", Your.sToken)
                    .build();
        }
    }


    public ActivityMediaSheetModel(Context context){
        super(context,new MediaSheetAdapter(),new GridLayoutManager(context,3,
//                GridLayoutManager.VERTICAL,false),new GridSpacingItemDecoration(3,10,true));
                GridLayoutManager.VERTICAL,false));
        mCloudAccount="linqiang";
        post(()->{
            test();
        },2000);
    }


    private void test(){
        String baseUrl="http://172.16.20.215:2008";
        int DEFAULT_TIMEOUT =10;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new LogInterceptor())//添加打印拦截器
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(baseUrl).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiUrl api = retrofit.create(ApiUrl.class);
        api.login().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(getLifecycleProvider().bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new ApiCallback() {
                    @Override
                    public void onSuccess(Object model) {

                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        toast("结束  ");
                    }
                });
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, Sheet data) {

    }
    //    @Override
//    protected void onViewAttached(View root) {
//        super.onViewAttached(root);
//        JSONObject object=new JSONObject();
//        putIfNotNull(object,TAG_PAGE,0);
//        putIfNotNull(object,TAG_LIMIT,10);
//        request(mCloudAccount,"/sheet",object,new OnObjectRequestFinish<List<Sheet>>(){
//            @Override
//            public void onObjectRequested(boolean succeed, int what, String note, Frame frame, List<Sheet> data) {
//                Debug.D(getClass(),"@@@@@@@@@@@ "+data);
//                getAdapter().setData(data);
//            }
//        });
//    }
//
//    @Override
//    public void onItemClick(View view, int sourceId, int position, Sheet data) {
//        if (null!=data){
//            data.setAccount(mCloudAccount);
//            startActivity(MediaSheetDetailActivity.class,data);
//            finishAllActivity(MediaSheetActivity.class);
//        }
//    }

}
