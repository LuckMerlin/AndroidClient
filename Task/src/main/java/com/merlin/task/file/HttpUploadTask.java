package com.merlin.task.file;


public abstract class HttpUploadTask extends HttpFileTransTask<String, String> {
    private final String mToFolder;
    private final String mToName;

    public HttpUploadTask(String name,String from, String to,String toFolder,String toName) {
        this(name,from,to,null,toFolder,toName);
    }

    public HttpUploadTask(String name,String from, String to,String method,String toFolder,String toName) {
        super(name,from, to,method);
        mToFolder=toFolder;
        mToName=toName;
    }

    public final String getToFolder() {
        return mToFolder;
    }

    public final String getToName() {
        return mToName;
    }

}
