package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.Array2List;
import com.luckmerlin.databinding.BindingObject;

import java.util.ArrayList;
import java.util.List;

public class Text implements BindingObject,PublishMethods {
    private List<Object> mObjects;
    private final Float[] mSize=new Float[2];
    private Object mColor;
    private Text mHint;

    Text(Object color,Object text){
        mColor=color;
        append(text);
    }

    public static Text text(Object text){
        return text(null,text);
    }

    public static Text text(Object text,Object color){
        return new Text(color,text);
    }

    public Text color(Object color){
        mColor=color;
        return this;
    }

    public Text hint(Object hintColor,Object text){
        return hint(new Text(hintColor,text));
    }

    public Text hint(Text text){
        mHint=text;
        return this;
    }

    public final Integer getSizeUnit(Integer def) {
        Float current=mSize[0];
        return null==current?def:(int)((float)current);
    }

    public final float getSize(float def) {
        Float current=mSize[1];
        return null==current?def:current;
    }

    public Text append(Object text){
        if (null!=text){
            List<Object> objects=mObjects;
            objects=null!=objects?objects:(mObjects=new ArrayList<>(1));
            objects.add(text);
        }
        return this;
    }

    public Text clean(){
        List<Object> objects=mObjects;
        mObjects=null;
        mHint=null;
        mColor=null;
        if (null!=objects){
            objects.clear();
        }
        return this;
    }

    public List<Object> getObjects() {
        return mObjects;
    }

    public Text getHint() {
        return mHint;
    }

    public Object getColor() {
        return mColor;
    }
}
