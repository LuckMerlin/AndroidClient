package com.luckmerlin.databinding.text;

import android.text.Editable;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public interface OnTextChangeAfter extends BindingObject,OnEditTextChange, PublishMethods {
    void onTextChangeAfter(Editable editor);
}
