package com.merlin.transport;

import com.merlin.api.Reply;
import com.merlin.debug.Debug;

import java.util.List;

public abstract class ConveyGroup<T extends Convey> extends Convey {
    private List<T> mChildren;
    private T mConveying;

    public ConveyGroup(String name){
        super(name);
    }

    public boolean remove(T convey,String debug){
        List<T> children=null!=convey?mChildren:null;
        if (null!=children&&children.remove(convey)){
            Debug.D(getClass(),"Remove convey child "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

    @Override
    protected Reply onStart(Finish finish, String debug) {
        return next(null,debug);
    }

    public final int childCount(){
        List<T> children=mChildren;
        return null!=children?children.size():-1;
    }

    public final T findChild(Object obj){
        List<T> children=null!=obj?mChildren:null;
        int index=null!=children&&children.size()>0?children.indexOf(obj):-1;
        return index>=0?children.get(index):null;
    }

    public final boolean isExistChild(Object data){
        return null!=data&&null!=findChild(data);
    }

    private synchronized Reply next(T convey,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't convey while arg NULL "+(null!=debug?debug:"."));
            return new Reply(false,WHAT_ARGS_INVALID,"Convey in NULL.",null);
        }
        T conveying=mConveying;
        if (null!=conveying){
            Debug.W(getClass(),"Can't convey while exist conveying child "+(null!=debug?debug:"."));
            return new Reply(false,WHAT_EXIST,"Exist conveying child.",conveying);
        }
        final Finish innerFinish= (innerReply)->{
            Debug.D(getClass(),"Child 结束 "+innerReply);
            Convey currentConveying=mConveying;
            if (null!=currentConveying&&convey==currentConveying){
                mConveying=null;
            }
            indexNext(convey,debug);
        };
        notifyProgress();
        mConveying=convey;
        final Reply startReply= convey.onStart(innerFinish,debug);
        if (null!=startReply&&!startReply.isSuccess()){
            Convey currentConveying=mConveying;
            if (null!=currentConveying&&convey==currentConveying){
                mConveying=null;
            }
            innerFinish.onFinish(startReply);
        }
        return startReply;
    }

    public final Convey indexNext(T convey,String debug){
        List<T> children=mChildren;
        int length=null!=children?children.size():-1;
        if (length>0){
            int index=(null!=convey?children.indexOf(convey):-1)+1;
            return index<0||index>=length?null:children.get(index);
        }
        return null;
    }

    public boolean addChild(T convey,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't add NULL as child convey "+(null!=debug?debug:"."));
            return false;
        }
        if (isExistChild(convey)){
            Debug.W(getClass(),"Can't add already exist child convey "+(null!=debug?debug:"."));
            return false;
        }
        return false;
    }

}
