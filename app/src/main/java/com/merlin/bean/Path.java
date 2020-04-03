package com.merlin.bean;

public class Path {
    private String parent;
    private String name;
    private String extension;

    public Path(){
        this(null,null,null);
    }

    public Path(String parent,String name,String extension){
        this.parent=parent;
        this.name=name;
        this.extension=extension;
    }

    public final String getExtension() {
        return extension;
    }

    public final String getParent() {
        return parent;
    }

    public final String getName() {
        return getName(false);
    }

    public final String getName(boolean extension) {
        return null!=name&&extension&&null!=this.extension?name+this.extension:name;
    }

    public String getPath() {
        String value=getName(true);
        return null!=parent&&null!=value?parent+value:null;
    }
}
