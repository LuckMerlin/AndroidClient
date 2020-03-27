package com.merlin.bean;

import java.io.File;

public class LocalFolder extends FolderData<LocalFile> {

    public final File getFile(){
        String path=getPath();
        return null!=path&&path.length()>0?new File(path):null;
    }

}
