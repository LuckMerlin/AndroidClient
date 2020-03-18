package com.merlin.api;
import java.util.HashMap;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

public interface UploadFileApi {
    @POST(Address.PREFIX_FILE + "/save")
    @Multipart
    Call<Reply> save(@PartMap HashMap<String, RequestBody> map);
}

