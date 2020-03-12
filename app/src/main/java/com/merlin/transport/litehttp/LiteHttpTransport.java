package com.merlin.transport.litehttp;

import android.content.Intent;
import android.util.Log;

import com.merlin.api.Address;
import com.merlin.api.What;
import com.merlin.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import alexclin.httplite.HttpLite;
import alexclin.httplite.HttpLiteBuilder;
import alexclin.httplite.LiteClient;
import alexclin.httplite.okhttp3.Ok3Lite;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.BufferedSink;

public class LiteHttpTransport implements What {
    private final OkHttpClient mHttpLite;

    public interface Callback{

    }

    public LiteHttpTransport(){
        OkHttpClient.Builder builder=new OkHttpClient.Builder();
        builder.writeTimeout(Integer.MAX_VALUE-1, TimeUnit.MILLISECONDS);
        builder.readTimeout(Integer.MAX_VALUE-1, TimeUnit.MILLISECONDS);
        OkHttpClient okHttpClient=mHttpLite=builder.build();
    }

    public int upload(String filePath, String name,String cloudUrl,String cloudFolder){
        if (null!=filePath&&filePath.length()>0&&null!=cloudUrl&&cloudUrl.length()>0){
            final File file=new File(filePath);
            if (!file.exists()){
                 Debug.W(getClass(),"Can't upload file with not exist file "+filePath);
                 return WHAT_FILE_NOT_EXIST;
            }else if (!file.canRead()){
                Debug.W(getClass(),"Can't upload file without read permission "+filePath);
                return WHAT_NONE_PERMISSION;
            }
            OkHttpClient client=mHttpLite;
            if (null!=client){
                Request request = new Request.Builder().url("http://172.16.20.210:2009/wechat").build();
                client.newWebSocket(request,new WebSocketListener(){
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        super.onClosed(webSocket, code, reason);
                    }
                });
//                UploadBody uploadBody=new UploadBody(file);
//                Call call = client.newCall(new Request.Builder().url(cloudUrl).post(uploadBody).build());
//                call.enqueue(new okhttp3.Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        Debug.D(getClass(),"失败的 "+e+" call="+call);
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        Debug.D(getClass(),"onResponse "+call+" response="+response);
//                    }
//                });
//                Request.Builder builder1=new Request.Builder();
//                Debug.D(getClass(),"$$$$$$$aa$$$$$ 请求 "+cloudUrl);
//                builder1.baseUrl(cloudUrl+"/file/upload").post();
//                Debug.D(getClass(),"$$$$$$$$$$$$ 请求 "+cloudUrl);
//                httpLite.enqueue(builder1.build(),null);
                return WHAT_SUCCEED;
            }
            return WHAT_ERROR_UNKNOWN;
        }
        Debug.W(getClass(),"Can't upload file with invalid path "+filePath);
        return WHAT_ARGS_INVALID;
    }
}
