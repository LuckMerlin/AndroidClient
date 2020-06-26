package com.merlin.api;
import com.merlin.bean.IPath;

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
    Call<Reply<ApiList<Reply<IPath>>>> save(@Part MultipartBody.Part file);

    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply<ApiList<Reply<IPath>>>> saves(@Part() List<MultipartBody.Part> list);

    @POST(Address.PREFIX_FILE + "/meta")
    @FormUrlEncoded
    Observable<Reply<IPath>> getSaved(@Field(Label.LABEL_MD5) String md5);
}

