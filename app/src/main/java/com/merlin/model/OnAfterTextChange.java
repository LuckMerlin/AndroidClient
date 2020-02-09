package com.merlin.model;

import android.text.Editable;
import android.widget.EditText;

public interface OnAfterTextChange extends Callback {
     void onAfterChanged(EditText et, Editable e);
}
