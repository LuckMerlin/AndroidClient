package com.luckmerlin.test;

import android.view.ViewGroup;

import com.luckmerlin.adapter.recycleview.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.adapter.recycleview.SectionListAdapter;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.Canceler;

import java.util.ArrayList;
import java.util.List;

public class DDD extends SectionListAdapter<String> {

    @Override
    protected Integer onResolveDataLayoutId(ViewGroup parent) {
        return R.layout.test_layout;
    }

    @Override
    protected Canceler onSectionLoad(SectionRequest request, OnSectionLoadFinish callback) {
        if (null!=callback){
            List<String> list=new ArrayList<>();
            list.add("dd");
            list.add("dd1");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd2");
            list.add("dd3]");
            Section section=new Section("eee",0,list);
            callback.onSectionLoadFinish(true,"",section);
        }
        return new Canceler() {
            @Override
            public boolean cancel(boolean cancel, String debug) {
                return false;
            }
        };
    }
}
