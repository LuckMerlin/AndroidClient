package com.merlin.bean;

public final class FModify {
    public final static int MODE_NONE=0;//0000 0000
    public final static int MODE_COVER=1;//0000 0001
    public final static int MODE_SKIP=2;//0000 0010
    public final static int MODE_KEEP=4;//0000 0100
    public final static int MODE_POSTFIX=8;//0000 1000
    private int mode;
    private String parent;
    private String name;
    private String extension;

    public FModify(){
        this(null,null,null,MODE_NONE);
    }

    public FModify(String parent, String name,String extension, int mode){
        this.parent=parent;
        this.extension=extension;
        this.name=name;
        this.mode=mode;
    }

    public int getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public String getExtension() {
        return extension;
    }
}
