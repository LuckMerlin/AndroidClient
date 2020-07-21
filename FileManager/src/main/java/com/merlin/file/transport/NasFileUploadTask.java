package com.merlin.file.transport;

import com.merlin.api.Label;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.task.file.HttpUploadTask;
import com.task.debug.Debug;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class NasFileUploadTask extends HttpUploadTask {

    public NasFileUploadTask(String from, Path toFolder,String name) {
        this(from,null!=toFolder&&toFolder.isDirectory()?toFolder.getHostUri():null,
                null!=toFolder&&toFolder.isDirectory()?toFolder.getPath():null,name);
    }

    private NasFileUploadTask(String from,String toUri,String toFolder,String toName){
        super(from,null!=toUri?toUri+"/file/upload":null,toFolder,toName);
    }

    @Override
    protected HttpURLConnection createHttpConnect(String urlPath, String method) throws IOException {
        HttpURLConnection connection=super.createHttpConnect(urlPath,method);
        if (null!=connection){
            connection.setRequestProperty(Label.LABEL_FOLDER,getToFolder());
            connection.setRequestProperty(Label.LABEL_NAME, getToName());
        }
        return connection;
    }

    @Override
    protected Long onResolveBreakPoint(File file) throws Exception {
        HttpURLConnection connection=createHttpConnect(getTo(),GET);
        if (null==connection){
            throw new IllegalAccessException("Exception create connection to resolve upload break point.");
        }
        connection.setDoInput(true);
        connection.connect();
        int responseCode=connection.getResponseCode();
        if (responseCode!=HttpURLConnection.HTTP_OK){
            Debug.D("Can't resolve upload break point while HTTP response code NOT ok.code="+responseCode);
            return null;
        }
        String responseMessage=connection.getResponseMessage();
        Debug.D("Response message "+responseMessage+" "+responseCode);
        InputStream inputStream=null!=connection?connection.getInputStream():null;
        BufferedReader reader=null;
        try {
            reader = null!=inputStream?new BufferedReader(new InputStreamReader(inputStream)):null;
            StringBuffer buffer=new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String response=buffer.toString();
            JSONObject json=null!=response&&response.length()>0&&response.startsWith("{")&&response.endsWith("}")?new JSONObject(response):null;
            if (null!=json){//{"note": "File path invalid", "what": -1005, "success": true, "data": null}
                boolean success=json.optBoolean(Label.LABEL_SUCCESS, false);
                int what=json.optInt(Label.LABEL_WHAT, What.WHAT_ERROR_UNKNOWN);
                if (success){
                    if (what== What.WHAT_NOT_EXIST||what== What.WHAT_FILE_NOT_EXIST){
                        return 0l;
                    }
                    JSONObject data=json.optJSONObject(Label.LABEL_DATA);
                    return null!=data?data.optLong(Label.LABEL_LENGTH, 0):null;
                }
            }
        }catch (Exception e){
            Debug.E("Exception while resolve upload break point.e="+e,e);
            e.printStackTrace();
        }finally {
            close(inputStream,reader);
            connection.disconnect();
        }
        return null;
    }

}
