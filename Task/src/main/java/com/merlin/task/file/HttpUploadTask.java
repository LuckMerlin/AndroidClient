package com.merlin.task.file;
import com.merlin.task.Canceler;
import com.merlin.task.FromToTask;
import com.merlin.task.Networker;
import com.merlin.task.Progress;
import com.merlin.task.Status;
import com.merlin.task.What;
import com.task.debug.Debug;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.io.File;
import java.net.URLEncoder;


public abstract class HttpUploadTask extends HttpFileTransTask<String, String> {
    private final String mToFolder;
    private final String mToName;

    public HttpUploadTask(String from, String to,String toFolder,String toName) {
        this(from,to,null,toFolder,toName);
    }

    public HttpUploadTask(String from, String to,String method,String toFolder,String toName) {
        super(from, to,method);
        mToFolder=toFolder;
        mToName=toName;
    }

    public final String getToFolder() {
        return mToFolder;
    }

    public final String getToName() {
        return mToName;
    }

    protected abstract Long onResolveBreakPoint(File file) throws Exception;

    protected void onUploadPrepared(HttpURLConnection connection){
        //Do nothing
    }

    protected final boolean inflate(HttpURLConnection connection,String key,String value){
        if (null!=connection&&null!=key&&key.length()>0&&null!=value){
            try {
                connection.setRequestProperty(key, URLEncoder.encode(value,"utf-8"));
                return true;
            } catch (UnsupportedEncodingException e) {
                Debug.E("Exception set value to header."+e,e);
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected Canceler onExecute(Networker networker) {
        String toUriPath=getTo();
        String fromPath=getFrom();
        if (null==toUriPath||toUriPath.length()<=0||null==fromPath||fromPath.length()<=0){
            Debug.W("Can't upload file while args invalid.");
            notifyStatus(Status.FINISH,"Upload args invalid");
            return null;
        }
        final File fromFile=new File(fromPath);
        final long fileLength=fromFile.exists()?fromFile.length():-1;
        if (fileLength<=0){
            Debug.W("Can't upload file while file is EMPTY or NOT exist.");
            notifyStatus(Status.FINISH, What.WHAT_EMPTY,"File is EMPTY or NOT exist "+fromPath);
            return null;
        }
        if (!fromFile.canRead()){
            Debug.W("Can't upload file while file none read permission.");
            notifyStatus(Status.FINISH, What.WHAT_NONE_PERMISSION,"NONE read permission "+fromPath);
            return null;
        }
        OutputStream outputStream=null;
        DataInputStream inputStream=null;
        try {
            Debug.W("Preparing upload file."+fromPath);
            notifyStatus(Status.PREPARE, What.WHAT_NONE,"Prepare upload file");
            final Long currentLength=onResolveBreakPoint(fromFile);
            if (null==currentLength){
                Debug.W("Can't upload file while break point resolve NULL.");
                notifyStatus(Status.FINISH, What.WHAT_ERROR,"Break point resolve NULL "+fromPath);
                return null;
            }
            createHttpConnect(,);
//            Debug.D("AAAAAAAAAAA "+currentLength);
//            final String fileName=fromFile.getName();
//            URL url = new URL(toUriPath);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setChunkedStreamingMode(1024 * 1024);
//            String method=getMethod();
//            conn.setRequestMethod(null!=method&&method.equalsIgnoreCase("post")?"POST":"GET");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setUseCaches(false);
//            conn.setRequestProperty("Charset", "UTF-8");
//            String contentLength=Long.toString(fileLength);
//            conn.setRequestProperty("Content-Length", contentLength);
//            conn.setRequestProperty("length", contentLength);
//            conn.setConnectTimeout(5000);
//            conn.setRequestProperty("Content-Type", "binary/octet-stream;boundary=*********LuckMelrin*****;file="+ fileName);
//            conn.setRequestProperty("name",fileName);
//            onUploadPrepared(conn);
//            conn.connect();
//            Debug.D("Uploading file "+fileLength+" "+toUriPath);
//            FileProgress progress=new FileProgress(currentLength,fileLength);
//            notifyStatus(Status.DOING,"Doing upload file "+fromPath, progress);
//            OutputStream out=outputStream = new DataOutputStream(conn.getOutputStream());
//            DataInputStream in=inputStream = new DataInputStream(new FileInputStream(fromFile));
//            int size = 0;
//            byte[] bufferOut = new byte[1024*1024];
//            int what=What.WHAT_SUCCEED;//Default succeed
//            long lastTime=System.nanoTime();
//            long uploaded=0;
//            double sec;
//            while ((size = in.read(bufferOut)) >=0) {
//                if (size>0){
//                    out.write(bufferOut, 0, size);
//                    long currentTime=System.nanoTime();
//                    uploaded += size;
//                    progress.setDone(uploaded);
//                    if ((sec=(((double) (currentTime-lastTime))/(1000000000)))>0){
//                        progress.setPerBytes((long)(size/sec));
//                    }
//                    notifyStatus(Status.DOING,null, progress);
//                    lastTime=currentTime;
//                }
//                if (isCanceled()){
//                    what=What.WHAT_CANCEL;
//                    Debug.D("Canceled upload file."+fromPath);
//                    break;
//                }
//            }
//            out.flush();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                System.out.println("---line---"+line);
//            }
//            Debug.D("Succeed upload file "+fileLength+" "+toUriPath);
        }catch (Exception e){
            Debug.E("Exception upload file."+e,e);
            e.printStackTrace();
            notifyStatus(Status.FINISH,What.WHAT_EXCEPTION,"Exception upload file task "+e);
            return null;
        }finally {
            close(outputStream,inputStream);
        }
        return null;
    }
}
