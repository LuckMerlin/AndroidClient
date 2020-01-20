package com.merlin.dialog;

import android.content.Context;
import android.view.View;

import com.merlin.client.R;

public class SingleInputDialog extends TitleDialog  {

    public SingleInputDialog(Context context){
        super(context);
    }


    @Override
    protected Integer onResolveContentLayout() {
        return R.layout.edittext;
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

    @Override
    public void onViewClick(View v, int id) {
        switch (id){
            case R.id.dlg_cancelTV:
                dismiss();
                break;
            case R.id.dlg_sureTV:
                 dismiss();
                break;
        }
    }
}
