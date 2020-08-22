package com.luckmerlin.databinding.text;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.BindingObject;

public interface OnTextChanged  extends BindingObject,OnEditTextChange, PublishMethods {
   void onTextChanged(CharSequence s, int start, int before, int count);
}
