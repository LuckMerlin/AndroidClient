package com.merlin.bean;

import android.content.Context;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StatFs;

import androidx.annotation.NonNull;

import com.merlin.client.R;
import com.merlin.debug.Debug;

import java.io.Serializable;

public final class ClientMeta implements Parcelable {
    /**
     * account : nas
     * timestamp : 1.5761359971479676E9
     * altitude : 0
     * longitude : 0
     * address :
     * deviceType : nasDevice
     * name : DESKTOP-1CGFIVE
     * platform : win32
     */
    private String url;
    private String account;
    private String deviceType;
    private String name;
    private String imageUrl;
    private String platform;
    private String folder;
    private long free;
    private long total;
    private final static String LOCAL_URL="http://127.0.0.1";

    /**
     * @deprecated
     */
    public ClientMeta(String name,String url,String account,String imageUrl,String folder){
        this.name=name;
        this.url=url;
        this.account=account;
        this.imageUrl=imageUrl;
        this.folder=folder;
    }

    public static ClientMeta buildLocalClient(Context context){
        ClientMeta meta=new ClientMeta(null);
        meta.url=LOCAL_URL;
        meta.platform="Android";
        meta.deviceType="Mobile";
        meta.name=null!=context?context.getString(R.string.local):"Local";
        meta.account="Local";
        String path = Environment.getDataDirectory().getPath();
        if (null!=path&&path.length()>0){
            StatFs statFs = new StatFs(path);
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long availableBlocks = statFs.getAvailableBlocks();
            long rom_length = totalBlocks*blockSize;
            meta.total=rom_length;
            meta.free=availableBlocks*blockSize;
        }
        return meta;
    }

    public String getAccount() {
        return account;
    }

    public boolean isLocalClient(){
        String urlValue=url;
        return null!=urlValue&&urlValue.equals(LOCAL_URL);
    }

    public String getDeviceType() {
        return deviceType;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public long getTotal() {
        return total;
    }

    public long getFree() {
        return free;
    }

    public String getUrl() {
        return url;
    }

    public boolean isDeviceType(String type){
        String deviceType=null!=type?this.deviceType:null;
        return null!=deviceType&&deviceType.equals(type);
    }

    public String getFolder() {
        return folder;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return ""+url+" "+super.toString();
    }

    private ClientMeta(Parcel in){
        if (null!=in) {
            this.url = in.readString();
            this.account = in.readString();
            this.deviceType = in.readString();
            this.name = in.readString();
            this.imageUrl = in.readString();
            this.platform = in.readString();
            this.free = in.readLong();
            this.total = in.readLong();
            this.folder=in.readString();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(account);
        dest.writeString(deviceType);
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(platform);
        dest.writeLong(free);
        dest.writeLong(total);
        dest.writeString(folder);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ClientMeta> CREATOR = new Creator<ClientMeta>() {
        @Override
        public ClientMeta createFromParcel(Parcel in) {
            return new ClientMeta(in);
        }

        @Override
        public ClientMeta[] newArray(int size) {
            return new ClientMeta[size];
        }
    };

}
