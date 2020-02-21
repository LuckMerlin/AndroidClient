package com.merlin.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.merlin.activity.MediaSheetActivity;
import com.merlin.activity.MediaSheetDetailActivity;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.adapter.MediaSheetCategoryAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.Modify;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.api.What;
import com.merlin.bean.MediaSheet;
import com.merlin.bean.Sheet;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetCategoryModel extends Model implements MediaPlayDisplayAdapter.OnMediaPlayModelShow,Label, OnTapClick, OnLongClick {
    private final MediaSheetCategoryAdapter mCategoryAdapter=new MediaSheetCategoryAdapter(){
        @Override
        protected boolean onPageLoad(String arg, int from, OnApiFinish<Reply<SectionData<Sheet>>> finish) {
            return null!=call(Api.class,finish).queryCategory(arg,false,from,from+10);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/all")
        @FormUrlEncoded
        Observable<Reply<SectionData<Sheet>>> queryCategory(@Field(LABEL_NAME) String name,@Field(LABEL_USER) boolean user ,@Field(LABEL_FROM) int from,
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
    public void onMediaPlayModelShow() {
//        queryPage(true,"After model show.");
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
                    call(Api.class,(OnApiFinish<Reply<Sheet>>)(what, note, data2, arg)->{
                        toast(note);
                        MediaSheetCategoryAdapter adapter=what== What.WHAT_SUCCEED?mCategoryAdapter:null;
                        if (null!=adapter){
                            adapter.remove(sheet,"After sheet create succeed.");
                        }
                    }).deleteSheet(id);
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
               call(Api.class,(OnApiFinish<Reply<ApiList<Sheet>>>)(what, note, data2, arg)->{
                   toast(note);
                   MediaSheetCategoryAdapter adapter=what== What.WHAT_SUCCEED?mCategoryAdapter:null;
                   if (null!=adapter){
//                       adapter.resetLoad("After sheet create succeed.");
                   }
               }).createSheet(text,null,null);
           }
            dialog.dismiss();
            return true;
        },false);
    }

    private boolean queryPage(boolean reset,String debug){
        MediaSheetCategoryAdapter adapter=mCategoryAdapter;
        return null!=adapter&&(reset?adapter.reset(debug):adapter.loadPage(null,debug));
    }

    public MediaSheetCategoryAdapter getCategoryAdapter() {
        return mCategoryAdapter;
    }
}
