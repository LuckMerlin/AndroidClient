package com.merlin.transport;

import com.merlin.file.FileUploadBody;

import java.io.File;

public final class FileUpload extends Convey<FileUploadBody> {
    private final File mFile;

    public FileUpload(File file,String name){
        super(null!=name&&name.length()>0?name:null!=file?file.getName():null,null!=file?file.length():-1,-1);
        mFile=file;
    }

    @Override
    protected void onPrepare(String debug) {
        final File file=mFile;
        if (null==file||!file.exists()){
            finish(WHAT_FILE_EXIST,"File not exist.",file);
        }else if (!file.canRead()){
            finish(WHAT_NONE_PERMISSION,"File none read permission.",file);
        }
         iteratorAddAllFileInDirectory(file,debug);
    }

    @Override
    protected Canceler onStart(String debug) {
        
        return null;
    }

    private void iteratorAddAllFileInDirectory(File file,String debug){
        if (null!=file){
            addChild(new FileUploadBody(file,null),debug);
            if (file.isDirectory()){
                File[] files=file.listFiles();
                if (null!=files){
                    for (File child:files) {
                        if (null!=child){
                            iteratorAddAllFileInDirectory(child,debug);
                        }
                    }
                }
            }
        }
    }

    public File getFile() {
        return mFile;
    }

}
