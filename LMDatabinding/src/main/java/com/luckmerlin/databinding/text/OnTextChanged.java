package com.luckmerlin.databinding.text;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.IBinding;

public interface OnTextChanged  extends IBinding,OnEditTextChange, PublishMethods {
   void onTextChanged(CharSequence s, int start, int before, int count);
}
