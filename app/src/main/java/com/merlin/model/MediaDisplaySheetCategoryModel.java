package com.merlin.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.adapter.MediaSheetCategoryAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.PageData;
import com.merlin.api.What;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.dialog.Dialog;
import com.merlin.server.Retrofit;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetCategoryModel extends Model implements Label, OnTapClick, OnLongClick, MediaPlayDisplayAdapter.OnMediaPlayModelShow {
    private final MediaSheetCategoryAdapter mCategoryAdapter=new MediaSheetCategoryAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Sheet>>> finish) {
            return call(prepare(Api.class).queryCategory(arg,false,from,from+10),finish);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/all")
        @FormUrlEncoded
        Observable<Reply<PageData<Sheet>>> queryCategory(@Field(LABEL_NAME) String name, @Field(LABEL_USER) boolean user , @Field(LABEL_FROM) int from,
                                                         @Field(LABEL_TO) int to);

        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/create")
        @FormUrlEncoded
        Observable<Reply<String>> createSheet(@Field(LABEL_TITLE) String title, @Field(LABEL_NOTE) String note,@Field(LABEL_URL) String url);

        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/delete")
        @FormUrlEncoded
        Observable<Reply<Sheet>> deleteSheet(@Field(LABEL_ID) String id);

    }

    public MediaDisplaySheetCategoryModel(){
        queryPage(false,"While model create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.string.add:
                        return createSheet()||true;
                }
                return null!=data&&data instanceof Sheet&&startActivity(MediaSheetDetailActivity.class,(Sheet)data);
        }
        return true;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        if (null!=data&&data instanceof Sheet){
            deleteSheet((Sheet)data);
        }
        return true;
    }

    private boolean deleteSheet(Sheet sheet){
        final String id=null!=sheet?sheet.getId():null;
        if (null==id||id.length()<=0){
            toast(R.string.inputNotNull);
            return false;
        }
        final Dialog dialog=new Dialog(getViewContext());
        String title=sheet.getTitle();
        return dialog.create().title(R.string.delete).message(getText(R.string.deleteSure,(null!=title?title:"")+"("+sheet.getSize()+")")).left(R.string.sure).right(R.string.cancel).show((view,clickCount,resId,data)->{
                dialog.dismiss();
                if (resId==R.string.sure){
                    call(prepare(Api.class).deleteSheet(id),(OnApiFinish<Reply<Sheet>>)(what, note, data2, arg)->{
                        toast(note);
                        MediaSheetCategoryAdapter adapter=what== What.WHAT_SUCCEED?mCategoryAdapter:null;
                        if (null!=adapter){
                            adapter.remove(sheet,"After sheet create succeed.");
                        }
                    });
                }
                return true;});
    }

    private boolean createSheet(){
        Context context=getViewContext();
        final Dialog dialog=new Dialog(context);
        final EditText et=(EditText)LayoutInflater.from(context).inflate(R.layout.edit_text,null,false);
        return dialog.setContentView(et).title(R.string.createSheet).left(R.string.sure).right(R.string.cancel).show((view, clickCount, resId, data)->{
           if (resId == R.string.sure){
               String text=et.getText().toString();
               if (null==text||text.length()<=0){
                   toast(R.string.inputNotNull);
                   return true;
               }
               call(prepare(Api.class).createSheet(text,null,null),(OnApiFinish<Reply<ApiList<Sheet>>>)(what, note, data2, arg)->{
                   toast(note);
                   MediaSheetCategoryAdapter adapter=what== What.WHAT_SUCCEED?mCategoryAdapter:null;
                   if (null!=adapter){
                       adapter.reset("After sheet create succeed.");
                   }
               });
           }
            dialog.dismiss();
            return true;
        },false);
    }

    private boolean queryPage(boolean reset,String debug){
        MediaSheetCategoryAdapter adapter=mCategoryAdapter;
        return null!=adapter&&(reset?adapter.reset(debug):adapter.loadPage(null,debug));
    }

    @Override
    public void onMediaPlayModelShow() {
        MediaSheetCategoryAdapter adapter= mCategoryAdapter;
        if (null!=adapter){
            adapter.reset("After media sheet model show.");
        }
    }

    public MediaSheetCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }
}
