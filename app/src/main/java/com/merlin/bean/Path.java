package com.merlin.bean;

public class Path {
    private String parent;
    private String name;
    private String extension;
    private String host;

    public Path(){
        this(null,null,null);
    }

    public Path(String parent,String name,String extension){
        this(null,parent,name,extension);
    }

    public Path(String host,String parent,String name,String extension){
        this.host=host;
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

    public final String getHost() {
        return host;
    }

    public final String getPath() {
        return getPath(null);
    }

    public final String getPath(String hostDivider) {
        String value=getName(true);
        String path=null!=parent&&null!=value?parent+value:null;
        String host=null!=hostDivider?this.host:null;
        return null!=host?host+(hostDivider.length()<=0?"/":hostDivider):path;
    }
}
