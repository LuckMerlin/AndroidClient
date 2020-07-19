package com.merlin.task.file;
import com.merlin.task.Canceler;
import com.merlin.task.Networker;
import com.merlin.task.Result;
import com.merlin.task.Status;
import com.merlin.task.What;
import com.task.debug.Debug;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloadTask extends FileTransTask<String, String> {
    private String mMethod;

    public HttpDownloadTask(String from, String to) {
        this(from,to,null);
    }

    public HttpDownloadTask(String from, String to,String method) {
        super(from, to,method);
    }

    public final long getDownloadedLength(){
        String toPath=getTo();
        return null!=toPath&&toPath.length()>0?new File(toPath).length():0;
    }

    protected void onDownloadPrepared(HttpURLConnection connection,long currentLength){
        //Do nothing
    }

    @Override
    protected final Canceler onExecute(Networker networker) {
        String toPath=getTo();
        String fromUriPath=getFrom();
        if (null==toPath||toPath.length()<=0||null==fromUriPath||fromUriPath.length()<=0){
            notifyStatus(Status.FINISH,"Download args invalid");
            return null;
        }
        final File toFile=new File(toPath);
        final File parent=toFile.getParentFile();
        if (null==parent){
            notifyStatus(Status.FINISH, What.WHAT_ERROR,"Fail download file while parent is NULL.");
            return null;
        }
        OutputStream outputStream=null;
        InputStream inputStream=null;
        HttpURLConnection connection = null;
        try {
            URL fromUri=new URL(fromUriPath);
            notifyStatus(Status.PREPARE,"Prepare download file task "+fromUri);
            HttpURLConnection conn=connection = (HttpURLConnection) fromUri.openConnection();
            if (null==conn){
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Fail open connection for download target path");
                return null;
            }
            String method=mMethod;
            conn.setRequestMethod(null!=method&&method.equalsIgnoreCase("post")?"POST":"GET");
            conn.setRequestProperty("Charset", "UTF-8");
            final boolean isEnableBreakPoint=isEnableBreakPoint();
            final long currentLength=toFile.exists()?toFile.length():0;
            if (isEnableBreakPoint&&currentLength>0){
                conn.setRequestProperty("Range","bytes=" + currentLength + "-");
            }
            onDownloadPrepared(conn,currentLength);
            conn.connect();
            String contentType=conn.getContentType();
            if (null==contentType||!contentType.equalsIgnoreCase("application/octet-stream")){
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Fail download path which content type invalid."+contentType+" "+fromUriPath);
                return null;
            }
            String connHeaderLength = conn.getHeaderField("content-length");
            final long fileLength = null!=connHeaderLength&&connHeaderLength.length()>0?Long.parseLong(connHeaderLength):-1;
            if (fileLength<=0){
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Download file length is EMPTY "+fileLength);
                return null;
            }

            if (currentLength==fileLength){
                int cover=getCover();
                if (cover==Cover.COVER_SKIP||cover==Cover.COVER_NONE) {
                    Debug.D("File has been already downloaded. "+fileLength+" "+toPath);
                    notifyStatus(Status.FINISH, What.WHAT_SKIP, "File has been already downloaded."+fileLength);
                    return null;
                }
            }
           final InputStream input=inputStream= conn.getInputStream();
           if (null==input){
               notifyStatus(Status.FINISH, What.WHAT_ERROR,"Stream is NULL ");
               return null;
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
               return null;
           }
           if (!toFile.canWrite()){
               Debug.D("Fail download file which target path NONE permission. "+toFile);
               notifyStatus(Status.FINISH, What.WHAT_ERROR,"Target path NONE permission");
               return null;
           }
           FileProgress progress=new FileProgress(currentLength,fileLength);
           Debug.D("Downloading file "+fileLength+" "+toPath);
           notifyStatus(Status.DOING,"Doing download file task "+fromUri, progress);
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
               if (isCanceled()){
                   what=What.WHAT_CANCEL;
                   Debug.D("Canceled download file."+toPath);
                   break;
               }
            }
            out.flush();
           if (what==What.WHAT_SUCCEED){
               Debug.D("Succeed download file. "+toFile.length()+"\n"+"From:"+fromUriPath+"\nTo:"+toPath);
               notifyStatus(Status.FINISH,what,"Succeed download file "+fromUri, new FileDownloadResult(fromUriPath,toPath,fileLength));
               return null;
           }
            Debug.W("Fail download file task."+fileLength+" "+toPath);  //Download fail
            if (!isEnableBreakPoint&&null!=toFile&&toFile.exists()){//Delete fail file
                Debug.D("Delete download fail file."+toFile.length()+" "+toPath);
                toFile.delete();
            }
            notifyStatus(Status.FINISH,what,"Fail download file "+fromUri, null);
            return null;
        }catch (Exception e){
            Debug.E("Exception download file."+e,e);
            e.printStackTrace();
            if (e instanceof FileNotFoundException){//File not exist
                notifyStatus(Status.FINISH,What.WHAT_NOT_EXIST,"Exception download file task which not exist "+e);
            }else{
                notifyStatus(Status.FINISH,What.WHAT_EXCEPTION,"Exception download file task "+e);
            }
            return null;
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

        private FileDownloadResult(String from,String to,long length){
            mFrom=from;
            mTo=to;
            mLength=length;
        }

    }
}
