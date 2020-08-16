package com.luckmerlin.databinding.view;
 class Tag extends Value {
    private Object mObject;

    public final Tag tag(Object object){
        mObject=object;
        return this;
    }

    public Object getObject() {
        return mObject;
    }

    @Override
    public Values values() {
        return null;
    }
}
