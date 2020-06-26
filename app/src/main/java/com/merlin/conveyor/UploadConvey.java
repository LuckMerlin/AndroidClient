package com.merlin.conveyor;
import com.merlin.api.ApiList;
import com.merlin.api.ApiSaveFile;
import com.merlin.api.CoverMode;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.IPath;
import com.merlin.browser.FileSaveBuilder;
import com.merlin.browser.Md5Reader;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.OnConveyStatusChange;
import com.merlin.util.FileSize;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadConvey extends FileConvey {

    public UploadConvey(IPath from, IPath to, int coverMode){
        super(from,to,coverMode);
    }

    @Override
    protected boolean onConvey(Retrofit retrofit, OnConveyStatusChange change, String debug) {
            IPath to=getTo();
            String serverUrl=null!=to?to.getHost():null;
            IPath pathValue=getFrom();
            String path=null!=pathValue?pathValue.getPath():null;
            final String folder=null!=to?to.getParent():null;
            if (null==path||path.length()<=0||null==serverUrl||null==retrofit||serverUrl.length()<=0){
                return updateStatus(FINISHED,change,this,new Reply<>(true, What.WHAT_ARGS_INVALID,"Args NULL.",null))&&false;
            }
            final File file=new File(path);
            if (!file.exists()){
                return updateStatus(FINISHED,change,this,new Reply<>(true, What.WHAT_FILE_NOT_EXIST,"File not exist.",null))&&false;
            }
            if (!file.canRead()){
                return updateStatus(FINISHED,change,this, new Reply<>(true, What.WHAT_NONE_PERMISSION,"File none permission.",null))&&false;
            }
            final int coverMode=getCoverMode();
            final String[] md5s=new String[1];
            updateStatus(PREPARING,change,this,null);
            final OnApiFinish<Reply<IPath>> md5CheckFinish=(int what, String note, Reply<IPath> data, Object arg)-> {
                updateStatus(PREPARED,change,this,null);
                IPath exist=null!=data?data.getData():null;
                final Confirm confirm=(int innerWhat,String de)->{
                    if (!isCanceled()&&innerWhat==What.WHAT_SUCCEED){//If need continue
                        final FileSaveBuilder builder=new FileSaveBuilder();
                        final Progress progress=new Progress(file.length());
                        final Reply<Progress> progressReply=new Reply<>(true,What.WHAT_SUCCEED,"",progress);
                        final UploadRequestBody uploadBody=new UploadRequestBody(file){
                            @Override
                            protected Boolean onProgress(long upload, float speed) {
                                if (!isFinished()){
                                    progress.setConveyed(upload);
                                    updateStatus(PROGRESS,change,UploadConvey.this,progressReply);
                                    return false;
                                }
                                return true;
                            }
                        };
                        MultipartBody.Part part=builder.createFilePart(builder.createFileHeadersBuilder(file.getName(),folder,file.isDirectory()),uploadBody);
                        Debug.D(getClass(),"Upload file "+file.getName()+" to "+folder+" "+(null!=debug?debug:"."));
                        Call<Reply<ApiList<Reply<IPath>>>> call= retrofit.prepare(ApiSaveFile.class, serverUrl).save(part);
                        call.enqueue(new Callback<Reply<ApiList<Reply<IPath>>>>() {
                            @Override
                            public void onResponse(Call<Reply<ApiList<Reply<IPath>>>> call, Response<Reply<ApiList<Reply<IPath>>>> response) {
                                updateStatus(FINISHED,change,UploadConvey.this,null!=response?response.body():null);
                            }

                            @Override
                            public void onFailure(Call<Reply<ApiList<Reply<IPath>>>> call, Throwable t) {
                                updateStatus(FINISHED,change,UploadConvey.this,new Reply(true,What.WHAT_ERROR_UNKNOWN,"File upload error."+t,null));
                            }
                        });
                    }else{
                        updateStatus(FINISHED,change,UploadConvey.this,new Reply(true,innerWhat,"Upload cancel.",null));
                    }
                };
                if (null!=exist&&(coverMode!= CoverMode.REPLACE&&coverMode!=CoverMode.KEEP)){
                    updateStatus(CONFIRM,change,UploadConvey.this,new Reply(true,What.WHAT_INTERRUPT,"File existed.",confirm));
                }else{
                    confirm.onConfirm(What.WHAT_SUCCEED,"Not need interrupt,Just go to upload.");
                }
            };
            if (coverMode!= CoverMode.REPLACE&&coverMode!=CoverMode.KEEP){
                final ApiSaveFile api=retrofit.prepare(ApiSaveFile.class,serverUrl);
                retrofit.call(api.getSaved(null).subscribeOn(Schedulers.io()).doOnSubscribe((Disposable disposable) ->{
                        disposable.dispose();
                        if (!isCanceled()){
                            String md5=md5s[0]=new Md5Reader().load(file);
                            if (!isCanceled()&&null!=md5&&md5.length()>0){
                                retrofit.call(api.getSaved(md5).subscribeOn(Schedulers.io()),md5CheckFinish);
                            }else{
                                md5CheckFinish.onApiFinish(What.WHAT_SUCCEED,"Md5 load NONE",
                                        new Reply(true,What.WHAT_SUCCEED,"Md5 load none",null),null);
                            }
                        }
                }));
            }else {
                md5CheckFinish.onApiFinish(What.WHAT_SUCCEED, "Not need check md5",
                        new Reply(true, What.WHAT_SUCCEED, "Not need check md5", null), null);
            }
            return true;
    }

    private static abstract class UploadRequestBody extends RequestBody {
        private final File mFile;
        private boolean mCancel=false;

        protected abstract Boolean onProgress(long upload,float speed);

        private UploadRequestBody(File file){
            mFile=file;
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
                    Debug.D(getClass(),"Uploading file "+ FileSize.formatSizeText(file.length()) +" "+file.getAbsolutePath());
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
                                Boolean interruptUpload=onProgress(uploaded, -1);
                                if (null!=interruptUpload&&interruptUpload){
                                    Debug.D(getClass(),"File upload interrupted.");
                                    break;
                                }
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
