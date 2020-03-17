package com.merlin.socket;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.retrofit.Retrofit;
import com.merlin.util.Int;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Arrays;

public final class Frame implements Label{
    private final String data;
    private final String access;
    private final String encoding;
    private final String version;
    private final String unique;
    private final String key;
    private final String from;
    private byte[] body;
    private final String to;
    private double position;
    private final long length;
    public final static int LENGTH_BYTES_SIZE=4;

    public Frame(){
        this(0);
    }

    public Frame(long length){
        this(length,0,null,null,null,null,null,null,null,null);
    }

    public Frame(long length,long position,String to,String unique,String key,String
            data,byte[] body,String version,String access,String encoding){
        this.from=null;
        this.data=data;
        this.position=position;
        this.length=length;
        this.to=to;
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

    protected Frame setPosition(double position){
        this.position=position;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public boolean isTerminal(){
        return this.position==this.length;
    }

    public double getPosition() {
        return position;
    }

    public long getLength() {
        return length;
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

    public  Reply getDataReply(){
        try {
            String dataText=getData();
            dataText=null!=dataText&&dataText.length()>0?dataText.trim():null;
            return null!=dataText&&dataText.length()>2&&dataText.startsWith("{")&&dataText.endsWith("}")?
                    new Gson().fromJson(dataText, Reply.class):null;
        }catch (Exception e){
            Debug.E(getClass(),"E"+e,e);
        }
        return null;
    }

    public String getData(){
        return data;
    }

//    public String getData(String def){
//        return getData(null,def);
//    }
//
//    public String getData(String encoding, String def){
//        byte[] data=this.data;
//        if (data!=null&&data.length>0) {
//            encoding = null != encoding && encoding.length() > 0 ? encoding : this.encoding;
//            encoding = null != this.encoding && encoding.length() > 0 ? encoding : "utf-8";
//            try {
//                return new String(data,encoding);
//            } catch (UnsupportedEncodingException e) {
//                Debug.E(getClass(),"Can't get frame data as text.e="+e,e);
//                e.printStackTrace();
//            }
//        }
//        return def;
//    }

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
        put(json,LABEL_ACCESS,access).put(json,LABEL_ENCODING,encoding).
        put(json,LABEL_VERSION,version).put(json,LABEL_UNIQUE,unique).put(json,LABEL_KEY,key).put(json,LABEL_DATA,data)
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
                        frame=new Frame(headJson.optLong(LABEL_LENGTH),headJson.optLong(LABEL_POSITION),headJson.optString(LABEL_TO),
                                headJson.optString(LABEL_UNIQUE),headJson.optString(LABEL_KEY),headJson.optString(LABEL_DATA),body,
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
