package com.luckmerlin.databinding.view;

public final class Values {
    private Text mText;
    private Image mImage;
    private Event mEvent;
    private Tag mTag;

    public static final Values val(){
        return new Values();
    }

    public static final Event event(){
        return val().eve(null,null);
    }

    public static final Tag tag(Object obj){
        return val().tagObj(obj);
    }

    public static final Text text(Integer color,Object ...stringObjects){
        return val().txt(color,stringObjects);
    }

    public static final Image image(Object obj,Object background){
        return val().img(obj,background);
    }

    public Event eve(Integer eventId,Object obj){
        Event event=mEvent;
        return (null!=event?event:(mEvent=new Event(){
            @Override
            public Values values() {
                return Values.this;
            }
        })).tag(eventId,obj);
    }

    public Tag tagObj(Object obj){
        Tag tag=mTag;
        return (null!=tag?tag:(mTag=new Tag(){
            @Override
            public Values values() {
                return Values.this;
            }
        })).tag(obj);
    }

    public Text txt(Integer color,Object ...stringObjects){
        Text text=mText;
        return (null!=text?text:(mText=new Text(){
            @Override
            public Values values() {
                return Values.this;
            }
        })).color(color).append(stringObjects);
    }

    public Image img(Object obj,Object background){
        Image image=mImage;
        return (null!=image?image.background(background):(mImage=new Image(){
            @Override
            public Values values() {
                return Values.this;
            }
        })).src(obj).background(background);
    }

}
