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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class FileUploadConvey extends ConveyGroup<FileUploadConvey> implements Label{
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
        return iteratorAddAllFileInDirectory(file,debug);
    }

    @Override
    protected Reply onStart(Finish finish, String debug) {
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
        final FileUploadBody requestBody = new FileUploadBody(file){
            @Override
            protected void onTransportProgress(long uploaded, long total, float speed) {
                Debug.D(getClass(),"进度 "+uploaded+" "+total);
            }
        };
        HashMap<String,RequestBody> params = new HashMap<>();
        addParams(LABEL_PARENT,mFolder,params);
        addParams(LABEL_NAME,getName(),params);
        addParams(LABEL_FOLDER,file.isDirectory()?LABEL_FOLDER:null,params);
        params.put(LABEL_DATA,requestBody);
        retrofit.prepare(ApiSaveFile.class, Address.LOVE_ADDRESS).save(params).enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {
                Debug.D(getClass(),"AAAAAAonResponseAAAAAAAA "+response);
            }

            @Override
            public void onFailure(Call<Reply> call, Throwable t) {
                Debug.D(getClass(),"onFailure "+t);
            }
        });
        return super.onStart(finish,debug);
    }

    private boolean addParams(String key,String value,Map<String, RequestBody> map){
        RequestBody requestBody = null!=key&&null!=key&&null!=map?RequestBody.create(MediaType.parse("text/plain"), value):null;
        if (null!=requestBody){
            map.put(key,requestBody);
            return true;
        }
        return false;
    }

    private Reply iteratorAddAllFileInDirectory(File file, String debug){
        if (null!=file){
            addChild(new FileUploadConvey(mRetrofit,file,mFolder,file.getName()),debug);
            if (file.isDirectory()){
                File[] files=file.listFiles();
                if (null!=files){
                    for (File child:files) {
                        if (null!=child){
                            iteratorAddAllFileInDirectory(child,debug);
                        }
                    }
                }
            }
        }
        return null;
    }

    public File getFile() {
        return mFile;
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
