package com.luckmerlin.databinding.view;

import com.luckmerlin.core.proguard.PublishMethods;
import java.util.List;

public class Text extends Value implements PublishMethods {
    private List<Object> mObjects;
    private Integer mColor;
    private Text mHint;

    Text(Object ...stringObjects){
        this(null,stringObjects);
    }

    Text(Integer color,Object ...stringObjects){
        mColor=color;
        mObjects=new Array2List().toList(null,stringObjects);
    }

    public static Text text(Object ...stringObjects){
        return text(null,stringObjects);
    }

    public static Text text(Integer color,Object ...stringObjects){
        return new Text(color,stringObjects);
    }

    public Text color(Integer color){
        mColor=color;
        return this;
    }

    public Text hint(Integer hintColor,Object... hints){
        return hint(new Text(hintColor,hints));
    }

    public Text hint(Text text){
        mHint=text;
        return this;
    }

    public Text append(Object ...stringObjects){
        mObjects=null!=stringObjects&&stringObjects.length>0?new Array2List().toList(mObjects,stringObjects):null;
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

    public Integer getColor() {
        return mColor;
    }

    @Override
    public Values values() {
        return null;
    }
}
