package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.content.Intent;

public interface OnActivityIntentChange {
   void onActivityIntentChanged(Activity activity, Intent intent);
}
