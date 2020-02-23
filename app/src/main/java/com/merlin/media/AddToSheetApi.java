package com.merlin.media;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasMedia;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AddToSheetApi {

    @POST(Address.PREFIX_MEDIA+"/play/addMediaIntoSheet")
    @FormUrlEncoded
    Observable<Reply<NasMedia>> addIntoSheet(@Field(Label.LABEL_MD5) String md5, @Field(Label.LABEL_ID) String sheet_id);

}
