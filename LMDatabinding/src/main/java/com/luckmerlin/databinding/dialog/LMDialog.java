package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class LMDialog extends Dialog {

    public LMDialog(Context context, Integer windowType) {
        super(context, windowType);
    }


    public final Dialog title(Object title){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setTitleText(title);
//        }
        return this;
    }

    public final Dialog message(Object msgTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setMessageText(msgTextId);
//        }
        return this;
    }

    public final Dialog left(Object leftTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setLeftText(leftTextId);
//        }
        return this;
    }

    public final Dialog center(Object centerTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setCenterText(centerTextId);
//        }
        return this;
    }

    public final Dialog right(Object rightTextId){
//        DialogLayoutBinding binding=mBinding;
//        if (null!=binding){
//            binding.setRightText(rightTextId);
//        }
        return this;
    }


    public final View getRootView() {
//        ViewDataBinding binding=mBinding;
//        return null!=binding?binding.getRoot():null;
        return null;
    }

}
