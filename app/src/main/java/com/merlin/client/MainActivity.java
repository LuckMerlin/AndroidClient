package com.merlin.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.merlin.debug.Debug;
import com.merlin.oksocket.FrameParser;
import com.merlin.server.Frame;


public class MainActivity extends Activity implements FrameParser.OnFrameParseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Client server = new Client("www.luckmerlin.com", 5005);
        server.connect();
    }

    @Override
    public void OnFrameParsed(Frame frame) {
        Debug.D(getClass(),"shouda "+frame.getCode()+" "+frame);
    }

    public void ddd(View view){
        System.exit(1);
    }


}
