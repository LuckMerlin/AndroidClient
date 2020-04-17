package com.merlin.conveyor;

import com.merlin.api.Canceler;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;

import java.util.ArrayList;
import java.util.List;

public class ConveyGroup<T extends Convey> extends Convey{
    private final List<Convey> mConveys=new ArrayList<>(1);
    private Convey mConveying;

    public ConveyGroup(){
        this(null);
    }

    public ConveyGroup(List<Convey> conveys){
        add(conveys);
    }

    @Override
    protected final boolean onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
        List<Convey> conveys=mConveys;
        if (null==conveys||conveys.size()<0){
            return updateStatus(FINISHED,change,null,new Reply<>(false, What.WHAT_EMPTY, "Convey group is EMPTY.",null))&&false;
        }
        final Convey next=indexNextUnFinished();
        if (null==next){
            return updateStatus(FINISHED,change,null,new Reply<>(false, What.WHAT_ERROR_UNKNOWN,
                    "Convey group index first next fail.",null))&&false;
        }
        final Canceler canceler=(boolean cancel,String de)->{
            return true;
        };
        final OnConveyStatusChange callback=new OnConveyStatusChange() {
            @Override
            public void onConveyStatusChanged(int status, Convey parent, Convey convey, Reply reply) {
                updateStatus(status,change,convey,reply);
                if (status==FINISHED){
                    final Convey nextConvey=indexNextUnFinished();
                    if (null!=nextConvey){
                        nextConvey.convey(retrofit,this,debug);
                    }else{
                        updateStatus(FINISHED,change,ConveyGroup.this,new Reply(true,What.WHAT_SUCCEED,null,null));
                    }
                }
            }
        };
        mConveying=next;
        return next.convey(retrofit,callback,debug);
    }

    public final boolean add(Convey ...conveys){
        List<Convey> list=mConveys;
        if (null!=list&&null!=conveys&&conveys.length>0){
            for (Convey child:conveys) {
                if (null!=child&&!list.contains(child)&&list.add(child)){
                    //DO nothing
                }
            }
        }
        return true;
    }

    public final boolean add(List<Convey> list){
        List<Convey> conveys=null!=list&&list.size()>0?mConveys:null;
        if (null!=conveys){
            for (Convey child:list) {
                if (null!=child&&!conveys.contains(child)&&conveys.add(child)){
                    //Do nothing
                }
            }
            return true;
        }
        return false;
    }

    public final Convey indexNextUnFinished(){
        List<Convey> conveys=mConveys;
        int size=null!=conveys?conveys.size():-1;
        if (size>0){
            Convey conveying=mConveying;
            int index=null!=conveys?conveys.indexOf(conveying):-1;
            for (int i = index+1; i < size; i++) {
                Convey convey=conveys.get(i);
                if (null!=convey&&!convey.isFinished()){
                    return convey;
                }
            }
        }
        return null;
    }

    public final int size(){
        List<Convey> conveys=mConveys;
        return null!=conveys?conveys.size():0;
    }

}
