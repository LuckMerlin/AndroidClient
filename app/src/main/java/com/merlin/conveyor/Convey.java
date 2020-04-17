package com.merlin.conveyor;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.transport.Status;

public abstract class Convey implements Status {
    private ConveyStatus mStatus;
    private boolean mCanceled=false;

    protected abstract boolean onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug);

    public final boolean convey(Retrofit retrofit, OnConveyStatusChange change, String debug){
        ConveyStatus status=mStatus;
        if (null!=status){
            return updateStatus(FINISHED,change,this,new Reply(true,What.WHAT_ALREADY_DOING,"Already started.",null));
        }
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
    /**
     * @deprecated
     */
    final boolean progress(OnConveyStatusChange change,Convey child,Reply reply){
        if (null!=change){
            change.onConveyStatusChanged(PROGRESS,this,child,reply);
            return true;
        }
        return false;
    }

    /**
     * @deprecated
     */
    final boolean finish(OnConveyStatusChange change,Convey child,Reply reply){
        if (null!=change){
            change.onConveyStatusChanged(FINISHED,this,child,reply);
            return true;
        }
        return false;
    }

    public final boolean isCanceled() {
        return mCanceled;
    }

    final boolean updateStatus(int status, OnConveyStatusChange change, Convey child, Reply reply){
        if (null!=change){
            change.onConveyStatusChanged(status,this,child,reply);
            return true;
        }
        return false;
    }

    public interface Confirm{
        void onConfirm(int what,String debug);
    }

}
