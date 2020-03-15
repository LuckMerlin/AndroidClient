package com.merlin.socket;

import com.merlin.api.Label;
import com.merlin.debug.Debug;
import com.merlin.util.Int;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public final class Frame implements Label{
    public final static String FORMAT_TEXT="text";
    public final static String FORMAT_BYTES="bytes";
    private final String access;
    private final String format;
    private final String encoding;
    private final String version;
    private final String unique;
    private final String key;
    private final String from;
    private byte[] body;
    private final String to;
    private long position;
    private final long length;
    public final static int LENGTH_BYTES_SIZE=4;

    public Frame(){
        this(0);
    }

    public Frame(long length){
        this(length,0,null,null,null,null,null,null,null,null);
    }

    public Frame(long length,long position,String to,String format,String unique,String key,byte[] body,String version,String access,String encoding){
        this.from=null;
        this.position=position;
        this.length=length;
        this.to=to;
        this.format=format;
        this.unique=unique;
        this.key=key;
        this.body=body;
        this.version=version;
        this.encoding=encoding;
        this.access=access;
    }

    public byte[] getBody() {
        return body;
    }

    public String getKey() {
        return key;
    }

    public String getTo() {
        return to;
    }

    public Frame setBody(byte[] body){
        this.body=body;
        return this;
    }

    public Frame setPosition(long position){
        this.position=position;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public boolean isTerminal(){
        Debug.D(getClass(),"aaa "+this.position+" "+this.length);
        return this.position==this.length;
    }

    public long getPosition() {
        return position;
    }

    public long getLength() {
        return length;
    }

    public String getFormat() {
        return format;
    }

    public String getUnique() {
        return unique;
    }

    public String getVersion() {
        return version;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getAccess() {
        return access;
    }

    public String getBodyText(String def){
        return getBodyText(null,def);
    }

    public String getBodyText(String encoding, String def){
        byte[] data=this.body;
        if (data!=null&&data.length>0) {
            encoding = null != encoding && encoding.length() > 0 ? encoding : this.encoding;
            encoding = null != this.encoding && encoding.length() > 0 ? encoding : "utf-8";
            try {
                return new String(data,encoding);
            } catch (UnsupportedEncodingException e) {
                Debug.E(getClass(),"Can't get frame data as text.e="+e,e);
                e.printStackTrace();
            }
        }
        return def;
    }

    private Frame put(JSONObject json,String key,Object value){
        if (null!=json&&null!=key&&null!=value){
            try {
                json.put(key,value);
            } catch (JSONException e) {
                Debug.E(getClass(),"Exception put frame value into json.e="+e,e);
                e.printStackTrace();
            }
        }
        return this;
    }

    public byte[] toFrameBytes(){
        JSONObject json=new JSONObject();
        put(json,LABEL_ACCESS,access).put(json,LABEL_FORMAT,format).put(json,LABEL_ENCODING,encoding).
        put(json,LABEL_VERSION,version).put(json,LABEL_UNIQUE,unique).put(json,LABEL_KEY,key)
                .put(json,LABEL_TO,to).put(json,LABEL_POSITION, position).put(json,LABEL_LENGTH, length);
        String headText=null!=json&&json.length()>0?json.toString():null;
        try {
            byte[] headBytes=null!=headText&&headText.length()>0?headText.getBytes("utf-8"):null;
            int headLength=null!=headBytes?headBytes.length:0;
            byte[] body=this.body;
            int bodyLength=null!=body?body.length:0;
            int lengthBytesEndpoint=LENGTH_BYTES_SIZE*2;
            byte[] result=new byte[lengthBytesEndpoint+headLength+bodyLength];
            Int.toByteArray(headLength,result,0);
            Int.toByteArray(bodyLength,result,LENGTH_BYTES_SIZE);
            if (headLength>0){
                System.arraycopy(headBytes,0,result,lengthBytesEndpoint,headLength);
            }
            if (bodyLength>0){
                System.arraycopy(body,0,result,lengthBytesEndpoint+headLength,bodyLength);
            }
            return result;
        } catch (UnsupportedEncodingException e) {
            Debug.E(getClass(),"Exception frame to bytes.e="+e,e);
            e.printStackTrace();
        }
        return null;
    }

    public static Frame read(byte[] buffer,int bodyStartIndex,OnFrameReceive onReceive){
        Frame frame=null;
            final int length=null!=buffer?buffer.length:-1;
            if (bodyStartIndex>=0){
                if (bodyStartIndex>0){//Empty frame head
                    JSONObject headJson=null;
                    try {
                        String headText=new String(buffer,0, bodyStartIndex,"utf-8");
                        headJson=null!=headText&&headText.length()>0?new JSONObject(headText):null;
                    } catch (Exception e) {
                        Debug.E(Frame.class,"Exception read frame head text.e="+e,e);
                        e.printStackTrace();
                    }
                    byte[] body=bodyStartIndex<=length?Arrays.copyOfRange(buffer,bodyStartIndex,length):null;
                    if (null!=headJson&&headJson.length()>0){
                        frame=new Frame(headJson.optLong(LABEL_LENGTH),headJson.optLong(LABEL_POSITION),headJson.optString(LABEL_TO),headJson.optString(LABEL_FORMAT),
                                headJson.optString(LABEL_UNIQUE),headJson.optString(LABEL_KEY),body,
                                headJson.optString(LABEL_VERSION),headJson.optString(LABEL_ACCESS),null);
                    }else{
                        frame=new Frame().setBody(body);
                    }
                }else if (length==0&&bodyStartIndex==0){ //Empty Frame
                    frame=new Frame();
                }
                if (null!=frame&&null!=onReceive){
                    onReceive.OnFrameReceived(frame);
                }
            }
            return frame;
    }
}
