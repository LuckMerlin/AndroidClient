package com.merlin.transport;

import java.io.File;

public final class FileUpload extends Convey {
    private final File mFile;

    public FileUpload(File file){
        mFile=file;
    }

    public File getFile() {
        return mFile;
    }
}
