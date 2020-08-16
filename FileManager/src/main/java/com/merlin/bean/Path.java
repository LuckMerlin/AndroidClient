package com.merlin.bean;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.util.FileSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Path implements Parcelable {
    private final static String LOCAL_HOST="localDevice";
    private Path thumb;
    private String parent;
    private String name;
    private String title;
    private String pathSep;
    private String host;
    private int port;
    private int permissions;
    private long size;
    private String extension;
    private String mime;
    private long length;
    private long accessTime;
    private long modifyTime;
    private long createTime;
    private String md5;
    private double total;
    private double free;

    public static Path build(File file){
        if (null!=file&&file.exists()){
            Path path=new Path();
            long total=file.getTotalSpace();
            path.total=total;
            path.free=total>0?total-file.getFreeSpace():0;
            String parent=file.getParent();
            parent=null!=parent&&!parent.endsWith(File.separator)?parent+File.separator:parent;
            path.pathSep=File.separator;
            path.parent=parent;
            String name=file.getName();
            path.title=name;
            path.name=name;
            int extensionIndex=null!=name&&name.length()>1?name.indexOf("."):-1;
            if (extensionIndex>0){
                path.name = name.substring(0,extensionIndex);
                path.extension = name.substring(extensionIndex);
            }
            path.modifyTime = file.lastModified();
            path.length= file.length();
            boolean directory=file.isDirectory();
            String[] children=directory?file.list():null;
            path.size = directory?null!=children?children.length:0:What.WHAT_NOT_DIRECTORY;
            path.host=LOCAL_HOST;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    path.mime= Files.probeContentType(Paths.get(file.getAbsolutePath()));
                } catch (IOException e) {
                    //Do nothing
                }
            }else{

            }
            return path;
        }
        return null;
    }

    public final String getName(boolean includeExtension) {
        String ext=includeExtension?extension:null;
        return null!=ext&&ext.length()>0?(null!=name?name+ext:ext):name;
    }

    public final String getName() {
        return getName(true);
    }

    public String generateChildPath(String childName) {
        String path=isDirectory()&&null!=childName&&childName.length()>0?getPath():null;
        String pathSep=null!=path&&path.length()>0?this.pathSep:null;
        return null!=pathSep?path+pathSep+childName:null;
    }

    public String getParent() {
        return parent;
    }

    public int getPermissions() {
        return permissions;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public long getSize() {
        return size;
    }

    public final String getExtension() {
        return extension;
    }

    public final String getMime() {
        return mime;
    }

    public final long getLength() {
        return length;
    }

    public final long getAccessTime() {
        return accessTime;
    }

    public final long getModifyTime() {
        return modifyTime;
    }

    public final long getCreateTime() {
        return createTime;
    }

    public final String getMd5() {
        return md5;
    }

    public final boolean isDirectory(){
        return size>=0;
    }

    public final String getHostUri(){
        return getHostUri(null);
    }

    public final String getHostUri(String protocol){
        String host=this.host;
        int port=this.port;
        return null!=host&&host.length()>0?(null!=protocol?protocol:"http://")+host+":"+port:null;
    }

    public final String getUri(){
        return getUri("");
    }

    public String getPathSep() {
        return pathSep;
    }

//    public void setPathSep(String pathSep) {
//        this.pathSep = pathSep;
//    }

    public final String getUri(String divider){
        String path=getPath();
        String hostUri=getHostUri();
        return (null!=hostUri&&hostUri.length()>0)?hostUri+(null!=divider?divider:"/")+(null!=path?path:""):null;
    }

    public final String getPath() {
        String parent=this.parent;
        String name=this.name;
        String extension=this.extension;
        return (null!=parent?parent:"")+(null!=name?name:"")+(null!=extension?extension:"");
    }

    public final boolean isAccessible() {
        return true;
    }

    public final boolean isLocal() {
        String host=this.host;
        return null!=host&&host.equals(LOCAL_HOST);
    }

    public String getVolume(){
        long size=getSize();
        return size==What.WHAT_NOT_DIRECTORY? FileSize.formatSizeText(getLength()):""+size;
    }

    public final boolean applyNameChange(Reply<Path> reply){
        Path data=null!=reply&&reply.isSuccess()&&reply.getWhat()==What.WHAT_SUCCEED?reply.getData():null;
        if (null!=data){
            name=data.name;
            return true;
        }
        return false;
    }

//    public void setTotal(double total) {
//        this.total = total;
//    }
//
//    public void setFree(double free) {
//        this.free = free;
//    }

    public double getTotal() {
        return total;
    }

    public double getFree() {
        return free;
    }


    public Path getThumb() {
        return thumb;
    }

//    public int syncColor(){
//        int color= R.color.syncNull;
//        String md5=getMd5();
//        if (null!=md5&&md5.length()>0){
//            color=R.color.syncNeed;
//            Reply<Path> sync=mSync;
//            if (null!=sync){
//                color=R.color.syncFail;
//                if (sync.isSuccess()&&sync.getWhat()== What.WHAT_SUCCEED){
//                    Path syncUrl=sync.getData();
//                    String path=null!=syncUrl?syncUrl.getPath():null;
//                    color=null!=path&&path.length()>=0?R.color.synced:R.color.syncedNone;
//                }
//            }
//        }
//        return color;
//    }

    public String getTitle() {
        return title;
    }

//    public Reply<Path> getSync() {
//        return mSync;
//    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(host);
        dest.writeString(parent);
        dest.writeString(name);
        dest.writeString(extension);
        dest.writeString(mime);
        dest.writeString(md5);
        dest.writeString(title);
        dest.writeString(pathSep);
        dest.writeLong(accessTime);
        dest.writeLong(createTime);
        dest.writeLong(modifyTime);
        dest.writeLong(length);
        dest.writeLong(size);
        dest.writeInt(permissions);
        dest.writeInt(port);
    }

    private Path(){
        this(null);
    }

    private Path(Parcel parcel){
        if (null!=parcel){
            host=parcel.readString();
            parent=parcel.readString();
            name=parcel.readString();
            extension=parcel.readString();
            mime=parcel.readString();
            md5=parcel.readString();
            title=parcel.readString();
            pathSep=parcel.readString();
            accessTime=parcel.readLong();
            createTime=parcel.readLong();
            modifyTime=parcel.readLong();
            length=parcel.readLong();
            size=parcel.readLong();
            permissions=parcel.readInt();
            port=parcel.readInt();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>(){
        @Override
        public Path createFromParcel(Parcel source) {
            return new Path(source);
        }

        @Override
        public Path[] newArray(int size) {
            return new Path[size];
        }
    };

}
