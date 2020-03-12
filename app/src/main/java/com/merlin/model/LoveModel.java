package com.merlin.model;

import android.view.View;

import com.merlin.activity.LoveDetailActivity;
import com.merlin.adapter.LoveAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.bean.Love;
import com.merlin.client.R;
import com.merlin.server.Retrofit;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoveModel  extends Model implements OnTapClick,Label {
    private interface Api{
        @POST(Address.PREFIX_LOVE+"/get")
        @FormUrlEncoded
        Observable<Reply<ApiList<Love>>> getLoves(@Field(LABEL_NAME) String NAME);
    }

    private final LoveAdapter mAdapter=new LoveAdapter() {
        @Override
        protected Retrofit.Canceler onPageLoad(String arg, int page, OnApiFinish<Reply<SectionData<Love>>> finish) {
            return call(prepare(Api.class).getLoves(null),finish);
        }
    };

    public LoveModel(){
//        mAdapter.loadPage(null,"While model create.");
        Collection<Love> list=new ArrayList<>();
        list.add(new Love());
        list.add(new Love());
        list.add(new Love());
        list.add(new Love());
        mAdapter.setData(list);
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                return onSingleTap(view,resId,data);
        }
        return false;
    }

    private boolean onSingleTap(View view,int resId,Object data){
        switch (resId){
            case R.string.add:
                return startActivity(LoveDetailActivity.class);
        }
        return false;
    }

    public LoveAdapter getAdapter() {
        return mAdapter;
    }

}
