package com.merlin.dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.merlin.client.R;

public final class SearchDialog extends Dialog_old {
    private OnSearchInputChange mInputChangeListener;
    private EditText mInput;
    public interface OnSearchInputChange{
        void onSearchInputChanged(String input);
    }

    public SearchDialog(Context context){
        super(context);
        setContentView(R.layout.dialog_search);
        mInput=findViewById(R.id.dialog_search_inputET,EditText.class);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                OnSearchInputChange listener=mInputChangeListener;
                if (null!=listener){
                    listener.onSearchInputChanged(s.toString());
                }
            }
        });
    }

    public SearchDialog setOnSearchInputChange(OnSearchInputChange listener){
        mInputChangeListener=listener;
        return this;
    }

}
