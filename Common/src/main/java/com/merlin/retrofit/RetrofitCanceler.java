package com.merlin.retrofit;

import com.merlin.debug.Debug;
import com.merlin.lib.Canceler;

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
