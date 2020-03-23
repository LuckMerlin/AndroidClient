package com.merlin.conveyor;

import android.content.Context;

import com.merlin.api.Reply;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.transport.Convey;
import com.merlin.transport.ConveyGroup;
import com.merlin.transport.Status;

public class StatusTextFormat implements Status {

    public String format(Context context, Convey status, String def){
        if (null!=context&&null!=status){
            switch (status.getStatus()){
                case CANCELED:
                    return context.getString(R.string.canceled);
                case PREPARING:
                    return context.getString(R.string.preparing);
                case PREPARED:
                    return context.getString(R.string.prepared);
                case STARTED:
                    return context.getString(R.string.started);
                case PAUSED:
                    return context.getString(R.string.paused);
                case FINISHED:
                    Reply reply=status instanceof ConveyGroup?status.getReply():status.getReply();
                    return context.getString(R.string.finished);
            }
        }
        return def;
    }
}
