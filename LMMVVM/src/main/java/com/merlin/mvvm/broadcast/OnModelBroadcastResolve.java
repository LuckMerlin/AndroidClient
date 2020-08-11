package com.merlin.mvvm.broadcast;

import android.content.Intent;

public interface OnModelBroadcastResolve {
    Intent[] onBroadcastResolve();
}
