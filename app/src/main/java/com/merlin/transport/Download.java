package com.merlin.transport;

import com.merlin.bean.ClientMeta;

public final class Download extends Transport{

    public Download(String fromPath,String toFolder,String name, ClientMeta client, Integer coverMode){
        super(fromPath,toFolder,name,client,coverMode);
    }

}
