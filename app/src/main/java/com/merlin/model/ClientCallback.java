package com.merlin.model;

import com.merlin.bean.FolderData;

import java.util.Collection;

public interface ClientCallback {
    void onBrowserModeChange(BrowserModel model,int lase,int curr);
    void onPageDataLoad(BrowserModel model, FolderData folder);
    boolean onProcessSet(Object object,String debug);
    Collection<Object> getAllClients();
}
