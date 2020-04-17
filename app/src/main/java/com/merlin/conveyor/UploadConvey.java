package com.merlin.conveyor;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.ApiSaveFile;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.NasFile;
import com.merlin.bean.Path;
import com.merlin.browser.FileSaveBuilder;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.transport.litehttp.UploadBody;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadConvey extends Convey {
    private final String mPath;
    private final String mServerUrl;
    private final String mFolder;
    private final int mCoverMode;

    public UploadConvey(String path,String serverUrl,String folder,int coverMode){
        mPath=path;
        mServerUrl=serverUrl;
        mFolder=folder;
        mCoverMode=coverMode;
    }

    @Override
    protected boolean onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
            String serverUrl=mServerUrl;
            String path=mPath;
            final String folder=mFolder;
            if (null==path||path.length()<=0||null==serverUrl||null==retrofit||serverUrl.length()<=0){
                return finish(change,this,new Reply<>(true, What.WHAT_ARGS_INVALID,"Args NULL.",null))&&false;
            }
            final File file=new File(path);
            if (!file.exists()){
                return finish(change,this,new Reply<>(true, What.WHAT_FILE_NOT_EXIST,"File not exist.",null))&&false;
            }
            if (!file.canRead()){
                return finish(change,this, new Reply<>(true, What.WHAT_NONE_PERMISSION,"File none permission.",null))&&false;
            }
            FileSaveBuilder builder=new FileSaveBuilder();
            Reply progressReply=new Reply();
            UploadRequestBody uploadBody=new UploadRequestBody(file){
                @Override
                protected void onProgress(long upload, float speed) {
                    progress(change,UploadConvey.this,progressReply);
                }
            };
            MultipartBody.Part part=builder.createFilePart(builder.createFileHeadersBuilder(file.getName(),folder,file.isDirectory()),uploadBody);
            Debug.D(getClass(),"Upload file "+file.getName()+" to "+mFolder+" "+(null!=debug?debug:"."));
            Call<Reply<ApiList<Reply<Path>>>> call=retrofit.prepare(ApiSaveFile.class, serverUrl).save(part);
            if (null==call){
                Debug.W(getClass(),"Can't upload file which upload call is NULL."+(null!=debug?debug:"."));
                return finish(change,this,new Reply(false,What.WHAT_ERROR_UNKNOWN,"Error on NULL file upload call.",null));
            }
            call.enqueue(new Callback<Reply<ApiList<Reply<Path>>>>() {
                @Override
                public void onResponse(Call<Reply<ApiList<Reply<Path>>>> call, Response<Reply<ApiList<Reply<Path>>>> response) {
                    finish(change,UploadConvey.this,null!=response?response.body():null);
                }

                @Override
                public void onFailure(Call<Reply<ApiList<Reply<Path>>>> call, Throwable t) {
                    finish(change,UploadConvey.this,new Reply(false,What.WHAT_ERROR_UNKNOWN,"Request fail."+t,null));
                }
            });
            return true;
    }

    private static abstract class UploadRequestBody extends RequestBody {
        private final File mFile;
        private boolean mCancel=false;
        private final long mLength;

        protected abstract void onProgress(long upload,float speed);

        private UploadRequestBody(File file){
            mFile=file;
            mLength=null!=file?file.length():0;
        }

        @Override
        public final MediaType contentType() {
            return MediaType.parse("application/otcet-stream");
        }

        @Override
        public final void writeTo(BufferedSink sink) throws IOException {
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
                                onProgress(uploaded, -1);
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
