package com.luckmerlin.databinding.text;

import android.text.Editable;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.IBinding;

public interface OnTextChangeAfter extends IBinding,OnEditTextChange, PublishMethods {
    void onTextChangeAfter(Editable editor);
}
