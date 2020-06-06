package com.merlin.model;

import android.view.View;

import androidx.databinding.ObservableField;

import com.merlin.adapter.MediaSheetChooseListAdapter;
import com.merlin.api.Address;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaSheetCreateModel extends Model implements Label, OnTapClick {
    private final ObservableField<String> mTitle=new ObservableField<>();

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/create")
        @FormUrlEncoded
        Observable<Reply<PageData<Sheet>>> createSheet(@Field(LABEL_TITLE) String title, @Field(LABEL_NOTE) String note);
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.string.create:
                 String title=mTitle.get();
                 if (null==title||title.length()<=0){
                    return toast(R.string.inputNotNull)||true;
                 }
                return null!=call(prepare(Api.class, Address.URL).createSheet(title,""),
                        (OnApiFinish<Reply<PageData<Sheet>>>)(int what, String note, Reply<PageData<Sheet>> d, Object arg)-> {


                })||true;
        }
        return false;
    }

    public ObservableField<String> getTitle() {
        return mTitle;
    }
}
