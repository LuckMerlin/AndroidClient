package com.merlin.bean;
import android.os.Parcel;

public class Document extends Path {
    private String title;
    private String imageUrl;
    private long length;
    private int childCount;
    private long modifyTime;
    private long accessTime;
    private boolean accessible;
    private String mime;
    private String md5;
    private boolean favorite;
    private int permission;

    protected Document(){
        this(null,null,null,null);
    }

    protected Document(String host, String parent, String name, String extension){
        this(host,parent,name,extension,null,null,0,0,0,false,null,0,0);
    }

    public Document(String host, String parent, String name, String extension, String title, String imageUrl,
                    int childCount, long length, long modifyTime, boolean accessible, String md5, long accessTime, int permission){
        super(host,parent,name,extension);
        this.title=title;
        this.childCount=childCount;
        this.imageUrl=imageUrl;
        this.length=length;
        this.modifyTime=modifyTime;
        this.accessible=accessible;
        this.md5=md5;
        this.accessTime=accessTime;
        this.permission=permission;
    }

    public final boolean isAccessible() {
        return accessible;
    }

    public final boolean isDirectory() {
        return childCount>=0;
    }

    public final String getMd5() {
        return md5;
    }

    public final int getPermission(){
        return permission;
    }

    public final int getChildCount() {
        return childCount;
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

    public final long getLength() {
        return length;
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
                                    long length, long modifyTime, boolean accessible, String md5, String mime,
                                    boolean favorite, long accessTime,int permission){
        this.title=title;
        this.imageUrl=imageUrl;
        this.childCount=childCount;
        this.length=length;
        this.modifyTime=modifyTime;
        this.accessible=accessible;
        this.md5=md5;
        this.mime=mime;
        this.favorite=favorite;
        this.accessTime=accessTime;
        this.permission=permission;
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
            childCount=dest.readInt();
            modifyTime=dest.readLong();
            accessible=dest.readBoolean();
            md5=dest.readString();
            mime=dest.readString();
            favorite=dest.readBoolean();
            accessTime=dest.readLong();
            permission=dest.readInt();
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
        dest.writeInt(childCount);
        dest.writeLong(modifyTime);
        dest.writeBoolean(accessible);
        dest.writeString(md5);
        dest.writeString(mime);
        dest.writeBoolean(favorite);
        dest.writeLong(accessTime);
        dest.writeInt(permission);
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
