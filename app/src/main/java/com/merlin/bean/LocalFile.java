package com.merlin.bean;

import com.merlin.client.R;

import java.io.File;

public final class LocalFile implements FileMeta{
    private String parent;
    private String title;
    private String name;
    private String extension;
    private Object imageUrl;
    private long size;
    private double modifyTime;
    private boolean directory;
    private boolean accessible;

    public LocalFile(File file){
        this(file,null);
    }

    public LocalFile(File file,String imageUrl){
        if (null!=file){
            this.parent=file.getParent();
            this.parent=null!=this.parent?this.parent+File.separator:null;
            String name=this.title=file.getName();
            int index=null!=name&&name.length()>0?name.lastIndexOf("."):-1;
            this.extension=index>0?name.substring(index):null;
            this.name=index<=0?name:name.substring(0,index);
            this.imageUrl=imageUrl;
            boolean directory=this.directory=file.isDirectory();
            this.size=file.length();
            if (directory){
                String[] names=file.list();
                this.size=null!=names?names.length:0;
            }
            this.imageUrl=null!=imageUrl&&imageUrl.length()>0?imageUrl:createDefaultThumb(file);
            this.modifyTime=file.lastModified();
            this.accessible=file.canRead()&&(!file.isDirectory()||file.canExecute());
        }
    }

    private LocalFile(String parent,String title,String name,String extension,String imageUrl,long size,long modifyTime){
        this.parent=parent;
        this.title=title;
        this.name=name;
        this.extension=extension;
        this.imageUrl=imageUrl;
        this.size=size;
        this.modifyTime=modifyTime;
    }

    private Object createDefaultThumb(File file){
        if (null!=file){
            if (file.isDirectory()){
                return R.drawable.folder;
            }
        }
        return null;
    }

    @Override
    public boolean applyModify(FileModify modify) {

        return false;
    }

    @Override
    public Object getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public double getModifyTime() {
        return modifyTime;
    }

    @Override
    public String getPath() {
        return null!=parent&&null!=name?parent+name:null;
    }

    @Override
    public boolean isAccessible() {
        return accessible;
    }

    @Override
    public boolean isDirectory() {
        return directory;
    }

    @Override
    public String permission() {
        return "";
    }

    public File getFile(){
       String path= getPath();
       return null!=path&&path.length()>0?new File(path):null;
    }
}
