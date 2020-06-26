package com.merlin.browser;

import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.lib.Canceler;
import com.merlin.server.Client;

public class LocalFileBrowser extends FileBrowser {
    public LocalFileBrowser(Client meta, Callback callback) {
        super(meta, callback);
    }

    @Override
    protected boolean onReboot(String debug) {
        return false;
    }

    @Override
    protected boolean onOpenPath(Path meta, String debug) {
        return false;
    }

    @Override
    protected boolean onShowPathDetail(Path meta, String debug) {
        return false;
    }

    @Override
    protected boolean onSetAsHome(String path, OnApiFinish<Reply<String>> finish, String debug) {
        return false;
    }

    @Override
    protected boolean onCreatePath(boolean dir, int coverMode, String folder, String name, OnApiFinish<Reply<Path>> finish, String debug) {
        return false;
    }

    @Override
    protected boolean onRenamePath(String path, String name, int coverMode, OnApiFinish<Reply<Path>> finish, String debug) {
        return false;
    }

    @Override
    protected Canceler onPageLoad(Object arg, int from, OnApiFinish finish) {
        return null;
    }
}
