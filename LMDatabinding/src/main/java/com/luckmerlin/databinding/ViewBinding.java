package com.luckmerlin.databinding;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.text.OnEditActionChange;
import com.luckmerlin.databinding.text.OnEditActionChangeListener;
import com.luckmerlin.databinding.text.OnEditTextChange;
import com.luckmerlin.databinding.text.OnEditTextChangeListener;
import com.luckmerlin.databinding.view.Image;
import com.luckmerlin.databinding.view.Tag;
import com.luckmerlin.databinding.view.Text;
import com.luckmerlin.databinding.view.Touch;

final class ViewBinding {

    boolean bind(View view, BindingObject ...bindings){
        if (null==view||null==bindings||bindings.length<=0){
            return false;
        }
        for (BindingObject binding:bindings) {
            if (binding instanceof BindingList){
                BindingList list=(BindingList)binding;
                if (null!=list&&list.size()>0){
                    for (BindingObject child:list) {
                        bind(view,child);
                    }
                }
                continue;
            }
            if (view instanceof TextView){
                TextView textView=(TextView)view;
                if (binding instanceof Text){
                    applyViewText(textView,(Text)binding);
                }
                if (binding instanceof OnEditActionChange) {
                    textView.setOnEditorActionListener(new OnEditActionChangeListener((OnEditActionChange)binding));
                }
                if (binding instanceof OnEditTextChange){
                    textView.addTextChangedListener(new OnEditTextChangeListener((OnEditTextChange) binding));
                }
            }
            if (binding instanceof Image){
                applyViewImage(view,(Image)binding);
            }
            if (binding instanceof Tag){
                applyViewTag(view,(Tag)binding);
            }
            if (binding instanceof Touch){
                applyViewTouch(view,(Touch)binding);
            }
        }
        return true;
    }

    private boolean applyViewTouch(View view,Touch touch){
        if (null==view){
            return false;
        }
        Object object=null!=touch?touch.getObject():null;
        if (null==object||(object instanceof Boolean&&!(Boolean)object)){
            return false;
        }
        if (object instanceof View.OnTouchListener){
            view.setOnTouchListener((View.OnTouchListener)object);
        }
        if (object instanceof View.OnClickListener){
            view.setOnClickListener((View.OnClickListener)object);
        }
        if (object instanceof View.OnLongClickListener){
            view.setOnLongClickListener((View.OnLongClickListener)object);
        }
        if (object instanceof Integer){
            Integer touchEvent=(Integer)object;
            switch (touchEvent){
                case Touch.CLICK:

                    break;
                case Touch.LONG_CLICK:

                    break;
            }
        }
        return true;
    }

    private boolean applyViewTag(View view,Tag tag){

        return false;
    }

    private boolean applyViewImage(View view,Image image){

        return false;
    }

    private boolean applyViewText(TextView view,Text text){

        return false;
    }
}
