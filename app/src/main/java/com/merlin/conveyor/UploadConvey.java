package com.merlin.conveyor;

import com.merlin.api.Canceler;
import com.merlin.api.CoverMode;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.server.Retrofit;
import com.merlin.service.ApiNasFileMeta;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.transport.Status;

import java.io.File;

import io.reactivex.Observable;

public class UploadConvey implements Convey {
    private final String mPath;
    private final String mServerUrl;
    private final String mFolder;
    private final int mCoverMode;

    public UploadConvey(String path,String serverUrl,String folder,int coverMode){
        mPath=path;
        mServerUrl=serverUrl;
        mFolder=folder;
        mCoverMode=coverMode;
    }

    @Override
    public Reply<Canceler> onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
            String path=mPath;
            String serverUrl=mServerUrl;
            if (null==path||null==serverUrl||null==retrofit){
                return new Reply<>(true, What.WHAT_ARGS_INVALID,"Args NULL.",null);
            }
            if (path.length()<=0||serverUrl.length()<=0){
                return new Reply<>(true, What.WHAT_ARGS_INVALID,"Args invalid.",null);
            }
            File file=new File(path);
            if (!file.exists()){
                return new Reply<>(true, What.WHAT_FILE_NOT_EXIST,"File not exist.",null);
            }
            if (!file.canRead()){
                return new Reply<>(true, What.WHAT_NONE_PERMISSION,"File none permission.",null);
            }
//        public final static int NONE=0x00; //0000 0000
//        public final static int REPLACE=0x01;//0000 0001
//        public final static int KEEP=0x02;//0000 0010
//        public final static int SKIP=0x04;//0000 0100
//        public final static int POSTFIX=0x08;//0000 1000
           change.onConveyStatusChanged(Status.PREPARING,this,new Reply(true,What.WHAT_SUCCEED,"Preparing",null));
           int coverMode=mCoverMode;
           if (coverMode== CoverMode.SKIP||coverMode==CoverMode.NONE){
//               Observable.amb()
//               retrofit.prepare(ApiNasFileMeta.class,serverUrl,null).getMeta();
           }
           String folder=mFolder;
        return null;
    }
}
