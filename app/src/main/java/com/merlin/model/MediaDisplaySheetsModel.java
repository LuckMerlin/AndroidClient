package com.merlin.model;

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
import com.merlin.api.Reply;
import com.merlin.api.PageData;
import com.merlin.api.What;
import com.merlin.bean.NasMedia;
import com.merlin.bean.MediaSheet;
import com.merlin.bean.SheetTitle;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.SingleInputDialog;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MediaDisplaySheetsModel extends Model implements BaseAdapter.OnItemClickListener<MediaSheet>,What, OnTapClick,Label {
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

    public MediaDisplaySheetsModel(){
        mSheetAdapter.setOnItemClickListener(this);
        queryAllSheets("全部");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        return false;
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, MediaSheet data) {
        mEnableSheets.set(false);
        querySheet(data,0);
    }

    private void selectCategory(int id){
    }

    private boolean queryCategories(){
        return null!= call(prepare(Api.class,Address.HOST).queryCategories(null,0,100), (OnApiFinish<Reply<PageData<SheetTitle>>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED){
//                mTitleAdapter.fillPage(null!=data?data.getData():null);
            }
        });
    }

    private void queryAllSheets(String titleName){
        call(prepare(Api.class,Address.HOST).queryAllSheets(titleName,0,10), (OnApiFinish<Reply<ApiList<MediaSheet>>>)(what, note, data, arg)->{
            if (what==WHAT_SUCCEED){
                ApiList<MediaSheet> list=null!=data?data.getData():null;
                mSheetAdapter.setData(list);
            }
        });
    }

    private boolean querySheet(MediaSheet sheet,int page){
       if (page<0){
           Debug.D(getClass(),"Can't query sheet with invalid args.sheetId="+sheet+" "+page);
           toast(R.string.requestFail);
           return false;
       }
       final long sheetId=sheet.getId();
       mQueryDetailId=sheetId;
       return null!=call(prepare(Api.class,Address.HOST).querySheetById(sheetId,page,50),(OnApiFinish<Reply<ApiList<NasMedia>>>)(what, note, data, arg)->{
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
       });
    }

    private void createSheet(){
        new SingleInputDialog(getViewContext()).show(R.string.createSheet,(dlg, text)->{
            dlg.dismiss();
            if (null!=text&&text.length()>0){
                call(prepare(Api.class,Address.HOST).createSheet(text),(OnApiFinish<Reply>)(what, note, data, arg)->{
                    if (what== What.WHAT_SUCCEED){
                        toast(R.string.createSucceed);
                    }else{
                        toast(""+note);
                    }
                });
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
