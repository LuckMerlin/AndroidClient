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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class FileUploadConvey extends ConveyGroup<FileUploadConvey.FileConvey> implements Label{
    private final File mFile;
    private final Retrofit mRetrofit;
    private final String mFolder;

    public FileUploadConvey(Retrofit retrofit,File file,String folder, String name){
        super(null!=name&&name.length()>0?name:null!=file?file.getName():null);
        mFolder=folder;
        mRetrofit=retrofit;
        mFile=file;
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
        return iteratorAddAllFileInDirectory(file.getParent(),file,debug);
    }

    private Reply iteratorAddAllFileInDirectory(String root,File file, String debug){
        if (null!=file&&null!=root&&root.length()>0){
            String fileName=file.getName();
            String parent=file.getParent();
            if (null==parent||parent.length()<=0){
                Debug.W(getClass(),"Can't iterator add all file while parent is NULL."+file);
                return null;
            }
            String targetFolderName=parent.replaceAll(root,"");
            addChild(new FileConvey(mRetrofit,file,targetFolderName,fileName,fileName),debug);
            File[] files=file.isDirectory()?file.listFiles():null;;
            if (null!=files){
                for (File child:files) {
                    if (null!=child){
                        iteratorAddAllFileInDirectory(root, child,debug);
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
        private final String mName;
        private final Retrofit mRetrofit;

        private FileConvey(Retrofit retrofit,File file,String folder,String name,String conveyName){
            super(conveyName);
            mRetrofit=retrofit;
            mFile=file;
            mFolder=folder;
            mName=name;
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
        protected Reply onStart(Finish finish, String debug) {
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
                    Debug.D(getClass(),"进度 "+uploaded+" "+total);
                }
            };
            String name=file.getName();
            String folder=mFolder;
            String targetFile=(null!=folder?folder:"")+(null!=name?name:"");
            MultipartBody.Part body = MultipartBody.Part.createFormData(LABEL_PATH, targetFile, requestBody);
            String fileName=file.getName();
            HashMap<String,RequestBody> params = new HashMap<>();
            final boolean isDirectory=file.isDirectory();
//            String name=getName();
//            addParams(LABEL_PARENT,folder,params);
//            addParams(LABEL_NAME,name,params);
            addParams(LABEL_HINT,File.separator,params);
            addParams(LABEL_FOLDER,isDirectory?LABEL_FOLDER:null,params);
            Debug.D(getClass(),"Upload file "+fileName+" to "+folder+" "+name+" "+(null!=debug?debug:"."));
            Reply responseReply;
            try {
                Response<Reply> response=retrofit.prepare(ApiSaveFile.class, Address.LOVE_ADDRESS).save(body,params).execute();
                responseReply=null!=response?response.body():null;
//                if (isDirectory&&null!=responseReply&&responseReply.getWhat()==WHAT_EXIST){//Fix
//                    responseReply=new Reply(true,responseReply.getWhat(),responseReply.getNote(),responseReply.getData());
//                }
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

        private boolean addParams(String key,String value,Map<String, RequestBody> map){
            RequestBody requestBody = null!=key&&null!=value&&null!=map?RequestBody.
                    create(MediaType.parse("multipart/form-data"), value):null;
            if (null!=requestBody){
                map.put(key,requestBody);
                return true;
            }
            return false;
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
