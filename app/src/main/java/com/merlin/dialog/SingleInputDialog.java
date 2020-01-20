package com.merlin.dialog;

import android.content.Context;

public class SingleInputDialog extends TitleDialog {

    public SingleInputDialog(Context context){
        super(context);
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
