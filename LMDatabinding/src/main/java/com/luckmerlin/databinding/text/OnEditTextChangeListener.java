package com.luckmerlin.databinding.text;

import android.text.Editable;
import android.text.TextWatcher;

public final class OnEditTextChangeListener implements TextWatcher {
    private final OnEditTextChange mOnEditTextChange;

    public OnEditTextChangeListener(OnEditTextChange change){
        mOnEditTextChange=change;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        OnEditTextChange change=mOnEditTextChange;
        if (null!=change&&change instanceof OnTextChangeBefore){
            ((OnTextChangeBefore)change).onTextChangeBefore(s,start,count,after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        OnEditTextChange change=mOnEditTextChange;
        if (null!=change&&change instanceof OnTextChanged){
            ((OnTextChanged)change).onTextChanged(s,start,before,count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        OnEditTextChange change=mOnEditTextChange;
        if (null!=change&&change instanceof OnTextChangeAfter){
            ((OnTextChangeAfter)change).onTextChangeAfter(s);
        }
    }
}
