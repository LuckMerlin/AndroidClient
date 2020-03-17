package com.merlin.model;

import androidx.databinding.ObservableField;

import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.Love;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoveDetailModel extends Model {
      private final ObservableField<Love> mLove=new ObservableField<>();
    private interface Api {
        @POST(Address.PREFIX_LOVE + "/detail")
        @FormUrlEncoded
        Observable<Reply<Love>> getLovesDetail(@Field(Label.LABEL_ID) String id);

        @POST(Address.PREFIX_LOVE + "/detail")
        @FormUrlEncoded
        Observable<Reply> addLove(@Field(Label.LABEL_DATA) Love love);
    }

//    private final LoveAdapter mAdapter=new LoveAdapter() {
//        @Override
//        protected boolean onPageLoad(String arg, int page, OnApiFinish<Reply<SectionData<Love>>> finish) {
//            return true;
//        }
//    };
//
//    public LoveAdapter getAdapter() {
//        return mAdapter;
//    }

    public LoveDetailModel(){
//        post(()->{
//            mLove.set(new Love("是的发送到发","司法所大师傅"));
//        },4000);
    }

    public ObservableField<Love> getLove() {
        return mLove;
    }
}
