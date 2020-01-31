package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.player.Playable;
import com.merlin.util.Time;

import java.io.File;

public class Media implements Playable, Parcelable {
    private long id;
    private String title;
    private String path;
    private String md5=null;
    private String thumbImageUrl;
    private String album;
    private String artist;
    private long duration=0;
    
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
    public String getAlbum() {
        return this.album;
    }
    public String getArtist() {
        return this.artist;
    }
    public long getDuration() {
        return this.duration;
    }

    public String getDurationText(){
        return Time.formatMediaDuration(duration);
    }

    public String getThumbImageUrl() {
        return thumbImageUrl;
    }

    public boolean isLocalExist(){
        String path=getPath();
        return null!=path&&path.length()>0&&new File(path).length()>0;
    }

    public Media(long id, String title,  String path,
            String md5, String thumbImageUrl, String album, String artist, long duration) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.md5 = md5;
        this.thumbImageUrl = thumbImageUrl;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }
    public Media() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    private Media(Parcel in){
        id=in.readLong();
        duration=in.readLong();
        title=in.readString();
        path=in.readString();
        md5=in.readString();
        thumbImageUrl=in.readString();
        album=in.readString();
        artist=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(duration);
        dest.writeString(title);
        dest.writeString(path);
        dest.writeString(md5);
        dest.writeString(thumbImageUrl);
        dest.writeString(album);
        dest.writeString(artist);
    }

    public static final Creator<Media> CREATOR = new Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

}
