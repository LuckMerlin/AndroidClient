package com.merlin.file;

import com.merlin.api.Address;
import com.merlin.api.Label;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface DownloadApi {
    @Streaming
    @POST(Address.PREFIX_FILE+"/download")
    @FormUrlEncoded
    Call<ResponseBody> downloadFile(@Field(Label.LABEL_PATH) String path, @Field(Label.LABEL_PLAY) boolean play);
}
