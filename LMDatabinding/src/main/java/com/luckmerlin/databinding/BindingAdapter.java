package com.luckmerlin.databinding;

import android.view.View;
import com.luckmerlin.databinding.view.Text;
import com.luckmerlin.databinding.view.ViewValuer;

public class BindingAdapter {

    @androidx.databinding.BindingAdapter("viewValue")
    public static void setViewValue(View view, Text viewText) {
        if (null!=view&&null!=viewText){
            new ViewValuer().set(view,viewText);
        }
    }

}
