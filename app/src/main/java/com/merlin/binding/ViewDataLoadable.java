package com.merlin.binding;

import android.view.View;

public interface ViewDataLoadable {
    int DATA_LOADED = 123;
    Object onViewData(int what,View view,String debug);
}
