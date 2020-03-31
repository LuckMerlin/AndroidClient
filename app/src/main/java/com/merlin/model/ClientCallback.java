package com.merlin.model;

import com.merlin.api.PageData;
import com.merlin.browser.FileBrowser;

import java.util.Collection;

public interface ClientCallback {
    void onBrowserModeChange(FileBrowser model, int lase, int curr);
    void onPageDataLoad(FileBrowser model, PageData folder);
    boolean onProcessSet(Object object,String debug);
    Collection<Object> getAllClients();
}
