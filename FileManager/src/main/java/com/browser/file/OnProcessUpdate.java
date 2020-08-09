package com.browser.file;

import com.merlin.bean.Path;

public interface OnProcessUpdate<T extends Path> {
    void onProcessUpdate(boolean succeed,T path);
}
