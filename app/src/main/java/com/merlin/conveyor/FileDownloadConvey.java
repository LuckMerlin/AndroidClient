package com.merlin.conveyor;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.Download;

import java.io.File;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public class FileDownloadConvey extends Convey{
    private interface Api{
        @Streaming
        @POST(Address.PREFIX_FILE+"/download")
        @FormUrlEncoded
        Call<ResponseBody> download(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_POSITION) double seek);
    }

    private final Retrofit mRetrofit;
    private final File mTarget;
    private final String mUrl;
    private final String mPath;

    public FileDownloadConvey(Retrofit retrofit,NasFile nas,String url,File folder){
        super(null!=nas?nas.getName(true):null);
        String name=getName();
        mRetrofit=retrofit;
        mPath=null!=nas?nas.getPath():null;
        mUrl=url;
        mTarget=null!=name&&name.length()>0&&null!=folder?new File(folder,name):null;
    }

    @Override
    protected Reply onPrepare(String debug) {
        final Retrofit retrofit=mRetrofit;
        if (null==retrofit){
            return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
        }else if (null==mUrl){
            return new Reply(false,WHAT_ARGS_INVALID,"Invalid url.",null);
        }else if (null==mPath){
            return new Reply(false,WHAT_ARGS_INVALID,"Invalid path.",null);
        }
        final File file=mTarget;
        if (null==file||(!file.exists()&&!file.mkdirs())){
            return new Reply(false,WHAT_CREATE_FAILED,"Folder create fail.",file);
        }else if (!file.exists()||!file.canWrite()){
            return new Reply(false,WHAT_NONE_PERMISSION,"Folder none write permission.",file);
        }
        return null;
    }

    @Override
    protected Boolean onCancel(boolean cancel, String debug) {
        return null;
    }

    @Override
    protected Reply onStart(Finisher finish, String debug) {
        final Retrofit retrofit=mRetrofit;
        final String url=mUrl;
        final String path=mPath;
        if (null==retrofit){
            Debug.W(getClass(),"Can't download file with NULL retrofit."+(null!=debug?debug:"."));
            return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
        }else if (null==url||url.length()<=0){
            return new Reply(false,WHAT_ARGS_INVALID,"Invalid url.",null);
        }else if (null==path||path.length()<=0){
            return new Reply(false,WHAT_ARGS_INVALID,"Invalid path.",null);
        }
        Call<ResponseBody> call=retrofit.prepare(Api.class, url, null).download(path,0);
//        call.enqueue(downloadBody);
        return new Reply(false,WHAT_ARGS_INVALID,"Args invalid.",null);
    }
}
