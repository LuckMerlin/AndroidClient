package com.merlin.bean;

import com.merlin.binding.ViewDataLoadable;

public interface FileMeta extends ViewDataLoadable {

     Object getImageUrl();

     String getPath(boolean host);

     boolean applyModify(FModify modify);

     String getExtension();

     String getTitle();

     double getModifyTime();

     String getName(boolean extension);

     String getParent();
     long getLength();
     int getChildCount();

     boolean isDirectory();

     boolean isAccessible();

     String permission();

}
