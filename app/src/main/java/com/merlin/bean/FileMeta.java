package com.merlin.bean;

public interface FileMeta {

     Object getImageUrl();

     String getPath();

     boolean applyModify(FModify modify);

     String getExtension();

     String getTitle();

     long getSize();

     double getModifyTime();

     String getName(boolean extension);

     String getParent();

     boolean isDirectory();
     boolean isAccessible();
     String permission();

}
