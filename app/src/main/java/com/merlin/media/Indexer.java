package com.merlin.media;

import com.merlin.debug.Debug;

public class Indexer {

    public int pre(Mode mode, int current, int count) {
        if (null!=mode&&count>0){
            if (mode==Mode.QUEUE_SORT){
                int next=current-1;
                return next>=0&&next<count?next:count-1;
            }
            return (int)Math.random()*count;
        }
        return -1;
    }

    public int next(Mode mode, int current, int count,boolean user) {
        if (count>0) {
            int next=current+1;
            if (mode == Mode.QUEUE_SORT) {
                return next>=0&&next<count?next:user?0:-1;
            }else if (mode == Mode.RANDOM) {
                return (int)(Math.random()*count);
            } else if (mode == Mode.SINGLE) {
                return user?(int)Math.random()*count:current;
            }
        }
        return -1;
    }
}
