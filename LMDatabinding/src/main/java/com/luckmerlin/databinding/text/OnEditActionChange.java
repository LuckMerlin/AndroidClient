package com.luckmerlin.databinding.text;

import android.view.KeyEvent;
import android.widget.TextView;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.IBinding;

public interface OnEditActionChange extends IBinding, PublishMethods {
    boolean onEditActionChange(TextView v, int actionId, KeyEvent event);
}
