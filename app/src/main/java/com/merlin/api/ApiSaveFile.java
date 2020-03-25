package com.merlin.api;
import com.merlin.bean.NasFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiSaveFile {
    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply> save(@Part MultipartBody.Part file);

    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply<ApiList<Reply<NasFile>>>> saves(@Part() List<MultipartBody.Part> list);

}

