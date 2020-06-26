package com.merlin.bean;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.merlin.api.Reply;
import com.merlin.api.What;

import java.io.File;
import java.net.URI;
import java.net.URL;

public class IPath implements Parcelable {
    private Long  id;
    private String parent;
    private String name;
    private String extension;
    private String mime;
    private String host;
    private long length;
    private boolean favorite;
    private String md5;
    private int port;
    private String title;
    private Object thumb;

    public IPath(){
        this(null,null,null);
    }

    public IPath(String parent, String name, String extension){
        this(null,parent,name,extension);
    }

    public IPath(String host, String parent, String name, String extension){
        this.host=host;
        this.parent=parent;
        this.name=name;
        this.extension=extension;
    }

    public final boolean setFavorite(boolean favorite,String debug){
        this.favorite=favorite;
        return true;
    }

    public final void setHost(String host) {
        this.host = host;
    }

    public final String getExtension() {
        return extension;
    }

    public final String getParent() {
        return parent;
    }

    public final String getName() {
        return name;
    }

    public final String getName(boolean extension) {
        return null!=name&&extension&&null!=this.extension?name+this.extension:name;
    }

    public String getMime() {
        return mime;
    }

    public final String getHost() {
        return host;
    }

    public final String getHostName(){
        return null!=host&&host.length()>0?host+":"+port:null;
    }

    public final String getMd5() {
        return md5;
    }

    public final long getLength() {
        return length;
    }

    public boolean isFavorite() {
        return favorite;
    }

    /**
     * @deprecated
     */
    public final String getHostUrl(){
        return (null!=host?host:"")+":"+port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public final String getPath() {
        return getPath(null);
    }

    public boolean isLocal(){
        return null==id;
    }

    public final long getId() {
        return null!= id ?id:-1;
    }

    public String getTitle() {
        return title;
    }

    /**
     * @deprecated
     */
    public final boolean isExistHost(){
        String host=this.host;
        return null!=host&&host.length()>0;
    }

    /**
     * @deprecated
     */
    public final boolean isNetworkHost(){
        String host=this.host;
        return null!=host&&host.length()>0&&host.toLowerCase().startsWith("http://");
    }

    public final String getPath(String hostDivider) {
        String value=getName(true);
        String path=null!=parent&&null!=value?parent+value:null;
        String host=null!=hostDivider?this.host:null;
        return null!=host?host+(hostDivider.length()<=0?"/":hostDivider):path;
    }

    public int getPort() {
        return port;
    }

    public Object getThumb() {
        return thumb;
    }

    public final boolean applyPathChange(Reply<IPath> reply){
        if (null!=reply&&reply.isSuccess()&&reply.getWhat()== What.WHAT_SUCCEED){
            IPath path=reply.getData();
            return null!=path&&setPath(path.host,path.parent,path.name,path.extension);
        }
        return false;
    }

    protected final boolean setPath(String host,String parent,String name,String extension){
        this.host=host;
        this.parent=parent;
        this.name=name;
        this.extension=extension;
        return true;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof IPath){
            IPath file=(IPath)obj;
            return equals(file.parent,parent)&&equals(file.name,name)&&equals(file.extension,extension);
        }
        return super.equals(obj);
    }

    protected final boolean equals(String v1,String v2){
        return null!=v1&&null!=v2?v1.equals(v2):(null==v1&&null==v2);
    }

    public static <T extends IPath> IPath build(Object object, T result){
        IPath path=null;
        if (null!=object){
            if (object instanceof IPath){
                IPath objPath=(IPath)object;
                return new IPath(objPath.getHost(),objPath.getParent(),objPath.getName(false),objPath.getExtension());
            }
            if (object instanceof File){
                File file=(File)object;
                String parent=file.getParent();
                String name=file.getName();
                String extension=null;
                int length=null!=name?name.length():-1;
                int index=length>0?name.lastIndexOf("."):-1;
                if (index>0&&index<length){
                    extension=name.substring(index);
                    name=name.substring(0,index);
                }
                return new IPath(null,parent,name,extension);
            }
            if (null!=object){
                if (object instanceof String){
                    String value=(String)object;
                    int length=value.length();
                    int index=value.lastIndexOf(File.separator);
                    String parent=value;
                    String name=null;
                    if (index>0&&index+1<length){
                        parent=value.substring(0,index+1);
                        name=value.substring(index+1);
                    }
                    length=null!=parent?parent.length():-1;
                    index=null!=parent?parent.lastIndexOf(":"):-1;
                    String host=null;
                    if (index>0&&length>0&&index<length){
                        host=value.substring(0,index);
                        parent=value.substring(index);
                    }
                    String extension=null;
                    length=null!=name?name.length():-1;
                    index=length>0?name.lastIndexOf("."):-1;
                    if (index>0&&index<length){
                        extension=name.substring(index);
                        name=name.substring(0,index);
                    }
                    return new IPath(null!=host&&host.length()>0?host:null,parent,name,extension);
                }else if (object instanceof URL){
                    ((URL)object).getHost();
                }else if (object instanceof URI){

                }else if (object instanceof Uri){
                }
            }
        }
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parent);
        dest.writeString(name);
        dest.writeString(extension);
        dest.writeString(host);
        dest.writeLong(id==null?-2001:id);
        dest.writeInt(port);
    }

    protected IPath(Parcel parcel){
        if (null!=parcel) {
            parent = parcel.readString();
            name = parcel.readString();
            extension = parcel.readString();
            long idLong=parcel.readLong();
            id=(idLong==-2001)?null:idLong;
            host = parcel.readString();
            port = parcel.readInt();
        }
    }

    public static final Creator<IPath> CREATOR = new Creator<IPath>() {
        @Override
        public IPath createFromParcel(Parcel in) {
            return new IPath(in);
        }

        @Override
        public IPath[] newArray(int size) {
            return new IPath[size];
        }
    };

}
