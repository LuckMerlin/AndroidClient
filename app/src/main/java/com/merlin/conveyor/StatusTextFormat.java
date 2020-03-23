package com.merlin.conveyor;

import android.content.Context;

import com.merlin.api.Reply;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.transport.Convey;
import com.merlin.transport.ConveyGroup;
import com.merlin.transport.Status;

public class StatusTextFormat implements Status {

    public Integer format(Convey status){
        if (null!=status){
            switch (status.getStatus()){
                case CANCELED:
                    return R.string.canceled;
                case PREPARING:
                    return R.string.preparing;
                case PREPARED:
                    return R.string.prepared;
                case STARTED:
                    return R.string.started;
                case PAUSED:
                    return R.string.paused;
                case FINISHED:
                    if (status instanceof ConveyGroup){
                       Convey convey= ((ConveyGroup)status).getFirstUnSucceedReply();
                    }
                    status.getReply();
                    return R.string.finished;
            }
        }
        return null;
    }
}
