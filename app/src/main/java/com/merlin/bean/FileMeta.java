package com.merlin.bean;

import com.merlin.api.Reply;
import com.merlin.binding.ViewDataLoadable;

public interface FileMeta extends ViewDataLoadable {

     Object getImageUrl();

     String getPath(boolean host);

     boolean applyChange(Reply<Path> reply);

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
