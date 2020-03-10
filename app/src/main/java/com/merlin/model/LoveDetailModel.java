package com.merlin.model;

import androidx.databinding.ObservableField;

import com.merlin.adapter.LoveAdapter;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.bean.Love;

public class LoveDetailModel extends Model {
      private final ObservableField<Love> mLove=new ObservableField<>();
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
