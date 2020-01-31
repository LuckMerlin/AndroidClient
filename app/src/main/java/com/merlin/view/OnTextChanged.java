package com.merlin.view;

import android.widget.TextView;

public interface OnTextChanged {
    int TEXT_CHANGING=2001;
    int TEXT_CHANGED=2002;
    int TEXT_BEFORE=2003;
    void onTextChanged(TextView tv,int state, CharSequence text);
}
