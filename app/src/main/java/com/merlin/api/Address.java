package com.merlin.api;

public interface Address {
    //        mUrl=null!=url&&url.length()>=0?url:"http://192.168.0.3:2008";
//    mUrl=null!=url&&url.length()>=0?url:"http://172.16.20.215:2008";
//    String URL="http://172.16.20.215:2008";
//    String URL="http://192.168.0.6:2008";
//    String URL="http://106.12.163.77:2020";
    String URL="http://172.16.20.207:2008";
//    String URL="http://53971a7b.cpolar.io";
//    String URL="http://792bcd1.cpolar.io";
    String PREFIX_USER="/user";
    String PREFIX_MEDIA="/media";
    String PREFIX_FILE="/file";
    String PREFIX_IMAGE="/image";
    String PREFIX_USER_REBOOT=PREFIX_USER+"/reboot";
    String PREFIX_FILE_BROWSER=PREFIX_FILE+"/directory/browser";
    String PREFIX_FILE_CLIENT_META=PREFIX_FILE+"/client/meta";
    String PREFIX_MEDIA_PLAY=PREFIX_MEDIA+"/play";
    String PREFIX_THUMB=PREFIX_IMAGE+"/thumbs";
}
