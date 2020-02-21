package com.merlin.model;

import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.adapter.MediaSheetCategoryAdapter;
import com.merlin.adapter.MediaSheetChooseListAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.bean.Sheet;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaSheetChooseListModel extends Model implements Label {

    private final MediaSheetChooseListAdapter mAdapter=new MediaSheetChooseListAdapter(){
        @Override
        protected boolean onPageLoad(String arg, int from, OnApiFinish<Reply<SectionData<Sheet>>> finish) {
            return null!=call(MediaSheetChooseListModel.Api.class,finish).querySheets(arg,true,from,10);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/all")
        @FormUrlEncoded
        Observable<Reply<SectionData<Sheet>>> querySheets(@Field(LABEL_NAME) String name, @Field(LABEL_USER) boolean user , @Field(LABEL_FROM) int from,
                                                         @Field(LABEL_TO) int to);
    }

    public MediaSheetChooseListModel(){
        queryPage("While model create.");
    }

    private boolean queryPage(String debug){
        MediaSheetChooseListAdapter adapter=mAdapter;
        return null!=adapter&&adapter.loadPage(null,debug);
    }

    public MediaSheetChooseListAdapter getAdapter() {
        return mAdapter;
    }

}
