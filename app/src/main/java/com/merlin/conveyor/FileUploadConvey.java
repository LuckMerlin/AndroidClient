package com.merlin.conveyor;

import androidx.annotation.Nullable;

import com.merlin.api.Address;
import com.merlin.api.ApiSaveFile;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.debug.Debug;
import com.merlin.file.FileSaveBuilder;
import com.merlin.server.Retrofit;
import com.merlin.util.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;

public final class FileUploadConvey extends ConveyGroup<FileUploadConvey.FileConvey> implements Label{
    private final File mFile;
    private final Retrofit mRetrofit;
    private final String mFolder;

    public FileUploadConvey(Retrofit retrofit,File file,String url,String folder){
        super(null!=file?file.getName():null);
        mRetrofit=retrofit;
        mFile=file;
        mFolder=folder;
    }

    @Override
    protected Reply onPrepare(String debug) {
        final Retrofit retrofit=mRetrofit;
        if (null==retrofit){
            return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
        }
        final File file=mFile;
        if (null==file||!file.exists()){
            return new Reply(false,WHAT_FILE_EXIST,"File not exist.",file);
        }else if (!file.canRead()){
            return new Reply(false,WHAT_NONE_PERMISSION,"File none read permission.",file);
        }
        return iteratorAddAllFileInDirectory(file.getParent(),file,mFolder,debug);
    }

    private Reply iteratorAddAllFileInDirectory(String root,File file,String folder, String debug){
        if (null!=file&&null!=root&&root.length()>0){
            String parent=file.getParent();
            if (null==parent||parent.length()<=0){
                Debug.W(getClass(),"Can't iterator add all file while parent is NULL."+file);
                return null;
            }
            String targetFolderName=parent.replaceAll(root,"");
            if (null!=folder&&folder.length()>0){
                folder= folder.endsWith(File.separator)?folder:folder+File.separator;
                if(null!=targetFolderName&&targetFolderName.length()>0){
                    targetFolderName= targetFolderName.startsWith(File.separator)?
                            targetFolderName.replaceFirst(File.separator,""): targetFolderName;
                }
                targetFolderName=folder+targetFolderName;
            }
            addChild(new FileConvey(mRetrofit,file,targetFolderName),debug);
            File[] files=file.isDirectory()?file.listFiles():null;;
            if (null!=files){
                for (File child:files) {
                    if (null!=child){
                        iteratorAddAllFileInDirectory(root, child,folder,debug);
                    }
                }
            }
        }
        return null;
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof FileUploadConvey){
            FileUploadConvey convey=((FileUploadConvey)obj);
            String folder=convey.mFolder;
            File file=convey.mFile;
            String filePath=null!=file?file.getAbsolutePath():null;
            File currFile=mFile;
            String currFilePath=null!=currFile?currFile.getAbsolutePath():null;
            if (((null==folder&&null==mFolder)||(null!=folder&&null!=mFolder&&folder.equals(mFolder)))&&
                    (null==filePath&&null==currFilePath)||(null!=filePath&&null!=currFilePath&&filePath.equals(currFilePath))){
                return true;
            }
        }
        return super.equals(obj);
    }

    protected final static class FileConvey extends Convey {
        private final File mFile;
        private final String mFolder;
        private final Retrofit mRetrofit;
        private Call<Reply> mUploadingCall;

        private FileConvey(Retrofit retrofit,File file,String folder){
            super(null!=file?file.getName():null);
            mRetrofit=retrofit;
            mFile=file;
            mFolder=folder;
        }

        @Override
        protected Reply onPrepare(String debug) {
            final Retrofit retrofit=mRetrofit;
            if (null==retrofit){
                return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
            }
            final File file=mFile;
            if (null==file||!file.exists()){
                return new Reply(false,WHAT_FILE_EXIST,"File not exist.",null);
            }else if (!file.canRead()){
                return new Reply(false,WHAT_NONE_PERMISSION,"File none read permission.",null);
            }
            return null;
        }

        @Override
        protected Reply onStart(Finisher finish, String debug) {
            final Retrofit retrofit=mRetrofit;
            if (null==retrofit){
                Debug.W(getClass(),"Can't upload file with NULL retrofit."+(null!=debug?debug:"."));
                return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
            }
            final File file=mFile;
            if (null==file||!file.exists()){
                Debug.W(getClass(),"Can't upload file which not exist."+(null!=debug?debug:"."));
                return new Reply(false,WHAT_FILE_EXIST,"File not exist.",null);
            }else if (!file.canRead()){
                Debug.W(getClass(),"Can't upload file which none read permission."+(null!=debug?debug:"."));
                return new Reply(false,WHAT_NONE_PERMISSION,"File none read permission.",null);
            }else if (null!=mUploadingCall){
                Debug.W(getClass(),"Can't upload file which is already uploading."+(null!=debug?debug:"."));
                return new Reply(false,WHAT_ALREADY_DOING,"File already uploading.",null);
            }

            final FileUploadBody requestBody = new FileUploadBody(file){
                @Override
                protected void onTransportProgress(long uploaded, long total, float speed) {
                    if (null!=finish){
                        finish.onProgress(uploaded,total,speed,FileConvey.this);
                    }
                }

                @Override
                protected boolean isCancel() {
                    return FileConvey.this.isCancel();
                }
            };
            MultipartBody.Part part=new FileSaveBuilder().createFilePart(file,mFolder,requestBody);
            Debug.D(getClass(),"Upload file "+file.getName()+" to "+mFolder+" "+(null!=debug?debug:"."));
            Reply responseReply=null;
            Call<Reply> call=null;
            try {
                Map<String,MultipartBody.Part> list=new HashMap<>();
                list.put("ddd",part);
                call=mUploadingCall=retrofit.prepare(ApiSaveFile.class, Address.LOVE_ADDRESS).save(list);
                if (null==call){
                    Debug.W(getClass(),"Can't upload file which upload call is NULL."+(null!=debug?debug:"."));
                    return new Reply(false,WHAT_ERROR_UNKNOWN,"Error on NULL file upload call.",null);
                }
                Response<Reply> response=call.execute();
                responseReply=null!=response?response.body():null;
            } catch (IOException e) {
                if (e instanceof SocketTimeoutException){
                    responseReply=new Reply(false,WHAT_TIMEOUT,"Timeout call file upload api."+e,e);
                }else if (e instanceof ConnectException){
                    responseReply=new Reply(false,WHAT_NONE_NETWORK,"Network exception call file upload api."+e,e);
                }else if(e instanceof ProtocolException){
                    String message=e.getMessage();
                    if (null!=message&&message.equals(Integer.toString(What.WHAT_CANCEL))){
                        responseReply=new Reply(false,WHAT_CANCEL,"Canceled call file upload api."+e,e);
                    }else{
                        responseReply=new Reply(false,WHAT_EXCEPTION,"Exception call file upload api."+e,e);
                    }
                }else{
                    responseReply=new Reply(false,WHAT_EXCEPTION,"Exception call file upload api."+e,e);
                }
                Debug.E(getClass(),"Exception call file upload api."+e,e);
                e.printStackTrace();
            }
            Call<Reply> curr=mUploadingCall;
            if (null!=curr&&null!=call&&curr==call){
                mUploadingCall=null;
            }
            if (null!=finish){
                finish.onFinish(responseReply);
            }
            return null;
        }

        @Override
        protected Boolean onCancel(boolean cancel, String debug) {
            final Retrofit retrofit=mRetrofit;
            Call<Reply> curr=mUploadingCall;
            if (null!=retrofit&&null!=curr){
                if (cancel&&!curr.isCanceled()){
                    Debug.D(getClass(),"Canceling file upload "+mFile+" "+(null!=debug?debug:"."));
                    curr.cancel();
                    return true;
                }
            }
            return false;
        }
    }

    private static abstract class FileUploadBody extends RequestBody {
        private final File mFile;

        protected abstract void onTransportProgress(long uploaded,long total,float speed);

        protected abstract boolean isCancel();

        private FileUploadBody(File file){
            mFile=file;
        }

        @Override
        public long contentLength() {
            File file = mFile;
            return null != file && file.exists() && file.isFile() ? file.length() : 0;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse("application/otcet-stream");
        }

        @Override
        public void writeTo(BufferedSink sink) {
            final File file = mFile;
            if (null != file && file.exists()) {
                if (file.isFile()) {
                    FileInputStream in = null;
                    long fileLength = file.length();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    try {
                        in = new FileInputStream(file);
                        long uploaded = 0;
                        if (!isCancel()) {
                            int read;
                            while ((read = in.read(buffer)) != -1) {
                                if (isCancel()) {
                                    Debug.D(getClass(),"Cancel file upload convey.");
                                    throw new IOException(Integer.toString(What.WHAT_CANCEL));
                                }
                                uploaded += read;
                                sink.write(buffer, 0, read);
                                onTransportProgress(uploaded, fileLength, -1);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        new Closer().close(in);
                    }
                }
            }
        }
    }

}
