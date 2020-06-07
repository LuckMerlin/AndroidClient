package com.merlin.bean;
import android.os.Parcel;

public class Document extends Path {
    private String title;
    private String imageUrl;
    private long length;
    private int size;
    private long modifyTime;
    private long accessTime;
    private String mime;
    private String md5;
    private boolean favorite;
    private int permissions;

    protected Document(){
        this(null,null,null,null);
    }

    protected Document(String host, String parent, String name, String extension){
        this(host,parent,name,extension,null,null,0,0,0,null,0,0);
    }

    public Document(String host, String parent, String name, String extension, String title, String imageUrl,
                    int childCount, long length, long modifyTime, String md5, long accessTime, int permissions){
        super(host,parent,name,extension);
        this.title=title;
        this.size=childCount;
        this.imageUrl=imageUrl;
        this.length=length;
        this.modifyTime=modifyTime;
        this.md5=md5;
        this.accessTime=accessTime;
        this.permissions=permissions;
    }

    public final boolean isAccessible(){
        return true;
    }

    public final boolean isDirectory() {
        return size>=0;
    }

    public final int getPermission(){
        return permissions;
    }

    public final int getChildCount() {
        return size;
    }

    public final String getTitle() {
        return title;
    }

    public final String getImageUrl() {
        return imageUrl;
    }

    public final long getModifyTime() {
        return modifyTime;
    }


    public final String getMime() {
        return mime;
    }

    public final boolean isFavorite() {
        return favorite;
    }

    public final long getAccessTime() {
        return accessTime;
    }

    protected final boolean setFile(String title, String imageUrl, int childCount,
                                    long length, long modifyTime, String md5, String mime,
                                    boolean favorite, long accessTime,int permissions){
        this.title=title;
        this.imageUrl=imageUrl;
        this.size=childCount;
        this.length=length;
        this.modifyTime=modifyTime;
        this.md5=md5;
        this.mime=mime;
        this.favorite=favorite;
        this.accessTime=accessTime;
        this.permissions=permissions;
        return true;
    }

    private Document(Parcel dest){
        if (null!=dest){
            String host=dest.readString();
            String parent=dest.readString();
            String name=dest.readString();
            String extension=dest.readString();
            setPath(host,parent,name,extension);
            title=dest.readString();
            imageUrl=dest.readString();
            length=dest.readLong();
            size=dest.readInt();
            modifyTime=dest.readLong();
            md5=dest.readString();
            mime=dest.readString();
            favorite=dest.readInt()==1;
            accessTime=dest.readLong();
            permissions=dest.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getHost());
        dest.writeString(getParent());
        dest.writeString(getName());
        dest.writeString(getExtension());
        dest.writeString(title);
        dest.writeString(imageUrl);
        dest.writeLong(length);
        dest.writeInt(size);
        dest.writeLong(modifyTime);
        dest.writeString(md5);
        dest.writeString(mime);
        dest.writeInt(favorite?1:0);
        dest.writeLong(accessTime);
        dest.writeInt(permissions);
    }

    public static final Creator<Document> CREATOR = new Creator<Document>() {
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }
        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
}
