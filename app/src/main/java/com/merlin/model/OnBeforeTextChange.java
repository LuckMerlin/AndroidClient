package com.merlin.model;

import android.widget.EditText;

public interface OnBeforeTextChange extends Callback {
     void beforeTextChanged(EditText et,CharSequence s, int start, int count, int after);
}
