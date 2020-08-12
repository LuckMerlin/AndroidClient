package com.luckmerlin.mvvm.service;

import android.content.Intent;

import java.util.List;

/**
 * Create LuckMerlin
 * Date 15:35 2020/8/12
 * TODO
 */
public interface OnModelServiceResolve {
    List<Intent> onServiceResolved(List<Intent> list);
}
