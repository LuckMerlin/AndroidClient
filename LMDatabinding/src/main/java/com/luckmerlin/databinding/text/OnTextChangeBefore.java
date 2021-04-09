package com.luckmerlin.databinding.text;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.IBinding;

public interface OnTextChangeBefore extends IBinding,OnEditTextChange, PublishMethods {
    void onTextChangeBefore(CharSequence s, int start, int count, int after);
}
