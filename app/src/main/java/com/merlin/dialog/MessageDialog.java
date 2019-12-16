package com.merlin.dialog;

import android.content.Context;
import android.widget.TextView;

import com.merlin.client.R;

public class MessageDialog extends Dialog {

    public MessageDialog(Context context){
        super(context);
        setContentView(R.layout.dlg_message_layout);
    }


    public boolean show(String title,String msg,String ...items){
        (findViewById(R.id.dlg_message_layout_msgTV,TextView.class)).setText(msg);
        super.show();
        return false;
    }
}
