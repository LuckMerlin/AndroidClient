package com.merlin.file;

import com.merlin.transport.Canceler;
import com.merlin.transport.Convey;

import java.io.File;

public class FileUploadBody extends Convey {

    public FileUploadBody(File file,String name){
        super(null!=name&&name.length()>0?name:null!=file?file.getName():null,null!=file?file.length():-1,-1);
    }

    @Override
    protected Canceler onStart(String debug) {

        return null;
    }
}
