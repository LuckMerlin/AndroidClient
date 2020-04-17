package com.merlin.api;
import com.merlin.bean.Path;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
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

}

