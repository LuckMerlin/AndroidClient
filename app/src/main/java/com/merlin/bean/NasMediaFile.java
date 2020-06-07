package com.merlin.bean;

public class NasMediaFile extends Path  {
    private String artist;
    private String album;
    private long duration;


    public NasMediaFile(){
        this(null,null,null,null);
    }

    public NasMediaFile(String host, String parent, String name, String extension){
        super(host,parent,name,extension);
    }

    public long getDuration() {
        return duration;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

}
