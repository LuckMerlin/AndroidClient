package com.luckmerlin.databinding.view;

import java.util.List;

public final class TextResTag extends Text implements ResTag{
    private Object mTag;

     TextResTag(Object text,Object color,Object tag) {
        super(text,color);
        mTag=tag;
    }

    public static TextResTag text(Object textResId,Object tag){
        return text(textResId,null,tag);
    }

    public static TextResTag text(Object textResId, Object color,Object tag){
        return new TextResTag(textResId,color,tag);
    }

    public Object getTag() {
        return mTag;
    }

    @Override
    public Integer getResTagId() {
        List<Object> objects=getObjects();
        Object object=null!=objects&&objects.size()>0?objects.get(0):null;
        return null!=object&&object instanceof Integer?((Integer)object):null;
    }
}
