package com.merlin.conveyor;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.util.ArrayList;
import java.util.List;

public class _ConveyGroup<T extends _Convey> extends _Convey {
    private final List<T> mChildren;
    private T mConveying;

    public _ConveyGroup(String name){
        this(0,name);
    }

    public _ConveyGroup(int initialCapacity, String name){
        super(name);
        mChildren=new ArrayList<>(initialCapacity<=0?1:initialCapacity);
    }

    public final boolean remove(T convey,String debug){
        List<T> children=null!=convey?mChildren:null;
        if (null!=children&&children.remove(convey)){
            Debug.D(getClass(),"Remove convey child "+(null!=debug?debug:"."));
            return true;
        }
        return false;
    }

//    @Override
//    protected Reply onPrepare(Retrofit retrofit,String debug) {
//        //Do nothing
//        return null;
//    }

//    @Override
//    protected final Reply onStart(Retrofit retrofit,Finisher finish, String debug) {
//        T conveying=mConveying;
//        if (null!=conveying){
//            Debug.W(getClass(),"Can't start convey group while exist conveying child."+conveying);
//            return new Reply(false,WHAT_EXIST,"Exist conveying convey.",conveying);
//        }
//        final T convey=indexNext(null,"While start convey "+(null!=debug?debug:"."));
//        if (null==convey){
//            Debug.W(getClass(),"Can't start convey group while next index NULL.");
//            return new Reply(false,WHAT_EMPTY,"Not index first convey.",null);
//        }
//        return startChild(retrofit,finish,convey,debug);
//    }

    @Override
    protected final Boolean onCancel(Retrofit retrofit,boolean cancel, String debug) {
        T conveying=mConveying;
        return null!=conveying&&conveying.cancel(retrofit,cancel,debug);
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

    private synchronized Reply startChild(Retrofit retrofit,Finisher finisher,T convey,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't convey while arg NULL "+(null!=debug?debug:"."));
            return new Reply(false,WHAT_ARGS_INVALID,"Convey in NULL.",null);
        }
        T conveying=mConveying;
        if (null!=conveying){
            Debug.W(getClass(),"Can't convey while exist conveying child "+(null!=debug?debug:"."));
            return new Reply(false,WHAT_EXIST,"Exist conveying child.",conveying);
        }
        final Finisher innerFinish= new Finisher() {
            @Override
            public void onFinish(Reply innerReply) {
                Debug.D(getClass(),"A Child 结束 "+innerReply+" "+convey.getName());
                _Convey currentConveying=mConveying;
                if (null!=currentConveying&&convey==currentConveying){
                    mConveying=null;
                }
                if (isCancel()){
                    Debug.D(getClass(),"Canceled convey "+ _ConveyGroup.this);
                }else if(null==innerReply||innerReply.getWhat()!=WHAT_NONE_NETWORK){//
                    T next= getFirstUnReplyChild();
                    if (null!=next) {
                        startChild(retrofit,finisher,next, "After one child finish." + convey.getName());
                    }else if (null!=finisher){
                        finisher.onFinish(new Reply(true,WHAT_SUCCEED,"Group finish.",null));
                    }
                }else{
                    Debug.D(getClass(),"Give up to convey next child "+ _ConveyGroup.this);
                }
            }

            @Override
            public void onProgress(long conveyed, long total, float speed, _Convey convey) {
                if (null!=finisher){
                    finisher.onProgress(conveyed,total,speed,convey);
                }
            }
        };
        mConveying=convey;
        Debug.D(getClass(),"Start child convey of group "+getName()+" "+(null!=debug?debug:"."));
//        final Reply startReply= convey.start(retrofit,innerFinish,getStatusChange(),debug);
//        if (null!=startReply&&!startReply.isSuccess()){
//            Convey currentConveying=mConveying;
//            if (null!=currentConveying&&convey==currentConveying){
//                mConveying=null;
//            }
//            innerFinish.onFinish(startReply);
//        }
//        return startReply;
        return null;
    }

    public final T indexNext(T convey,String debug){
        if (!isCancel()){
            List<T> children=mChildren;
            int length=null!=children?children.size():-1;
            if (length>0){
                int index=null!=convey?children.indexOf(convey)+1:0;
                return index<0||index>=length?null:children.get(index);
            }
        }
        return null;
    }

    public final boolean addChild(T convey,String debug){
        if (null==convey){
            Debug.W(getClass(),"Can't add NULL as child convey "+(null!=debug?debug:"."));
            return false;
        }
        if (isExistChild(convey)){
            Debug.W(getClass(),"Can't add already exist child convey "+(null!=debug?debug:"."));
            return false;
        }
        List<T> children=mChildren;
        if (null==convey){
            Debug.W(getClass(),"Can't add child convey into NULL list "+(null!=debug?debug:"."));
            return false;
        }
        synchronized (children){
            if (!children.contains(convey)&&children.add(convey)){
                return true;
            }
        }
        return false;
    }

    public final int getRepliedChildrenSize(){
        List<T> list=getRepliedChildren();
        return null!=list?list.size():0;
    }

    public final List<T> getRepliedChildren(){
        List<T> children=mChildren;
        List<T> list=null;
        if (null!=children){
            list=new ArrayList<>();
            synchronized (children){
                for (T child:children) {
                    if (null!=child&&null!=child.getReply()){
                        list.add(child);
                    }
                }
            }
        }
        return null!=list&&list.size()>0?list:null;
    }

    public final T getFirstUnReplyChild(){
        List<T> children=mChildren;
        if (null!=children){
            synchronized (children){
                for (T child:children) {
                    if (null!=child&&null==child.getReply()){
                        return child;
                    }
                }
            }
        }
        return null;
    }

    public final T getFirstUnSucceedChild(){
        List<T> children=mChildren;
        if (null!=children){
            synchronized (children){
                for (T child:children) {
                    if (null!=child&&(!child.isReply(true,What.WHAT_SUCCEED))){
                        return child;
                    }
                }
            }
        }
        return null;
    }

    public final ArrayList<T> getChildren() {
        List<T> children=mChildren;
        int size=null!=children?children.size():-1;
        ArrayList<T> result=size>0?new ArrayList<>(size):null;
        return null!=result&&result.addAll(children)?result:null;
    }

    public final Reply getFirstUnSucceedChildReply(){
        T child=getFirstUnSucceedChild();
        return null!=child?child.getReply():null;
    }

    public final T getConveying() {
        return mConveying;
    }

    public final int index(T convey){
        List<T> children=null!=convey?mChildren:null;
        int length=null!=children?children.size():-1;
        return length>0?children.indexOf(convey):-1;
    }

    public final int getChildCount(){
        List<T> children=mChildren;
        return null!=children?children.size():0;
    }


    @Override
    public boolean isSuccessFinished() {
        return super.isSuccessFinished()&&null==getFirstUnSucceedChild();
    }
}
