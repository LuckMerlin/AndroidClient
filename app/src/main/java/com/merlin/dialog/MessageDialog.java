package com.merlin.dialog;

import android.content.Context;
import android.widget.TextView;

import com.merlin.client.R;

public class MessageDialog extends Dialog_old {
    public MessageDialog(Context context){
        super(context);
        setContentView(R.layout.dlg_message_layout);
    }

    public boolean show(Object title,Object msg){
        (findViewById(R.id.dlg_message_layout_msgTV,TextView.class)).setText(getText(msg));
        super.show();
        return true;
    }
}
