package com.luckmerlin.databinding.text;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public interface OnTextChangeBefore extends BindingObject,OnEditTextChange, PublishMethods {
    void onTextChangeBefore(CharSequence s, int start, int count, int after);
}
