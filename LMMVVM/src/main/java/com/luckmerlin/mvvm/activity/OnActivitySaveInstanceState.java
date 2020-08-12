package com.luckmerlin.mvvm.activity;

import android.app.Activity;
import android.os.Bundle;

public interface OnActivitySaveInstanceState {
    void onActivitySaveInstanceState(Activity activity, Bundle outState);
}
