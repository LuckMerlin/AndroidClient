package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.merlin.file.Permissions;
import com.merlin.player.Playable;

import static com.merlin.api.What.WHAT_NOT_DIRECTORY;

public class NasFile  implements FileMeta,Parcelable , Playable {
    private long id;
    private String md5;
    private String mime;
    private int mode;
    private double createTime;
    private int permissions;
    private long length;
    private double insertTime;
    private boolean favorite;
    private String extra;
    private String imageUrl;
    private NasMedia meta;
    private double accessTime;
    private String parent;
    private String name;
    private String title;
    private String extension;
    private long size;
    private double modifyTime;

    public int getMode() {
        return mode;
    }

    public long getLength() {
        return length;
    }

    public boolean applyModify(FileModify modify){
        if (null!=modify){
            String path=modify.getPath();
            String name=modify.getName();
            if (null!=path||null!=name){
//                this.path=path;
//                this.name=name;
                return true;
            }
        }
        return false;
    }

    public double getAccessTime() {
        return accessTime;
    }

    public double getCreateTime() {
        return createTime;
    }

    public String getMime() {
        return mime;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public NasMedia getMeta() {
        return meta;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getMd5() {
        return md5;
    }

    public double getInsertTime() {
        return insertTime;
    }

    public long getId() {
        return id;
    }

    @Override
    public double getModifyTime() {
        return modifyTime;
    }

    @Override
    public long getSize() {
        return size;
    }

    public String getExtra() {
        return extra;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return null!=parent&&null!=name?parent+name:null;
    }

    @Override
    public String getParent() {
        return parent;
    }

    @Override
    public boolean isAccessible(){
        if (getSize()== What.WHAT_NONE_PERMISSION){
            return false;
        }
        int permission=permissions;
        Permissions permissions=new Permissions();
        return permissions.isOtherReadable(permission)&&(!isDirectory()||permissions.isOtherExecutable(permission));
    }

    public final int getPermissions() {
        return permissions;
    }

    public final boolean isDirectory(){
        return getSize()!=WHAT_NOT_DIRECTORY;
    }

    @Override
    public String permission() {
        return Integer.toString(permissions);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj){
            if (obj instanceof String){
                String path=getPath();
                return null!=path&&path.equals(obj);
            }
        }
        return super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    private NasFile(Parcel in){
        id=in.readLong();
        length=in.readLong();
//        path=in.readString();
//        name=in.readString();
        md5=in.readString();
        mime=in.readString();
//        extension=in.readString();
        extra=in.readString();
        imageUrl=in.readString();
        createTime=in.readDouble();
//        modifyTime=in.readDouble();
        insertTime=in.readDouble();
        permissions=in.readInt();
//        size=in.readLong();
        favorite=in.readInt()==1;
        Parcelable parcelable=in.readParcelable(NasFile.class.getClassLoader());
        meta=null!=parcelable&&parcelable instanceof NasMedia ?(NasMedia)parcelable:null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(length);
//        dest.writeString(path);
//        dest.writeString(name);
        dest.writeString(md5);
        dest.writeString(mime);
//        dest.writeString(extension);
        dest.writeString(extra);
        dest.writeString(imageUrl);
        dest.writeDouble(createTime);
//        dest.writeDouble(modifyTime);
        dest.writeDouble(insertTime);
        dest.writeInt(permissions);
//        dest.writeLong(size);
        dest.writeInt(favorite?1:0);
        dest.writeParcelable(meta,0);

    }

    public static final Creator<NasFile> CREATOR = new Creator<NasFile>() {
        @Override
        public NasFile createFromParcel(Parcel in) {
            return new NasFile(in);
        }

        @Override
        public NasFile[] newArray(int size) {
            return new NasFile[size];
        }
    };


}
