package com.merlin.model;

import com.merlin.bean.FolderData;

public interface ClientCallback {
    void onBrowserModeChange(BrowserModel model,int lase,int curr);
    void onPageDataLoad(BrowserModel model, FolderData folder);
}
