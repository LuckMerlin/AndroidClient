package com.merlin.browser;

import androidx.recyclerview.widget.RecyclerView;

import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.lib.Canceler;
import com.merlin.server.Client;

import java.util.ArrayList;

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
    protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
        return null;
    }

    @Override
    public Boolean onItemSlideRemove(int position, Object data, int direction, RecyclerView.ViewHolder viewHolder, Remover remover) {
        return null;
    }

    @Override
    protected FileProcess onCreateFileProcess(int mode, ArrayList<Path> files, String target, Integer coverMode, String debug) {
        return null;
    }
}
