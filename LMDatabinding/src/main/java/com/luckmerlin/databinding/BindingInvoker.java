package com.luckmerlin.databinding;

import android.view.View;
import java.util.Collection;

final class BindingInvoker {

    void invoke(View view, IBinding... bindings){
        if (null==view||null==bindings||bindings.length<=0){
            return;
        }
        for (IBinding binding:bindings) {
            if (null==binding){
              continue;
            }else if (binding instanceof Bindings){
                Collection<IBinding> children=((Bindings)binding).getBindings();
                if (null!=children){
                    for (IBinding child:children) {
                        invoke(view,child);
                    }
                }
            }
            if (binding instanceof BindingObject){
                ((BindingObject) binding).onBind(view);
            }
        }
    }

}
