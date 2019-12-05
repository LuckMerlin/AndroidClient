package com.merlin.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.merlin.oksocket.FrameParser;
import com.merlin.oksocket.LMSocket;
import com.merlin.server.Frame;


public class MainActivity extends Activity implements FrameParser.OnFrameParseListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LMSocket server = new LMSocket("www.luckmerlin.com", 5005);
        server=null;
        server.connect(new FrameParser(this));
    }

    @Override
    public void OnFrameParsed(Frame frame) {

    }

    public void ddd(View view){
        System.exit(1);
    }


}
