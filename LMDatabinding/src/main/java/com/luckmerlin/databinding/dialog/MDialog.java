package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public final class MDialog extends Dialog {

    public MDialog(android.app.Dialog dialog){
        super(dialog);
    }

    public MDialog(Context context, Integer windowType){
         super(context,windowType);
    }

    @Override
    protected View onSetContentView(View view) {
        if (null!=view&&null==view.getParent()&&!(view instanceof DialogLayout)){
            DialogLayout layout=getDialogLayout();
            if ((null!=layout?layout:new DialogLayout(view.getContext())).content(view)){
                return layout;
            }
        }
        return null;
    }

    public Dialog title(Object title){
        DialogLayout layout=getDialogLayout();
        if (null!=layout){
            layout.title(title);
        }
        return this;
    }

    public Dialog message(Object message){
        DialogLayout layout=getDialogLayout();
        if (null!=layout){
            layout.message(message);
        }
        return this;
    }

    public Dialog left(Object left){
        DialogLayout layout=getDialogLayout();
        if (null!=layout){
            layout.left(left);
        }
        return this;
    }

    public Dialog center(Object center){
        DialogLayout layout=getDialogLayout();
        if (null!=layout){
            layout.center(center);
        }
        return this;
    }

    public Dialog right(Object right){
        DialogLayout layout=getDialogLayout();
        if (null!=layout){
            layout.right(right);
        }
        return this;
    }

    private DialogLayout getDialogLayout(){
        View root=getRoot();
        return null!=root&&root instanceof DialogLayout?((DialogLayout)root):null;
    }

    private static final class DialogLayout extends LinearLayout{

        public DialogLayout(Context context) {
            super(context);
        }

        public boolean center(Object object){
            return false;
        }

        public boolean left(Object object){
            return false;
        }

        public boolean right(Object object){
            return false;
        }

        public boolean title(Object object){
            return false;
        }

        public boolean message(Object object){
            return false;
        }

        public boolean content(Object object){
            return false;
        }
    }

}
