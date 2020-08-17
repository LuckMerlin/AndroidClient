package com.merlin.file.transport;

import com.merlin.api.Label;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.task.Networker;
import com.merlin.task.Status;
import com.merlin.task.file.FileProgress;
import com.merlin.task.file.HttpUploadTask;
import com.task.debug.Debug;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class FileUploadNasTask extends HttpUploadTask {
    private Integer mMode;

    public FileUploadNasTask(String name, String from, Path toFolder, String toName,Integer mode) {
        this(name,from,null!=toFolder&&toFolder.isDirectory()?toFolder.getHostUri():null,
                null!=toFolder&&toFolder.isDirectory()?toFolder.getPath():null,toName,mode);
    }

    private FileUploadNasTask(String name, String from, String toUri, String toFolder, String toName,Integer mode){
        super(name,from,null!=toUri?toUri+"/file/upload":null,toFolder,toName);
        mMode=mode;
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

    private Long onResolveBreakPoint(File file) throws Exception {
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
            Debug.D("QQQQQQQQQQQQQ "+response);
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

    @Override
    protected void onExecute(Networker networker) {
        String toUriPath=getTo();
        String fromPath=getFrom();
        if (null==toUriPath||toUriPath.length()<=0||null==fromPath||fromPath.length()<=0){
            Debug.W("Can't upload file while args invalid.");
            notifyStatus(Status.FINISH,"Upload args invalid");
            return;
        }
        final File fromFile=new File(fromPath);
        final long fileLength=fromFile.exists()?fromFile.length():-1;
        if (fileLength<=0){
            Debug.W("Can't upload file while file is EMPTY or NOT exist.");
            notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_EMPTY,"File is EMPTY or NOT exist "+fromPath);
            return;
        }
        if (!fromFile.canRead()){
            Debug.W("Can't upload file while file none read permission.");
            notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_NONE_PERMISSION,"NONE read permission "+fromPath);
            return;
        }
        OutputStream outputStream=null;
        DataInputStream inputStream=null;
        BufferedReader bufferedReader=null;
        try {
            Debug.W("Preparing upload file."+fromPath);
            notifyStatus(Status.PREPARE, com.merlin.task.What.WHAT_NONE,"Prepare upload file");
            final Long currentLength=onResolveBreakPoint(fromFile);
            if (null==currentLength){
                Debug.W("Can't upload file while break point resolve NULL.");
                notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_ERROR,"Break point resolve NULL "+fromPath);
                return;
            }
            HttpURLConnection connection=createHttpConnect(getTo(), POST);
            if (null==connection){
                Debug.W("Can't upload file while upload connection NULL.");
                notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_ERROR,"Upload connection NULL"+fromPath);
                return;
            }
            String fileName=fromFile.getName();

            inflateHeader(connection,"connection","Keep-Alive");
            String contentLength=Long.toString(fileLength);
            inflateHeader(connection,Label.LABEL_POSITION, Long.toString(currentLength));
            inflateHeader(connection,Label.LABEL_LENGTH,Long.toString(fileLength));
            inflateHeader(connection,"Content-Length", contentLength);
            inflateHeader(connection,"length", contentLength);
            inflateHeader(connection,"Content-Type", "binary/octet-stream;boundary=*********LuckMelrin*****;file="+ fileName);
            inflateHeader(connection,"name",fileName);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setChunkedStreamingMode(1024 * 1024);
            connection.connect();
            Debug.D("Uploading file "+fileLength+" "+toUriPath);
            FileProgress progress=new FileProgress(currentLength,fileLength);
            notifyStatus(Status.DOING,"Doing upload file "+fromPath, progress);
            OutputStream out=outputStream = new DataOutputStream(connection.getOutputStream());
            DataInputStream in=inputStream = new DataInputStream(new FileInputStream(fromFile));
            int size = 0;
            byte[] bufferOut = new byte[1024*1024];
            boolean canceled=false;
            long lastTime=System.nanoTime();
            long uploaded=0;
            double sec;
            if (null!=currentLength&&currentLength>0){
                long skip=in.skip(currentLength);
                Debug.D("Skip uploaded "+skip);
            }
            while ((size = in.read(bufferOut)) >=0) {
                if (size>0){
                    out.write(bufferOut, 0, size);
                    long currentTime=System.nanoTime();
                    uploaded += size;
                    progress.setDone(uploaded);
                    if ((sec=(((double) (currentTime-lastTime))/(1000000000)))>0){
                        progress.setPerBytes((long)(size/sec));
                    }
                    notifyStatus(Status.DOING,null, progress);
                    lastTime=currentTime;
                }
                if (pause(null,"Fetched pause status")){
                    canceled=true;
                    Debug.D("Canceled upload file."+fromPath);
                    break;
                }
            }
            out.flush();
            BufferedReader reader =bufferedReader= new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer buffer=new StringBuffer();
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String response=buffer.toString();
            JSONObject json=null!=response&&response.length()>0&&response.startsWith("{")&&response.endsWith("}")?new JSONObject(response):null;
            String note=null;
            if (null!=json){//{"note": "File path invalid", "what": -1005, "success": true, "data": null}
                boolean success=json.optBoolean(Label.LABEL_SUCCESS, false);
                int what=json.optInt(Label.LABEL_WHAT, What.WHAT_ERROR_UNKNOWN);
                note=json.optString(Label.LABEL_NOTE);
                if (success&&what==What.WHAT_SUCCEED){
                    Debug.D("Succeed upload file "+fileLength+" "+toUriPath);
                    notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_SUCCEED,"Succeed upload file. ");
                    return;
                }
            }
            Debug.D("Fail upload file "+note+" "+fileLength+" "+toUriPath);
            notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_ERROR,note);
        }catch (Exception e){
            Debug.E("Exception upload file."+e,e);
            e.printStackTrace();
            notifyStatus(Status.FINISH, com.merlin.task.What.WHAT_EXCEPTION,"Exception upload file task "+e);
            return;
        }finally {
            close(outputStream,inputStream,bufferedReader);
        }
        return;
    }
}
