package com.merlin.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.merlin.activity.MediaSheetActivity;
import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaSheetCategoryAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetCategoryModel extends Model implements Label, OnTapClick {
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

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.string.add:
                        return addCategory()||true;
                }
                return null!=data&&data instanceof Sheet&&startActivity(MediaSheetDetailActivity.class,(Sheet)data);
        }
        return true;
    }

    private boolean addCategory(){
        Context context=getViewContext();
        final Dialog dialog=new Dialog(context);
        LayoutInflater.from(context).inflate(R.layout.edit_text,null,false);
        return dialog.setContentView(R.layout.edit_text).title(R.string.createSheet).
                left(R.string.sure).right(R.string.cancel).show((view, clickCount, resId, data)->{
//                    dialog.getContext()
                    dialog.dismiss();
                    return true;
        },false);
    }

    private boolean queryPage(String debug){
        MediaSheetCategoryAdapter adapter=mCategoryAdapter;
        return null!=adapter&&adapter.loadPage(null,debug);
    }

    public MediaSheetCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }
}
