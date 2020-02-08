package com.merlin.model;

import com.merlin.adapter.MediaSheetCategoryAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Sheet;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetCategoryModel extends Model implements Label {
    private final MediaSheetCategoryAdapter mCategoryAdapter=new MediaSheetCategoryAdapter(){
        @Override
        protected boolean onPageLoad(String arg, int page, OnApiFinish<Reply<PageData<Sheet>>> finish) {
            return null!=call(Api.class,finish).queryCategory(arg,page,10);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_SHEET_ALL)
        @FormUrlEncoded
        Observable<Reply<PageData<Sheet>>> queryCategory(@Field(LABEL_NAME) String name, @Field(LABEL_PAGE) int page,
                                                  @Field(LABEL_LIMIT) int limit);
    }

    public MediaDisplaySheetCategoryModel(){
        queryPage("While model create.");
    }

    private boolean queryPage(String debug){
        MediaSheetCategoryAdapter adapter=mCategoryAdapter;
        return null!=adapter&&adapter.loadPage(null,debug);
    }

    public MediaSheetCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }
}
