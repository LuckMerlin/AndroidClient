package com.luckmerlin.databinding.view;

/**
 * @deprecated
 */
class Event extends Value{
    public final static int CLICK_EVENT=0x00000001;
    public final static int LONG_CLICK_EVENT=0x00000010;
    private Object mTag;
    private Integer mEventId;

    Event(){

    }

    public Event tag(Integer event,Object obj){
        if (null!=event){
            mEventId=event;
            mTag=obj;
        }
        return this;
    }

    public Event clickTag(Object obj){
        tag(CLICK_EVENT,obj);
        return this;
    }

    public Event longClickTag(Object obj){
        tag(LONG_CLICK_EVENT,obj);
        return this;
    }

    public Event multiClickTag(Object obj){
        tag(LONG_CLICK_EVENT|CLICK_EVENT,obj);
        return this;
    }

    public final boolean isLongClickEnable(){
        Integer eventId=mEventId;
        return null!=eventId&&(eventId&LONG_CLICK_EVENT)>0;
    }

    public final boolean isClickEnable(){
        Integer eventId=mEventId;
        return null!=eventId&&(eventId&CLICK_EVENT)>0;
    }

    public Object getTag() {
        return getTag(null);
    }

    public Object getTag(Integer eventId) {
        return mTag;
    }

    @Override
    public  Values values() {
        return null;
    }
}
