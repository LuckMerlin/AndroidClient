package com.merlin.client;

import com.merlin.oksocket.LMSocket;

public final class Client extends LMSocket {

    public Client(String ip, int port){
        super(ip,port);
    }

    public boolean login(String account,String password){
        
        return false;
    }

    public boolean logout(){
        return false;
    }

}
