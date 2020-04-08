package com.merlin.dialog;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.merlin.client.R;

/**
 * @deprecated
 */
public class SingleInputDialog extends TitleDialog  {
    private Callback mCallback;

    public interface OnSingleInputCommit extends Callback{
        void onSingleInputCommit(SingleInputDialog dlg,String text);
    }

    public SingleInputDialog(Context context){
        super(context);
    }

    @Override
    protected Integer onResolveContentLayout() {
        return R.layout.dlg_single_input;
    }

    public boolean show(Integer titleId, OnSingleInputCommit click){
        if (null!=titleId){
            if (!super.isShowing()&&null!=super.show()&&super.isShowing()){
                 mCallback=click;
                 setTitle(titleId);
                 return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.dlg_sureTV:
                Callback callback=mCallback;
                if (null!=callback&&callback instanceof OnSingleInputCommit){
                    EditText editText=this.findViewById(R.id.dlg_single_inputET, EditText.class);
                    String text=null!=editText?editText.getText().toString():null;
                    ((OnSingleInputCommit)callback).onSingleInputCommit(SingleInputDialog.this,text);
                }
                break;
        }
//        Callback click=mClick;
//        if (null!=click&&null!=v){
//            click.onViewClick(v,v.getId());
//        }
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        mCallback=null;
    }

}
