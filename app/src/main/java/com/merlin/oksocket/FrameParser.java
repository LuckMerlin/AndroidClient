package com.merlin.oksocket;

import com.merlin.debug.Debug;
import com.merlin.server.Frame;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class FrameParser {

    public interface OnFrameParseListener{
        void OnFrameParsed(Frame frame);
    }

    private OnFrameParseListener  mListener;

    public FrameParser(OnFrameParseListener listener){
        mListener=listener;
    }

    public void onFrameReceived(byte[] bytes,byte[] body){
        HeadReader head=null!=bytes? HeadReader.read(bytes,true):null;
        if (null!=head){
            String encoding=head.getEncodingName();
            encoding=null!=encoding&&encoding.length()>0?encoding:"utf-8";
//            Protocol.dumpBytes(bytes,true);
            int headSize=head.getHeadSize();
            int contentSize=head.getContentSize();
            int total=headSize+contentSize;
            int size=null!=body?body.length:-1;
            if(size<total){
               return;//Invalid
            }
            byte[] headBytes= headSize >0 && headSize<= size?Arrays.copyOfRange(body,0,headSize):null;
            byte[] bodyBytes= headSize >0 && total< size?Arrays.copyOfRange(body,headSize,total):null;
            String headString=null;
            try {
                headString=null!=headBytes&&headBytes.length>0?new String(headBytes,encoding):null;
            } catch (UnsupportedEncodingException e) {
                Debug.E(getClass(),"Can't parse frame head data.e="+e+" encoding="+encoding,e);
                e.printStackTrace();
            }
            Frame json=null!=headString? Frame.buildFromJson(headString):null;
            Debug.D(getClass(),"地方撒旦 "+json+" "+headSize);
        }
    }
}
