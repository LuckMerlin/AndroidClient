package com.merlin.api;
import com.merlin.bean.Path;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiSaveFile {
    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply<ApiList<Reply<Path>>>> save(@Part MultipartBody.Part file);

    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply<ApiList<Reply<Path>>>> saves(@Part() List<MultipartBody.Part> list);

    @POST(Address.PREFIX_FILE + "/meta")
    @FormUrlEncoded
    Observable<Reply<Path>> getSaved(@Field(Label.LABEL_MD5) String md5);
}

