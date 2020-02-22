package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.player.Playable;
import com.merlin.util.Time;

public class NasMedia implements Parcelable, Playable {
    private String md5;
    private String title;
    private String album;
    private String artist;
    private int channel;
    private long duration=0;
    private boolean favorite;
    private long bitrate;
    private long sampleRate;
    private String bitrateMode;

    public boolean isFavorite() {
        return favorite;
    }

    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
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

    public String getMd5() {
        return md5;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getChannel() {
        return channel;
    }

    public long getBitrate() {
        return bitrate;
    }

    public long getSampleRate() {
        return sampleRate;
    }


    public String getBitrateMode() {
        return bitrateMode;
    }


    public String getDurationText(){
        return Time.formatMediaDuration(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private NasMedia(Parcel in){
        duration=in.readLong();
        title=in.readString();
        album=in.readString();
        artist=in.readString();
        md5=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(md5);
    }

    public static final Creator<NasMedia> CREATOR = new Creator<NasMedia>() {
        @Override
        public NasMedia createFromParcel(Parcel in) {
            return new NasMedia(in);
        }

        @Override
        public NasMedia[] newArray(int size) {
            return new NasMedia[size];
        }
    };


    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj){
            if (obj instanceof String){
                return null!=md5&&md5.equals(obj);
            }else if (obj instanceof NasMedia){
                 String md5=((NasMedia)obj).md5;
                return (null==md5&&this.md5==null)||(null!=md5&&null!=this.md5&&this.md5.equals(md5));
            }
        }
        return super.equals(obj);
    }
}
