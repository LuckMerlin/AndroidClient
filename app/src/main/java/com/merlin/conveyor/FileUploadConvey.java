package com.merlin.conveyor;

import com.merlin.api.Canceler;
import com.merlin.api.CoverMode;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.LocalFile;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;

public final class FileUploadConvey implements Convey {
    private LocalFile mFrom;
    private String mToFolder;
    private String mToName;
    private String mToHost;
    private int mCoverMode= CoverMode.NONE;

    public FileUploadConvey(LocalFile from,String toHost, String toFolder, String toName, int coverMode){
        mFrom=from;
        mToHost=toHost;
        mToFolder=toFolder;
        mToName=toName;
        mCoverMode=coverMode;
    }

    @Override
    public final Reply<Canceler> onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
        LocalFile localFile=mFrom;
        String host=mToHost;
        if (null==host||host.length()<=0){
            return new Reply<>(true, What.WHAT_ARGS_INVALID,"Host invalid.",null);
        }
        LocalFile file=mFrom;
        String path=null!=file?file.getPath():null;
        if (null==path||path.length()<=0){
            return new Reply<>(true, What.WHAT_ARGS_INVALID,"Path invalid.",null);
        }
        return null;
    }
}
