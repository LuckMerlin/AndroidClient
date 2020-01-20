package com.merlin.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.merlin.client.R;
import com.merlin.util.Text;

public class TitleDialog extends Dialog {

    public TitleDialog(Context context){
        super(context);
        setContentView(R.layout.dlg_layout);
    }

    public void setTitle(Integer titleId){
        if (null!=titleId){
            String text=Text.text(getContext(),null,titleId);
            TextView tv=findViewById(R.id.dlg_titleTV, TextView.class);
            if (null!=tv){
                tv.setVisibility(null!=text?View.VISIBLE:View.GONE);
                tv.setText(null!=text?text:"");
            }
        }
    }

    public boolean show(Integer titleId){
        if (null!=titleId){
            if (!super.isShowing()&&null!=super.show()&&super.isShowing()){
                 setTitle(titleId);
                 return true;
            }
        }
        return false;
    }

}
