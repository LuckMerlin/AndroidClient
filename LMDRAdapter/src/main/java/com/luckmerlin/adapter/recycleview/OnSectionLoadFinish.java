package com.luckmerlin.adapter.recycleview;

public interface OnSectionLoadFinish<D> {
    void onSectionLoadFinish(boolean succeed, String note, Section<D> section);
}
