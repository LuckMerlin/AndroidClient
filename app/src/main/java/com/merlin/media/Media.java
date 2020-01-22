package com.merlin.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.player.Playable;

import java.io.File;

public class Media implements Parcelable, Playable {
    private long id;
    private long mediaId;
    private String title;
    private String path;
    private String md5=null;
    private String url;
    private String account;
    private String album;
    private String artist;
    private long duration=12131;
    
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
    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isLocalExist(){
        String path=getPath();
        return null!=path&&path.length()>0&&new File(path).length()>0;
    }

    public long getMediaId() {
        return this.mediaId;
    }
    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    private Media(Parcel in){
        if (null!=in){
            duration=in.readLong();
            artist=in.readString();
            album=in.readString();
            account=in.readString();
            url=in.readString();
            md5=in.readString();
            path=in.readString();
            title=in.readString();
            mediaId=in.readLong();
            id=in.readLong();
        }
    }
    public Media(long id, long mediaId, String title,  String path,
            String md5, String url, String account, String album, String artist,
            long duration) {
        this.id = id;
        this.mediaId = mediaId;
        this.title = title;
        this.path = path;
        this.md5 = md5;
        this.url = url;
        this.account = account;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }
    public Media() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeString(account);
        dest.writeString(url);
        dest.writeString(md5);
        dest.writeString(path);
        dest.writeString(title);
        dest.writeLong(mediaId);
        dest.writeLong(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>(){

        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }

    };
}
