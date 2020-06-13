package com.merlin.media;

import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.INasFile;
import com.merlin.bean.Path;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

 interface FavoriteApi  {
        @POST(Address.PREFIX_FILE+"/favorite")
        @FormUrlEncoded
        Observable<Reply> makeFavorite(@Field(Label.LABEL_MD5) String md5, @Field(Label.LABEL_DATA) boolean favorite );
}
