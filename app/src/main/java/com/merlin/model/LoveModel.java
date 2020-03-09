package com.merlin.model;

import com.merlin.adapter.LoveAdapter;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.api.What;
import com.merlin.bean.Love;
import com.merlin.debug.Debug;

import java.util.ArrayList;
import java.util.List;

public class LoveModel  extends Model {

    private final LoveAdapter mAdapter=new LoveAdapter() {
        @Override
        protected boolean onPageLoad(String arg, int page, OnApiFinish<Reply<SectionData<Love>>> finish) {
            Reply<SectionData<Love>> reply=new Reply<>();
            reply.setSuccess(true);
            reply.setWhat(What.WHAT_SUCCEED);
            SectionData sectionData=new SectionData();
            sectionData.setFrom(0);
            List<Love> list=new ArrayList<>();
            list.add(new Love());
            sectionData.setData(list);
            reply.setData(sectionData);
            finish.onApiFinish(What.WHAT_SUCCEED,"",reply,null);
            Debug.D(getClass(),"AAAAAAAAAAAAA ");
            return true;
        }
    };

    public LoveAdapter getAdapter() {
        return mAdapter;
    }
}
