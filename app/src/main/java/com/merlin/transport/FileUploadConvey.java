package com.merlin.transport;

import com.merlin.api.Address;
import com.merlin.api.ApiSaveFile;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Response;

public final class FileUploadConvey extends ConveyGroup<FileUploadConvey.FileConvey> implements Label{
    private final File mFile;
    private final Retrofit mRetrofit;
    private final String mFolder;

    public FileUploadConvey(Retrofit retrofit,File file,String folder){
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

    protected final static class FileConvey extends Convey{
        private final File mFile;
        private final String mFolder;
        private final Retrofit mRetrofit;

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
            }
            final FileUploadBody requestBody = new FileUploadBody(file){
                @Override
                protected void onTransportProgress(long uploaded, long total, float speed) {
                    if (null!=finish){
                        finish.onProgress(uploaded,total,speed,FileConvey.this);
                    }
                }
            };
            String name=file.getName();
            String folder=mFolder;
            name= null!=name?name:"";
            StringBuilder disposition = new StringBuilder("form-data; name=luckmerlin;filename=luckmerlin");
            Headers.Builder headersBuilder = new Headers.Builder().addUnsafeNonAscii(
                    "Content-Disposition", disposition.toString());
            headersBuilder.add(LABEL_NAME,encode(name,""));
            headersBuilder.add(LABEL_PARENT,encode(folder,""));
            headersBuilder.add(LABEL_PATH_SEP,encode(File.separator,""));
            if (file.isDirectory()){
                headersBuilder.add(LABEL_FOLDER,LABEL_FOLDER);
            }
            MultipartBody.Part part= MultipartBody.Part.create(headersBuilder.build(),requestBody);
            Debug.D(getClass(),"Upload file "+name+" to "+folder+" "+name+" "+(null!=debug?debug:"."));
            Reply responseReply;
            try {
                Response<Reply> response=retrofit.prepare(ApiSaveFile.class, Address.LOVE_ADDRESS).save(part).execute();
                responseReply=null!=response?response.body():null;
            } catch (IOException e) {
                Debug.E(getClass(),"Exception call file upload api."+e,e);
                responseReply=new Reply(false,WHAT_ERROR_UNKNOWN,"Exception call file upload api."+e,e);
                e.printStackTrace();
            }
            if (null!=finish){
                finish.onFinish(responseReply);
            }
            return null;
        }
        private String encode(String name,String def){
            try {
                return null!=name&&name.length()>0?URLEncoder.encode(name, "UTF-8"):def;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return def;
        }

    }

    private static abstract class FileUploadBody extends RequestBody {
        private final File mFile;
        private boolean mCancel=false;

        protected abstract void onTransportProgress(long uploaded,long total,float speed);

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
        public void writeTo(BufferedSink sink) throws IOException {
            final File file = mFile;
            boolean succeed = false;
            if (null != file && file.exists()) {
                if (file.isFile()) {
                    FileInputStream in = null;
                    try {
                        long fileLength = file.length();
                        JSONObject json = new JSONObject();
                        json.put(Label.LABEL_NAME, file.getName());
                        json.put(Label.LABEL_LENGTH, fileLength);
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        in = new FileInputStream(file);
                        long uploaded = 0;
                        if (!mCancel) {
                            int read;
                            succeed = true;
                            while ((read = in.read(buffer)) != -1) {
                                if (mCancel) {
                                    break;
                                }
                                uploaded += read;
                                sink.write(buffer, 0, read);
                                onTransportProgress(uploaded, fileLength, -1);
                            }
                        }
                    } catch (Exception e) {
                        succeed = false;
                    } finally {
                        if (null != in) {
                            in.close();
                        }
                    }
                }
            }
        }

    }

}
