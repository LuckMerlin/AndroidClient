package com.merlin.model;

import android.content.Context;
import android.view.View;

import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.MediaSheet;
import com.merlin.client.R;
import com.merlin.dialog.SingleInputDialog;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetsModel extends BaseModel implements What,BaseModel.OnModelViewClick,Label {
    private final MediaSheetAdapter mSheetAdapter=new MediaSheetAdapter();

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/create")
        @FormUrlEncoded
        Observable<Reply> createSheet(@Field(LABEL_NAME) String name);

        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/get")
        @FormUrlEncoded
        Observable<Reply<MediaSheet>> querySheetByName(@Field(LABEL_NAME) String name);

        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/all")
        @FormUrlEncoded
        Observable<Reply<ApiList<MediaSheet>>> queryAllSheets(@Field(LABEL_NAME) String name,@Field(LABEL_PAGE) int page,@Field(LABEL_LIMIT) int limit);

    }

    public MediaDisplaySheetsModel(Context context){
        super(context);
        queryAllSheets();
    }

    @Override
    public void onViewClick(View v, int id) {
        switch (id){
            case R.id.mediaDisplaySheet_createTV:
                createSheet();
                break;
        }
    }

    private void queryAllSheets(){
        call(Api.class, (OnApiFinish<Reply<ApiList<MediaSheet>>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED){
                ApiList<MediaSheet> list=null!=data?data.getData():null;
                mSheetAdapter.setData(list);
            }
        }).queryAllSheets(null,0,10);
    }

    private void createSheet(){
        new SingleInputDialog(getViewContext()).show(R.string.createSheet,(dlg, text)->{
            dlg.dismiss();
            if (null!=text&&text.length()>0){
                call(Api.class,(OnApiFinish<Reply>)(what, note, data, arg)->{
                    if (what== What.WHAT_SUCCEED){
                        toast(R.string.createSucceed);
                    }else{
                        toast(""+note);
                    }
                }).createSheet(text);
            }
        });
    }

    public MediaSheetAdapter getSheetAdapter() {
        return mSheetAdapter;
    }
}
