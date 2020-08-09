package com.browser.file;

import com.merlin.api.OnProcessChange;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.retrofit.Retrofit;
import java.util.ArrayList;

public class FileCopyProcess extends FileProcess<Path>{
    private final Path mFolder;
    private final int mCoverMode;

    public FileCopyProcess(String title, ArrayList<Path> files, Path folder, int coverMode){
        super(title,files);
        mFolder=folder;
        mCoverMode=coverMode;
    }

    @Override
    protected Reply<Path> onProcess(Path pathObj, OnProcessChange<Path> processProgress, Retrofit retrofit) {
        final Path toFolder=mFolder;
        String toFolderPath=null!=toFolder?toFolder.getPath():null;
        Reply<Path> reply=null;
        final String filePath=null!=pathObj?pathObj.getPath():null;
        if (null==filePath||filePath.length()<=0||null==toFolderPath||toFolderPath.length()<=0){
            reply= new Reply(true, What.WHAT_ARGS_INVALID,"Path invalid.",pathObj);
        }else if (!toFolder.isDirectory()){
            reply= new Reply(true, What.WHAT_ARGS_INVALID,"Target path not folder",pathObj);
        }else if (pathObj.isLocal()){//Copy local file
            if (toFolder.isLocal()){//Copy local to local

            }else{//Upload local to cloud

            }
        }else{//Copy cloud file
            if (toFolder.isLocal()){//Download cloud to local

            }else{//Upload local to cloud

            }
        }
        return null;
    }
}
