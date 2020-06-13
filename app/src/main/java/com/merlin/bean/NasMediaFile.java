package com.merlin.bean;

/**
 * @deprecated
 */
public abstract class NasMediaFile extends Path  {
    private String artist;
    private String album;
    private long duration;


    public NasMediaFile(){
        this(null,null,null,null);
    }

    public NasMediaFile(String host, String parent, String name, String extension){
        super(host,parent,name,extension);
    }

    public final long getDuration() {
        return duration;
    }

    public final String getAlbum() {
        return album;
    }

    public final String getArtist() {
        return artist;
    }

}
