package com.merlin.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.client.R;
import com.merlin.client.databinding.DlgLayoutBinding;
import com.merlin.util.Text;

public class TitleDialog extends Dialog {

    protected Integer onResolveContentLayout(){
        //Do nothing
        return null;
    }

    public TitleDialog(Context context){
        super(context);
        LayoutInflater inflater=LayoutInflater.from(context);
        DlgLayoutBinding binding=DataBindingUtil.inflate(inflater,R.layout.dlg_layout,null,false);
        View root=null!=binding?binding.getRoot():null;
        if (null!=root){
            binding.setDialog(this);
            setContentView(root);
            ViewGroup vg=findViewById(R.id.dlg_contentFL,ViewGroup.class);
            Integer layoutId=null!=vg?onResolveContentLayout():null;
            ViewDataBinding childBinding=null!=layoutId?DataBindingUtil.inflate(inflater,layoutId,vg,true):null;
            if (null!=childBinding){
                childBinding.setVariable(com.merlin.client.BR.dialog,this);
            }
        }
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

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.dlg_sureTV:

                break;
            case R.id.dlg_cancelTV:
                dismiss();
                break;
        }
    }
}
