package com.merlin.website;

import androidx.annotation.Nullable;

import com.merlin.bean.IPath;

public final class TravelCategory {
        private boolean banner;
        private IPath url;
        private String title;
        private String note;
        private Object tag;
        private String permission;
        private long createTime;
        private long id;
        private int data;

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public IPath getUrl() {
        return url;
    }

    public Long getUrlId() {
        IPath currPath=url;
        return null!=currPath?currPath.getId():null;
    }

    public void setUrl(IPath url) {
        this.url = url;
    }

    public String getUrlPath()
    {
        IPath currUrl=url;
        return null!=currUrl?currUrl.getPath():null;
    }

    public String getNote() {
        return note;
    }

    public String getPermission() {
        return permission;
    }

    public int getChildren() {
        return data;
    }

    public boolean isBanner() {
        return banner;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof TravelCategory){
            return ((TravelCategory)obj).id==id;
        }
        return super.equals(obj);
    }
}
