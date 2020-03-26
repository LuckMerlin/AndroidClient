package com.merlin.server;

import com.merlin.api.Canceler;
import com.merlin.debug.Debug;

import io.reactivex.disposables.Disposable;

public final class RetrofitCanceler implements Canceler {
    Disposable mDisposable;

    public boolean cancel(boolean cancel,String debug){
        Disposable disposable=mDisposable;
        if (null!=disposable){
            if (cancel&&!disposable.isDisposed()){
                disposable.dispose();
                Debug.D(getClass(),"Cancel api call "+(null!=debug?debug:"."));
                return true;
            }
        }
        return false;
    }
}
