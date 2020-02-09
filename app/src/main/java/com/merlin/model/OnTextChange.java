package com.merlin.model;

import android.widget.EditText;

public interface OnTextChange extends Callback {
     void onTextChanged(EditText et, CharSequence s, int start, int before, int count);
}
