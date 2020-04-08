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

public final class LocalFile extends File {
    private transient Reply<Path> sync;

    public LocalFile(String parent,String name,String extension,String title,String imageUrl,int childCount,
                     long length,long modifyTime,boolean accessible,String md5,long accessTime){
        super(null,parent,name,extension,title,imageUrl,childCount,length,modifyTime,accessible,md5,accessTime);
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
            return new LocalFile(parent,name,extension,title,imageUrl,childCount,size,
                    modifyTime,accessible,md5,modifyTime);
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
        dest.writeBoolean(isAccessible());
        dest.writeString(getMd5());
        dest.writeString(getMime());
        dest.writeBoolean(isFavorite());
        dest.writeLong(getAccessTime());
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
            boolean accessible=dest.readBoolean();
            String md5=dest.readString();
            String mime=dest.readString();
            boolean favorite=dest.readBoolean();
            long accessTime=dest.readLong();
            setFile(title,imageUrl,childCount,length,modifyTime,accessible,md5,mime,favorite,accessTime);
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
