package com.merlin.conveyor;

import com.merlin.api.Canceler;
import com.merlin.api.Reply;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;

import java.util.ArrayList;
import java.util.List;

class ConveyGroup_<T extends Convey> extends Convey {
    private final List<T> mList;
    private T mConveying;

    public ConveyGroup_(List<T> list){
        mList=null!=list&&list.size()>0?list:new ArrayList<>(1);
    }

    @Override
    protected boolean onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
        return false;
    }

    //    @Override
//    public final Canceler onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
//        List<T> list=mList;
//        return null;
//    }

    private boolean next(){
        T conveying=mConveying;
        if (null!=conveying){

        }
        return false;
    }

    public final T getConveying() {
        return mConveying;
    }
}
