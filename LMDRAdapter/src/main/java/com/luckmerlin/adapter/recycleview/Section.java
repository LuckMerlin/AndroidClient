package com.luckmerlin.adapter.recycleview;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.List;

public interface Section<A,D> extends PublishMethods {

    public List<D> getData();

    public A getArg();

    public long getFrom();

    public long getTotal();
}
