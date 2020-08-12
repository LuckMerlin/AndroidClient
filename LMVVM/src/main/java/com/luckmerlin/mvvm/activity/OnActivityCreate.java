package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.os.Bundle;

import com.luckmerlin.core.proguard.PublishMethods;

public interface OnActivityCreate extends PublishMethods {
   void onActivityCreated(Activity activity, Bundle savedInstanceState);
}
