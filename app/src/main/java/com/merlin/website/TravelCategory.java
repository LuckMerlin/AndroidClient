package com.merlin.website;

import androidx.annotation.Nullable;

import com.merlin.bean.Path;

public final class TravelCategory {
        private boolean banner;
        private Path url;
        private String title;
        private String note;
        private Object tag;
        private String permission;
        private long createTime;
        private long id;
        private int children;

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public Path getUrl() {
        return url;
    }

    public void setUrl(Path url) {
        this.url = url;
    }

    public String getUrlPath() {
        Path currUrl=url;
        return null!=currUrl?currUrl.getPath():null;
    }

    public String getNote() {
        return note;
    }

    public String getPermission() {
        return permission;
    }

    public int getChildren() {
        return children;
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
