package com.merlin.website;

import com.merlin.bean.Path;

import java.util.List;

public final class TravelCategory {
        private boolean banner;
        private Path url;
        private String title;
        private String note;
        private Object tag;
        private String permission;
        private long createTime;
        private long id;
        private List<Path> children;

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

    public String getNote() {
        return note;
    }

    public String getPermission() {
        return permission;
    }

    public List<Path> getChildren() {
        return children;
    }
}
