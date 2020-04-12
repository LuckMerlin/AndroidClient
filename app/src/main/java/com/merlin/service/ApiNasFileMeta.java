package com.merlin.service;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasFile;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiNasFileMeta {
    @POST(Address.PREFIX_FILE+"/meta")
    @FormUrlEncoded
    Observable<Reply<NasFile>> getMeta(@Field(Label.LABEL_MD5) String md5,@Field(Label.LABEL_PATH) String path);
}
