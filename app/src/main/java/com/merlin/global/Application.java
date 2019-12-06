package com.merlin.global;

import com.merlin.client.Client;

public class Application extends android.app.Application {
   private final Client client = new Client("www.luckmerlin.com", 5005);

    @Override
    public void onCreate() {
        super.onCreate();
//        client.connect();
    }

}
