package com.merlin.bean;

public final class LocalFile extends File {

    public LocalFile(String parent,String title,String name,String extension,
                     String imageUrl,long size,long modifyTime, boolean directory, boolean accessible){
        super(parent,title,name,extension,imageUrl,size,modifyTime,directory,accessible);
    }

    public static LocalFile create(java.io.File file, String imageUrl){
        if (null!=file){
            String parent=file.getParent();
            parent=null!=parent?parent+ java.io.File.separator:null;
            String[] fix=getPostfix(file);
            String name=fix[0];
            String extension=fix[1];
            String title=file.getName();
            boolean directory=file.isDirectory();
            long size=file.length();
            if (directory){
                String[] names=file.list();
                size=null!=names?names.length:0;
            }
            long modifyTime=file.lastModified();
            boolean accessible=file.canRead()&&(!file.isDirectory()||file.canExecute());
            return new LocalFile(parent,title,name,extension,imageUrl,size,modifyTime,directory,accessible);
        }
        return null;
    }

    public java.io.File getFile(){
        String path= getPath();
        return null!=path&&path.length()>0?new java.io.File(path):null;
    }

    public static String[] getPostfix(java.io.File file){
        String name=null!=file?file.getName():null;
        int index=null!=name?name.lastIndexOf("."):-1;
        String[] result=new String[2];
        if (index>0){
            result[0]=name.substring(0,index);
            result[1]=name.substring(index);
        }else{
            result[0]=name;
        }
        return result;
    }


}
