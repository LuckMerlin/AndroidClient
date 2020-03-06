package com.merlin.bean;

public interface FileMeta {

     Object getImageUrl();

     String getPath();

     boolean applyModify(FModify modify);

     String getExtension();

     String getTitle();

     double getModifyTime();

     String getName(boolean extension);

     String getParent();
     int getChildCount();

     boolean isDirectory();
     boolean isAccessible();
     String permission();

}
