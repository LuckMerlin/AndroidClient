package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.util.Time;

public class Music implements Parcelable {
    private String title;
    private String album;
    private String artist;
    private int channel;
    private long duration=0;
    private long bitrate;
    private long sampleRate;
    private String bitrateMode;

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

    private Music(Parcel in){
        duration=in.readLong();
        title=in.readString();
        album=in.readString();
        artist=in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(duration);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeString(artist);
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

}
