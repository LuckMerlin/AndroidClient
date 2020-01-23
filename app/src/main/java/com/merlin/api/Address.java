package com.merlin.api;

public interface Address {
    //        mUrl=null!=url&&url.length()>=0?url:"http://192.168.0.3:2008";
//    mUrl=null!=url&&url.length()>=0?url:"http://172.16.20.215:2008";
//    String URL="http://172.16.20.215:2008";
//    String URL="http://192.168.0.6:2008";
    String URL="http://7091b467.cpolar.io";
//    String URL="http://792bcd1.cpolar.io";
    String PREFIX_USER="/user";
    String PREFIX_MEDIA="/media";
    String PREFIX_FILE="/file";
    String PREFIX_FILE_BROWSER=PREFIX_FILE+"/directory/browser";
    String PREFIX_MEDIA_PLAY=PREFIX_MEDIA+"/play";
}
