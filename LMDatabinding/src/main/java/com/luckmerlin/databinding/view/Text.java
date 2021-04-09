package com.luckmerlin.databinding.view;

import android.util.TypedValue;

import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.databinding.IBinding;

import java.util.ArrayList;
import java.util.List;

public class Text implements IBinding,PublishMethods, PublishFields {
    public final static int UNIT_DIP= TypedValue.COMPLEX_UNIT_DIP;
    public final static int UNIT_IN= TypedValue.COMPLEX_UNIT_IN;
    public final static int UNIT_MM= TypedValue.COMPLEX_UNIT_MM;
    public final static int UNIT_FRACTION= TypedValue.COMPLEX_UNIT_FRACTION;
    public final static int UNIT_FRACTION_PARENT= TypedValue.COMPLEX_UNIT_FRACTION_PARENT;
    public final static int UNIT_PT= TypedValue.COMPLEX_UNIT_PT;
    public final static int UNIT_PX= TypedValue.COMPLEX_UNIT_PX;
    public final static int UNIT_SP= TypedValue.COMPLEX_UNIT_SP;
    private List<Object> mObjects;
    private final Float[] mSize=new Float[2];
    private Object mColor;
    private Text mHint;

    Text(Object text,Object color){
        color(color);
        append(text);
    }

    public static Text text(Object text){
        return text(text,null);
    }

    public static Text text(Object text,Object color){
        return new Text(text,color);
    }

    public Text size(Float size) {
        mSize[1]=size;
        return this;
    }

    public Text sizeUnit(Integer unit){
        mSize[0]=null!=unit?(float)(unit):null;
        return this;
    }

    public Text color(Object color){
        mColor=color;
        return this;
    }

    public Text hint(Text text){
        mHint=text;
        return this;
    }

    public final Integer getSizeUnit() {
        return getSizeUnit(UNIT_PX);
    }

    public final Integer getSizeUnit(Integer def) {
        Float current=mSize[0];
        if (null==current){
            return def;
        }
        return (int)((float)current);
    }

    public final float getSize(float def) {
        Float current=mSize[1];
        return null==current?def:current;
    }

    public final Text append(Object text){
        if (null!=text){
            List<Object> objects=mObjects;
            objects=null!=objects?objects:(mObjects=new ArrayList<>(1));
            objects.add(text);
        }
        return this;
    }

    public final Text clean(){
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
