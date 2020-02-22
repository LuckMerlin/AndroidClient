package com.merlin.model;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableField;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaSheetAdapter;
import com.merlin.adapter.SheetMediaAdapter;
import com.merlin.adapter.SheetTitleAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.NasMedia;
import com.merlin.bean.MediaSheet;
import com.merlin.bean.SheetTitle;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.SingleInputDialog;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetsModel extends BaseModel implements BaseAdapter.OnItemClickListener<MediaSheet>,What,BaseModel.OnModelViewClick,Label {
    private final MediaSheetAdapter mSheetAdapter=new MediaSheetAdapter();
    private final SheetTitleAdapter mTitleAdapter=new SheetTitleAdapter();
    private final SheetMediaAdapter mSheetMediaAdapter=new SheetMediaAdapter();
    private final ObservableField<Boolean> mEnableSheets=new ObservableField<>(true);
    private final ObservableField<MediaSheet> mShowingSheet=new ObservableField<>();
    private Long mQueryDetailId;
    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/create")
        @FormUrlEncoded
        Observable<Reply> createSheet(@Field(LABEL_NAME) String name);

        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/detail")
        @FormUrlEncoded
        Observable<Reply<ApiList<NasMedia>>> querySheetById(@Field(LABEL_ID) long id, @Field(LABEL_PAGE) int page, @Field(LABEL_LIMIT) int limit);

        @POST(Address.PREFIX_MEDIA_PLAY+"/sheet/all")
        @FormUrlEncoded
        Observable<Reply<ApiList<MediaSheet>>> queryAllSheets(@Field(LABEL_NAME) String name,@Field(LABEL_PAGE) int page,@Field(LABEL_LIMIT) int limit);

        @POST(Address.PREFIX_MEDIA+"/sheet/category")
        @FormUrlEncoded
        Observable<Reply<PageData<SheetTitle>>> queryCategories(@Field(LABEL_NAME) String name, @Field(LABEL_PAGE) int page, @Field(LABEL_LIMIT) int limit);
    }

    public MediaDisplaySheetsModel(Context context){
        super(context);
        mSheetAdapter.setOnItemClickListener(this);
        queryAllSheets("全部");
    }

    @Override
    public void onViewClick(View v, int id,Object obj) {

    }

    @Override
    public void onItemClick(View view, int sourceId, int position, MediaSheet data) {
        mEnableSheets.set(false);
        querySheet(data,0);
    }

    private void selectCategory(int id){
    }

    private boolean queryCategories(){
        return null!= call(Api.class, (OnApiFinish<Reply<PageData<SheetTitle>>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED){
                mTitleAdapter.fillPage(null!=data?data.getData():null);
            }
        }).queryCategories(null,0,100);
    }

    private void queryAllSheets(String titleName){
        call(Api.class, (OnApiFinish<Reply<ApiList<MediaSheet>>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED){
                ApiList<MediaSheet> list=null!=data?data.getData():null;
                mSheetAdapter.setData(list);
            }
        }).queryAllSheets(titleName,0,10);
    }

    private boolean querySheet(MediaSheet sheet,int page){
       if (page<0){
           Debug.D(getClass(),"Can't query sheet with invalid args.sheetId="+sheet+" "+page);
           toast(R.string.requestFail);
           return false;
       }
       final long sheetId=sheet.getId();
       mQueryDetailId=sheetId;
       return null!=call(Api.class,(OnApiFinish<Reply<ApiList<NasMedia>>>)(what, note, data, arg)->{
           Long queryId=mQueryDetailId;
           if (null!=queryId&&queryId.equals(sheetId)){
               mQueryDetailId=null;
               if (what==WHAT_SUCCEED){
                   ApiList<NasMedia> list=null!=data?data.getData():null;
                   if (page<=0){
                       mShowingSheet.set(sheet);
                       mSheetMediaAdapter.setData(list,true);
                   }else{
                       mSheetMediaAdapter.addAll(list,true);
                   }
               }else{
                   toast(R.string.requestFail,note);
               }
           }
       }).querySheetById(sheetId,page,50);
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

    public ObservableField<Boolean> isEnableSheets() {
        return mEnableSheets;
    }

    public SheetMediaAdapter getSheetMediaAdapter() {
        return mSheetMediaAdapter;
    }

    public ObservableField<MediaSheet> getShowingSheet() {
        return mShowingSheet;
    }

    public SheetTitleAdapter getTitleAdapter() {
        return mTitleAdapter;
    }
}
