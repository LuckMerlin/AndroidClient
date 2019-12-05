package com.merlin.oksocket;

import com.merlin.server.Frame;

public final class FrameParser {

    public interface OnFrameParseListener{
        void OnFrameParsed(Frame frame);
    }

    private OnFrameParseListener  mListener;

    public FrameParser(OnFrameParseListener listener){
        mListener=listener;
    }

    public void onFrameBytesReceived(byte[] bytes,byte[] body){
           Frame frame= Frame.buildFromBytes(bytes,body);
           if (null!=frame&&null!=mListener){
               mListener.OnFrameParsed(frame);
           }

    }
}
