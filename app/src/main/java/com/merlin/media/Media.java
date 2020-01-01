package com.merlin.media;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

import java.io.File;

@Entity
public class Media {
    @Id(autoincrement = true)
    @Property(nameInDb = "id")
    private long id;
    @NotNull
    @Property(nameInDb = "title")
    private String title;
    @NotNull
    @Property(nameInDb = "path")
    @Index(unique = true)
    private String path;
    @Property(nameInDb = "md5")
    private String md5=null;
    @Property(nameInDb = "cloudUrl")
    private String cloudUrl;
    @Property(nameInDb = "account")
    private String account;
    @Property(nameInDb = "album")
    private String album;
    @Property(nameInDb = "artist")
    private String artist;
    @Property(nameInDb = "duration")
    private long duration=12131;

    @Generated(hash = 580707197)
    public Media(long id, @NotNull String title, @NotNull String path, String md5,
            String cloudUrl, String account, String album, String artist,
            long duration) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.md5 = md5;
        this.cloudUrl = cloudUrl;
        this.account = account;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }
    @Generated(hash = 551662551)
    public Media() {
    }
    
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getMd5() {
        return this.md5;
    }
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getAlbum() {
        return this.album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public long getDuration() {
        return this.duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDurationText(){
        return "00:03:12";
    }
    public String getCloudUrl() {
        return this.cloudUrl;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    public boolean isLocalExist(){
        String path=getPath();
        return null!=path&&path.length()>0&&new File(path).length()>0;
    }

}
