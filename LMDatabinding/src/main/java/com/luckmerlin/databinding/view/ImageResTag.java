package com.luckmerlin.databinding.view;

public final class ImageResTag extends Image  implements ResTag{
    private Object mTag;

     ImageResTag(Object image,Object background,Object tag) {
        super(image,background);
         mTag=tag;
    }

    public static ImageResTag image(Object image,Object tag){
        return image(image,null,tag);
    }

    public static ImageResTag image(Object image,Object background,Object tag){
        return new ImageResTag(image,background,tag);
    }

    public Object getTag() {
        return mTag;
    }

    @Override
    public Integer getResTagId() {
        Object object=getSrc();
        return null!=object&&object instanceof Integer?((Integer)object):null;
    }
}
