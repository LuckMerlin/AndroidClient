package com.merlin.transport;

import com.merlin.bean.ClientMeta;

public final class Upload extends Transport {
    public Upload(String fromPath,String toFolder,String name,ClientMeta meta,Integer coverMode){
        super(fromPath,toFolder,name,meta,coverMode);
    }

}
