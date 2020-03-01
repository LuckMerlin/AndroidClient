package com.merlin.bean;

public final class FModify {
    /**
     * @deprecated
     */
    public final static int MODE_NONE=FMode.MODE_NONE;//0000 0000
    /**
     * @deprecated
     */
    public final static int MODE_COVER=FMode.MODE_COVER;//0000 0001
    /**
     * @deprecated
     */
    public final static int MODE_SKIP=FMode.MODE_SKIP;//0000 0010
    /**
     * @deprecated
     */
    public final static int MODE_KEEP=FMode.MODE_KEEP;//0000 0100
    /**
     * @deprecated
     */
    public final static int MODE_POSTFIX=FMode.MODE_POSTFIX;//0000 1000
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
