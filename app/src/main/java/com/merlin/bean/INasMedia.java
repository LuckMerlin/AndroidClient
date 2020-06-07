package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.util.Time;

/**
 * @deprecated
 */
public class INasMedia implements Parcelable {
    private String md5;
    private String title;
    private String album;
    private String artist;
    private int channel;
    private String genre;
    private long duration=0;
    private boolean favorite;
    private int bitrate;
    private int sampleRate;
    private String imageUrl;
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

    public int getBitrate() {
        return bitrate;
    }

    public String getGenre() {
        return genre;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public String  getThumbImageUrl() {
        return imageUrl;
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

    private INasMedia(Parcel in){
        duration=in.readLong();
        title=in.readString();
        album=in.readString();
        artist=in.readString();
        md5=in.readString();
        favorite=in.readInt()==1;
        imageUrl=in.readString();
        sampleRate=in.readInt();
        bitrate=in.readInt();
        bitrateMode=in.readString();
        genre=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
        dest.writeString(md5);
        dest.writeInt(favorite?1:0);
        dest.writeString(imageUrl);
        dest.writeInt(sampleRate);
        dest.writeInt(bitrate);
        dest.writeString(bitrateMode);
        dest.writeString(genre);
    }

    public static final Creator<INasMedia> CREATOR = new Creator<INasMedia>() {
        @Override
        public INasMedia createFromParcel(Parcel in) {
            return new INasMedia(in);
        }

        @Override
        public INasMedia[] newArray(int size) {
            return new INasMedia[size];
        }
    };


    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj){
            if (obj instanceof String){
                return null!=md5&&md5.equals(obj);
            }else if (obj instanceof INasMedia){
                 String md5=((INasMedia)obj).md5;
                return (null==md5&&this.md5==null)||(null!=md5&&null!=this.md5&&this.md5.equals(md5));
            }
        }
        return super.equals(obj);
    }
}
