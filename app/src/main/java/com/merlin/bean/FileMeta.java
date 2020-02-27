package com.merlin.bean;

public interface FileMeta {

     String getImageUrl();

     String getPath();

     boolean applyModify(FileModify modify);

     String getExtension();

     String getTitle();

     long getSize();

     double getModifyTime();

     String getName();

     String getParent();

      boolean isDirectory();
      boolean isAccessible();
      String permission();

}
