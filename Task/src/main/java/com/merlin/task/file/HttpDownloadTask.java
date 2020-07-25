package com.merlin.task.file;
import com.merlin.task.Networker;
import com.merlin.task.Result;
import com.merlin.task.Status;
import com.merlin.task.What;
import com.task.debug.Debug;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;

public class HttpDownloadTask extends HttpFileTransTask<String, String> {
    private String mMethod;

    public HttpDownloadTask(String name,String from, String to,String method) {
        super(name,from, to,method);
    }

    public final long getDownloadedLength(){
        String toPath=getTo();
        return null!=toPath&&toPath.length()>0?new File(toPath).length():0;
    }

    protected void onDownloadPrepared(HttpURLConnection connection,long currentLength){
        //Do nothing
    }

    @Override
    protected final void onExecute(Networker networker) {
        final String toPath=getTo();
        final String fromUriPath=getFrom();
        if (null==toPath||toPath.length()<=0||null==fromUriPath||fromUriPath.length()<=0){
            notifyStatus(Status.FINISH,"Download args invalid");
            return;
        }
        final File toFile=new File(toPath);
        final File parent=toFile.getParentFile();
        if (null==parent){
            notifyStatus(Status.FINISH, What.WHAT_ERROR,"Fail download file while parent is NULL.");
            return;
        }
        OutputStream outputStream=null;
        InputStream inputStream=null;
        HttpURLConnection connection = null;
        try {
            Debug.D("Prepare download file."+fromUriPath);
            notifyStatus(Status.PREPARE,"Prepare download file task "+fromUriPath);
            HttpURLConnection conn=connection=createHttpConnect(fromUriPath,HEAD);
            if (null==conn){
                notifyStatus(Status.FINISH, What.WHAT_EXCEPTION,"Fail create connect to fetch file head.");
                return;
            }
            String contentType=conn.getContentType();
            String connHeaderLength = conn.getHeaderField("content-length");
            final long fileLength = null!=connHeaderLength&&connHeaderLength.length()>0?Long.parseLong(connHeaderLength):-1;
            Debug.D("Head fetched of download file."+contentType+" "+fileLength+" "+fromUriPath);
            if (fileLength<=0){
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Download file length is EMPTY "+fileLength);
                return;
            }
            long currentLength=toFile.exists()?toFile.length():0;
            final FileProgress progress=new FileProgress(currentLength,fileLength);
            final FileDownloadResult succeedResult=new FileDownloadResult(fromUriPath,toPath,fileLength,contentType);
            if (currentLength>=fileLength){
                int cover=getCover();
                if (cover!=Cover.COVER_REPLACE) {
                    Debug.D("File has been already downloaded. "+fileLength+" "+toPath);
                    notifyStatus(Status.DOING, What.WHAT_NONE, "File has been already downloaded."+fileLength, progress);
                    notifyStatus(Status.FINISH, What.WHAT_NONE, "File has been already downloaded."+fileLength, succeedResult);
                    return;
                }else{
                    toFile.delete();
                    if (!toFile.exists()) {

                        progress.setDone(currentLength=0);
                        Debug.D("Deleted already downloaded file while download with cover mode replace." + cover + " " + toPath);
                    }else{
                        notifyStatus(Status.FINISH, What.WHAT_NONE, "Fail delete already downloaded."+fileLength, succeedResult);
                        return;
                    }
                }
            }
            conn.disconnect();//Disconnect head connection
            conn=connection=createHttpConnect(fromUriPath,mMethod);
            if (null==conn){
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Fail open connection for download target path");
                return;
            }
            conn.setRequestProperty("Range","bytes=" + currentLength + "-");
            onDownloadPrepared(conn,currentLength);
            Debug.D("Prepared to download file from "+currentLength+" to "+fileLength+" "+fromUriPath);
            conn.connect();
            final int responseCode=conn.getResponseCode();
            if (responseCode!=HttpURLConnection.HTTP_OK&&responseCode!=HttpURLConnection.HTTP_PARTIAL){
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Fail open connection response code invalid."+responseCode);
                return;
            }
           final InputStream input=inputStream= conn.getInputStream();
           if (null==input){
               notifyStatus(Status.FINISH, What.WHAT_ERROR,"Stream is NULL ");
               return;
           }
           BufferedInputStream bufferedInputStream = new BufferedInputStream(input);
           if (!toFile.exists()){
               if (!parent.exists()) {//Make parent directory
                   Debug.D("Create download file parent directory. "+parent);
                   parent.mkdirs();
               }
               if (parent.exists()){
                   Debug.D("Create download file. "+toFile);
                   toFile.createNewFile();
               }
           }
           if (!toFile.exists()){
               Debug.D("Fail download file which create file fail. "+toFile);
               notifyStatus(Status.FINISH, What.WHAT_ERROR,"Create file fail");
               return;
           }
           if (!toFile.canWrite()){
               Debug.D("Fail download file which target path NONE permission. "+toFile);
               notifyStatus(Status.FINISH, What.WHAT_ERROR,"Target path NONE permission");
               return;
           }
           Debug.D("Downloading file "+fileLength+" "+contentType+" "+toPath);
           notifyStatus(Status.DOING,"Doing download file task "+fromUriPath, progress);
           OutputStream out =outputStream= new FileOutputStream(toFile,currentLength<fileLength);
           long downloaded=currentLength;
           int size=0;
           final byte[] buf = new byte[1024*1024];
           int what=What.WHAT_SUCCEED;//Default succeed
           long lastTime=System.nanoTime();
           double sec;
           while ((size = bufferedInputStream.read(buf)) >=0) {
               if (size>0){
                   out.write(buf, 0, size);
                   long currentTime=System.nanoTime();
                   downloaded += size;
                   if ((sec=(((double) (currentTime-lastTime))/(1000000000)))>0){
                       progress.setPerBytes((long)(size/sec));
                   }
                   progress.setDone(downloaded);
                   notifyStatus(Status.DOING,null, progress);
                   lastTime=currentTime;
               }
               if (pause(null,"Fetch pause status")){
                   what=What.WHAT_CANCEL;
                   Debug.D("Canceled download file."+toPath);
                   break;
               }
            }
            out.flush();
           if (what==What.WHAT_SUCCEED){
               Debug.D("Succeed download file. "+toFile.length()+"\n"+"From:"+fromUriPath+"\nTo:"+toPath);
               notifyStatus(Status.FINISH,what,"Succeed download file "+fromUriPath,succeedResult);
               return;
           }
            Debug.W("Fail download file task."+fileLength+" "+toPath);  //Download fail
            if (!isEnableBreakPoint()&&null!=toFile&&toFile.exists()){//Delete fail file
                Debug.D("Delete download fail file."+toFile.length()+" "+toPath);
                toFile.delete();
            }
            notifyStatus(Status.FINISH,what,"Fail download file "+fromUriPath, null);
            return;
        }catch (Exception e){
            Debug.E("Exception download file."+e,e);
            e.printStackTrace();
            if (e instanceof FileNotFoundException){//File not exist
                notifyStatus(Status.FINISH,What.WHAT_NOT_EXIST,"Exception download file task which not exist "+e);
            }else{
                notifyStatus(Status.FINISH,What.WHAT_EXCEPTION,"Exception download file task "+e);
            }
            return;
        }finally {
            close(outputStream,inputStream);
            if (null!=connection){
                connection.disconnect();
            }
        }
    }

    public static class FileDownloadResult implements Result {
        public final String mTo;
        public final String mFrom;
        public final long mLength;
        public final String mContentType;

        private FileDownloadResult(String from,String to,long length,String contentType){
            mContentType=contentType;
            mFrom=from;
            mTo=to;
            mLength=length;
        }

    }
}
