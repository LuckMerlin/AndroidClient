package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.LMBinding;
import com.luckmerlin.databinding.R;
import com.luckmerlin.databinding.ResourceCreator;
import com.luckmerlin.databinding.databinding.LmdialogBinding;

import java.util.Map;
import java.util.WeakHashMap;

public class LMDialog extends Dialog {

    public LMDialog(android.app.Dialog dialog){
        super(dialog);
    }

    public LMDialog(Context context){
        super(context,null);
    }

    public LMDialog(Context context, Integer windowType){
         super(context,windowType);
    }

    public Dialog title(Object title){
        LmdialogBinding layout=getDialogLayout();
        if (null!=layout){
            layout.setTitle(title);
        }
        return this;
    }

    public Dialog message(Object message){
        LmdialogBinding layout=getDialogLayout();
        if (null!=layout){
            layout.setMessage(message);
        }
        return this;
    }

    public Dialog left(Object left){
        LmdialogBinding layout=getDialogLayout();
        if (null!=layout){
            layout.setLeft(left);
        }
        return this;
    }

    public Dialog center(Object center){
        LmdialogBinding layout=getDialogLayout();
        if (null!=layout){
            layout.setCenter(center);
        }
        return this;
    }

    public Dialog right(Object right){
        LmdialogBinding layout=getDialogLayout();
        if (null!=layout){
            layout.setRight(right);
        }
        return this;
    }

    private LmdialogBinding getDialogLayout(){
        View root=getRoot();
        ViewDataBinding binding=null!=root?DataBindingUtil.getBinding(root):null;
        if (null!=binding&&binding instanceof LmdialogBinding){
            return (LmdialogBinding)binding;
        }
        binding=DataBindingUtil.inflate(inflater(),R.layout.lmdialog,null,false);
        if (null!=binding&&binding instanceof LmdialogBinding){
            setContentView(binding.getRoot());
            return (LmdialogBinding)binding;
        }
        return null;
    }

}
