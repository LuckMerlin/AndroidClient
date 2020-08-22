package com.luckmerlin.databinding.text;

import android.view.KeyEvent;
import android.widget.TextView;

public final class OnEditActionChangeListener implements TextView.OnEditorActionListener {
    private OnEditActionChange mCallback;

    public OnEditActionChangeListener(OnEditActionChange callback){
         mCallback=callback;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        OnEditActionChange callback=mCallback;
        return null!=callback&&callback.onEditActionChange(v,actionId,event);
    }

}
