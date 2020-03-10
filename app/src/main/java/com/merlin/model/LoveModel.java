package com.merlin.model;

import android.view.View;

import com.merlin.activity.LoveDetailActivity;
import com.merlin.adapter.LoveAdapter;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.api.What;
import com.merlin.bean.Love;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.List;

public class LoveModel  extends Model implements OnTapClick {

    private final LoveAdapter mAdapter=new LoveAdapter() {
        @Override
        protected boolean onPageLoad(String arg, int page, OnApiFinish<Reply<SectionData<Love>>> finish) {
            return true;
        }
    };

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
