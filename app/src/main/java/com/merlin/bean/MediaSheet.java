package com.merlin.bean;

import java.io.Serializable;
import java.util.List;

public class MediaSheet implements Serializable {
    private int size;
    private User createUser;
    private String commentId;
    private String createTime;
    private String createUserId;
    private long id;
    private String imageUrl;
    private String note;
    private String title;
    private List<NasMedia> data;

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getCreateUser() {
        return createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String image) {
        this.imageUrl = image;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NasMedia> getData() {
        return data;
    }

    public void setData(List<NasMedia> data) {
        this.data = data;
    }
}
