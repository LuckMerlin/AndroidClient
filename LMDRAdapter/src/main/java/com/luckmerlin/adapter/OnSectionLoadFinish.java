package com.luckmerlin.adapter;

import com.luckmerlin.adapter.recycleview.Section;
import com.luckmerlin.core.proguard.PublishMethods;

public interface OnSectionLoadFinish<A,D> extends PublishMethods {
    void onSectionLoadFinish(boolean succeed, String note, Section<A,D> section);
}
