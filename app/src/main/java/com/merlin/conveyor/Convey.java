package com.merlin.conveyor;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.transport.Status;

public abstract class Convey implements Status {
    private ConveyStatus mStatus;

    protected abstract boolean onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug);

    public final boolean convey(Retrofit retrofit, OnConveyStatusChange change, String debug){
        ConveyStatus status=mStatus;
        if (null!=status){
            return finish(change,this,new Reply(true,What.WHAT_ALREADY_DOING,"Already started.",null));
        }
        mStatus=status=new ConveyStatus(PREPARING,null);
        mStatus=status=new ConveyStatus(PREPARED,null);
        mStatus=status=new ConveyStatus(STARTED,null);
        final OnConveyStatusChange callback=(int st,Convey parent, Convey convey, Reply reply)-> {
            if (null!=convey&&convey==this&&st==FINISHED){
                mStatus=new ConveyStatus(FINISHED,reply);
            }
            if (null!=change){
                change.onConveyStatusChanged(st,parent,convey,reply);
            }
        };
       return onConvey(retrofit,callback,debug);
    }

    public final ConveyStatus getStatus() {
        return mStatus;
    }

    public final boolean isFinished(){
        ConveyStatus status=mStatus;
        if (null!=status){
            int statusValue=status.getStatus();
            if (statusValue==FINISHED){
                return true;
            }
        }
        return false;
    }

    final boolean progress(OnConveyStatusChange change,Convey child,Reply reply){
        if (null!=change){
            change.onConveyStatusChanged(PROGRESS,this,child,reply);
            return true;
        }
        return false;
    }

    final boolean finish(OnConveyStatusChange change,Convey child,Reply reply){
        if (null!=change){
            change.onConveyStatusChanged(FINISHED,this,child,reply);
            return true;
        }
        return false;
    }

}
