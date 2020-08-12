package com.luckmerlin.mvvm.service;

import android.content.Intent;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 15:35 2020/8/12
 * TODO
 */
public interface OnModelServiceResolve extends PublishMethods {
    List<Intent> onServiceResolved(List<Intent> list);
}
