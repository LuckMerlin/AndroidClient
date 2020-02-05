package com.merlin.model;

import androidx.annotation.Nullable;

import com.merlin.adapter.MediaSheetCategoryAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;

import com.merlin.bean.FolderMeta;
import com.merlin.bean.Sheet;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetCategoryModel extends Model implements Label {
    private final MediaSheetCategoryAdapter mCategoryAdapter=new MediaSheetCategoryAdapter();

    private interface Api{
        @POST(Address.PREFIX_MEDIA_SHEET_ALL)
        @FormUrlEncoded
        Observable<Reply<PageData<Sheet>>> queryCategory(@Field(LABEL_NAME) String name, @Field(LABEL_PAGE) int page,
                                                  @Field(LABEL_LIMIT) int limit);
    }

    public MediaDisplaySheetCategoryModel(){
        queryCategory(0);
    }

    public boolean queryCategory(int page){
        return null!=call(Api.class, "CategoryQueryDither_" + page,(OnApiFinish<Reply<PageData<Sheet>>>)(what, note, data, arg)->{
            if (what== What.WHAT_SUCCEED){
                mCategoryAdapter.fillPage(null!=data?data.getData():null);
            }
        }).queryCategory("",page,20);
    }

    public MediaSheetCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }
}
