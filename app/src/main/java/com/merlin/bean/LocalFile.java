package com.merlin.bean;

import android.os.Parcel;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.browser.Md5Reader;
import com.merlin.client.R;
import com.merlin.database.FileDB;

import java.io.File;

public final class LocalFile extends Document {
    private transient Reply<Path> sync;

    public LocalFile(String parent, String name, String extension, String title, String imageUrl, int childCount,
                     long length, long modifyTime, String md5, long accessTime, int permission) {
        super(null, parent, name, extension, title, imageUrl, childCount, length, modifyTime, md5, accessTime, permission);
    }

    public static LocalFile build(File file, String imageUrl, Md5Reader reader){
        Path path=null!=file?Path.build(file.getAbsolutePath(),null):null;
        if (null!=path){
            int permission=0x1111111;
            boolean directory=file.isDirectory();
            long size=file.length();
            String md5=null;
            int childCount= What.WHAT_NOT_DIRECTORY;
            if (directory){
                String[] names=file.list();
                childCount=null!=names?names.length:0;
            }else if(null!=reader){
                FileDB fileDB=reader.load(file,false);
                md5=null!=fileDB?fileDB.getMd5():null;
            }
            long modifyTime=file.lastModified();
            return new LocalFile(path.getParent(),path.getName(false),path.getExtension(),
                    file.getName(),imageUrl,childCount,size,modifyTime,md5,modifyTime,permission);
        }
        return null;
    }

    public java.io.File getFile(){
        String path= getPath(null);
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
        String md5=getMd5();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getHost());
        dest.writeString(getParent());
        dest.writeString(getName());
        dest.writeString(getExtension());
        dest.writeString(getTitle());
        dest.writeString(getImageUrl());
        dest.writeInt(getChildCount());
        dest.writeLong(getLength());
        dest.writeLong(getModifyTime());
        dest.writeString(getMd5());
        dest.writeString(getMime());
        dest.writeInt(isFavorite()?1:0);
        dest.writeLong(getAccessTime());
        dest.writeInt(getPermission());
    }

    private LocalFile(Parcel dest){
        if (null!=dest){
            String host=dest.readString();
            String parent=dest.readString();
            String name=dest.readString();
            String extension=dest.readString();
            setPath(host,parent,name,extension);
            String title=dest.readString();
            String imageUrl=dest.readString();
            int childCount=dest.readInt();
            long length=dest.readLong();
            long modifyTime=dest.readLong();
            String md5=dest.readString();
            String mime=dest.readString();
            boolean favorite=dest.readInt()==1;
            long accessTime=dest.readLong();
            int permission=dest.readInt();
            setFile(title,imageUrl,childCount,length,modifyTime,md5,mime,favorite,accessTime,permission);
        }
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
