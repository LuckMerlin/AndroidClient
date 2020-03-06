package com.merlin.transport;

public class Canceler {
     private boolean mCancel;

     public final boolean cancel(boolean cancel){
          boolean curr=mCancel;
          mCancel=cancel;
          return curr!=cancel;
     }

     public final boolean isCancel() {
          return mCancel;
     }
}
