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
import com.merlin.debug.Debug;
import com.merlin.media.Sheet;
import com.merlin.retrofit.Retrofit;
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
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class ActivityMediaSheetModel extends DataListModel<Sheet> implements BaseAdapter.OnItemClickListener<Sheet> {
    private final String mCloudAccount;

    private interface ApiUrl{
        @GET(Address.PREFIX_MEDIA+"sheets")
        Observable<User> login();
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
        Debug.D(getClass(),"############# ");
        call(ApiUrl.class,new Retrofit.OnApiFinish(){
            @Override
            public void onApiFinish(int what, String note, Object data) {
                Debug.D(getClass(),"$$$$$$$$$ 结束  "+what+" "+note+" "+data);
            }
        }).login();
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
