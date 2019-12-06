package com.merlin.client;

import com.merlin.oksocket.LMSocket;


public final class Client extends LMSocket {
    private String mAccount=null;

    public Client(String ip, int port){
        super(ip,port);
    }

    public boolean login(String account,String password){
//        meta = {TAG_ALTITUDE: 0 if latitude is None or not isinstance(latitude, float) else latitude,
//                TAG_LONGITUDE: 0 if longitude is None or not isinstance(longitude, float) else longitude,
//                TAG_ADDRESS: "" if address is None else address, TAG_DEVICE_TYPE: device_type, TAG_NAME:
//        "" if name is None else name, TAG_PLATFORM: "" if plat is None else plat}
//        if supports is not None and len(supports) > 0:
//        meta[TAG_SUPPORT] = supports
//        message = {TAG_ACCOUNT: account, TAG_PASSWORD: ('' if password is None else password), LABEL_META: meta}
        return false;
    }

    public boolean logout(){
        return false;
    }

}
