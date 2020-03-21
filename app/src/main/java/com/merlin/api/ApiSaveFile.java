package com.merlin.api;
import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiSaveFile {
    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply> save(@Part MultipartBody.Part file);
}

