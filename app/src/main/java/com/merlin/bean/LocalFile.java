package com.merlin.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.Nullable;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.browser.Md5Reader;
import com.merlin.client.R;
import com.merlin.database.FileDB;
import com.merlin.debug.Debug;

public final class LocalFile implements Parcelable,FileMeta {
    private String parent;
    private String name;
    private String title;
    private String extension;
    private String imageUrl;
    private long length;
    private int childCount;
    private double modifyTime;
    private boolean directory;
    private boolean accessible;
    private String md5;
    private Reply<Path> sync;

    public LocalFile(String parent,String title,String name,String extension,
                     String imageUrl,int childCount,long length,long modifyTime, boolean directory,
                     boolean accessible,String md5){
        this.parent=parent;
        this.title=title;
        this.name=name;
        this.childCount=childCount;
        this.extension=extension;
        this.imageUrl=imageUrl;
        this.length=length;
        this.modifyTime=modifyTime;
        this.directory=directory;
        this.accessible=accessible;
        this.md5=md5;
    }

    public static LocalFile create(java.io.File file, String imageUrl){
            return create(file,imageUrl,null);
    }

    public static LocalFile create(java.io.File file, String imageUrl, Md5Reader reader){
        if (null!=file){
            String parent=file.getParent();
            parent=null!=parent?parent+ java.io.File.separator:null;
            String[] fix=getPostfix(file);
            String name=fix[0];
            String extension=fix[1];
            String title=file.getName();
            String md5=null;
            boolean directory=file.isDirectory();
            long size=file.length();
            int childCount= What.WHAT_NOT_DIRECTORY;
            if (directory){
                String[] names=file.list();
                childCount=null!=names?names.length:0;
            }else if(null!=reader){
                FileDB fileDB=reader.load(file,false);
                md5=null!=fileDB?fileDB.getMd5():null;
            }
            long modifyTime=file.lastModified();
            boolean accessible=file.canRead()&&(!file.isDirectory()||file.canExecute());
            return new LocalFile(parent,title,name,extension,imageUrl,childCount,size,modifyTime,
                    directory,accessible,md5);
        }
        return null;
    }

    public java.io.File getFile(){
        String path= getPath(false);
        return null!=path&&path.length()>0?new java.io.File(path):null;
    }

    public static String[] getPostfix(java.io.File file){
        String name=null!=file?file.getName():null;
        int index=null!=name?name.lastIndexOf("."):-1;
        String[] result=new String[2];
        if (index>0){
            result[0]=name.substring(0,index);
            result[1]=name.substring(index);
        }else{
            result[0]=name;
        }
        return result;
    }

    public Reply<Path> getSync() {
        return sync;
    }

    public int syncColor(){
        int color=R.color.syncNull;
        if (null!=md5&&md5.length()>0){
            color=R.color.syncNeed;
            if (null!=sync){
                color=R.color.syncFail;
                if (sync.isSuccess()&&sync.getWhat()==What.WHAT_SUCCEED){
                    Path syncUrl=sync.getData();
                    String path=null!=syncUrl?syncUrl.getPath():null;
                    color=null!=path&&path.length()>=0?R.color.synced:R.color.syncedNone;
                }
            }
        }
        return color;
    }

    public boolean applySync(Reply<Path> sync){
        this.sync=sync;
        return true;
    }


    @Override
    public boolean applyChange(Reply<Path> reply) {
        Path path=null!=reply&&reply.isSuccess()&&reply.getWhat()==What.WHAT_SUCCEED?reply.getData():null;
        if (null!=path){
            parent=path.getParent();
            name=path.getName(false);
            extension=path.getExtension();
            return true;
        }
        return false;
    }

    /**
     * @deprecated
     */
    public boolean applyModify(FModify modify) {
        if (null!=modify){
            String name=modify.getName();
            if (null!=name){
                this.name=name;
            }
            String extension=modify.getExtension();
            if (null!=extension){
                this.extension=extension;
            }
            return true;
        }
        return false;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getParent() {
        return parent;
    }

    public String getName(boolean extension) {
        return null!=name&&extension&&null!=this.extension?name+this.extension:name;
    }

    public String getTitle() {
        return title;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public long getLength() {
        return length;
    }

    public double getModifyTime() {
        return modifyTime;
    }

    @Override
    public String getPath(boolean host) {
        String value=getName(true);
        return null!=parent&&null!=value?parent+value:null;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public boolean isDirectory() {
        return directory;
    }

    public String permission() {
        return "";
    }

    public String getMd5() {
        return md5;
    }

    @Override
    public Object onViewData(int what, View view, String debug) {
        switch (what){
            case DATA_LOADED:
                Debug.D(getClass(),"AAAAAAAAAAAA "+view+" "+debug);

                break;
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof LocalFile){
            LocalFile file=(LocalFile)obj;
            return equals(file.parent,parent)&&equals(file.name,name)&&equals(file.extension,extension);
        }
        return super.equals(obj);
    }

    private boolean equals(String v1,String v2){
        return null!=v1&&null!=v2?v1.equals(v2):(null==v1&&null==v2);
    }

    @Override
    public int getChildCount() {
        return childCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parent);
        dest.writeString(name);
        dest.writeString(title);
        dest.writeString(extension);
        dest.writeString(imageUrl);
        dest.writeLong(length);
        dest.writeInt(childCount);
        dest.writeDouble(modifyTime);
        dest.writeBoolean(directory);
        dest.writeBoolean(accessible);
    }

    private LocalFile(Parcel in){
        parent=in.readString();
        name=in.readString();
        title=in.readString();
        extension=in.readString();
        imageUrl=in.readString();
        length=in.readLong();
        childCount=in.readInt();
        modifyTime=in.readDouble();
        directory=in.readBoolean();
        accessible=in.readBoolean();
    }

    public static final Creator<LocalFile> CREATOR = new Creator<LocalFile>() {
        public LocalFile createFromParcel(Parcel in) {
            return new LocalFile(in);
        }

        @Override
        public LocalFile[] newArray(int size) {
            return new LocalFile[size];
        }
    };

}
