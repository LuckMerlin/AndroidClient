package com.merlin.model;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import androidx.databinding.ObservableField;

import com.google.gson.Gson;
import com.merlin.adapter.PhotoAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Love;
import com.merlin.bean.Photo;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.transport.Conveyor;
import com.merlin.transport.FileUploadConvey;
import com.merlin.view.OnTapClick;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoveDetailModel extends Model implements OnTapClick, Model.OnActivityIntentChange, Model.OnActivityResult {
    private final ObservableField<Love> mLove=new ObservableField<>();
    private final ObservableField<String> mContent=new ObservableField<>("测试内容");
    private final ObservableField<String> mTitle=new ObservableField<>("测试i标题");
    private final ObservableField<Long> mPlanTime=new ObservableField<>(System.currentTimeMillis());
    private final PhotoAdapter mPhotoAdapter=new PhotoAdapter(3,true);
    private final static int PHOTO_CHOOSE_ACTIVITY_RESULT_CODE=20243242;

    private interface Api {
        @POST(Address.PREFIX_LOVE + "/detail")
        @FormUrlEncoded
        Observable<Reply<Love>> getLovesDetail(@Field(Label.LABEL_ID) String id);

        @POST(Address.PREFIX_LOVE + "/save")
        @FormUrlEncoded
        Observable<Reply> save(@Field(Label.LABEL_DATA) String love);
    }


    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        File file=new File("/sdcard/Musics/大壮 - 我们不一样.mp3");
        new Conveyor(getViewContext(), Looper.getMainLooper()).convey(Conveyor.MODE_ADDED,null,null,
                new FileUploadConvey(new Retrofit(),file,"linqiangUpload",null));

//        RequestBody fileBody = new FileSaveRequestBody(file){
//            @Override
//            protected void onTransportProgress(long upload, long total, double speed) {
//                Debug.D(getClass(),"进度 "+upload+" "+total);
//            }
//        };
//        HashMap<String, RequestBody> map = new HashMap<>();
//        map.put("linqiang",fileBody);
//        prepare(ApiSaveFile.class,Address.LOVE_ADDRESS).save(map).enqueue(new Callback<Reply>() {
//            @Override
//            public void onResponse(Call<Reply> call, Response<Reply> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<Reply> call, Throwable t) {
//
//            }
//        });
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                return onSingleTap(view,resId,data);
        }
        return false;
    }

    private boolean onSingleTap(View view, int resId, Object data){
        switch (resId){
            case  R.id.loveDetail_saveTV:
                return save("After save tap.");
            case R.drawable.selector_photo_add:
                Context context=getViewContext();
                if (null!=context&&context instanceof Activity){
//                    MultiImageSelector.create().showCamera(true).count(-1) .multi() .start((Activity)context, PHOTO_CHOOSE_ACTIVITY_RESULT_CODE);
                }
                return true;
            case R.id.loveDetail_valueTimeTV:
                DatePickerDialog dialog= new DatePickerDialog(view.getContext(),(child,  year,  month,  dayOfMonth)-> {
                    Date date=new Date(year-1900,month-1,dayOfMonth);
                    mPlanTime.set(date.getTime());
                    }, 2020, 02, 22);
                 dialog.show();
                 return true;
        }
        return false;
    }

    private boolean save(String debug){
        final String title=mTitle.get();
        if (null==title||title.length()<=0){
            return toast(R.string.title,getText(R.string.inputNotNull));
        }
        final String content=mContent.get();
        if (null==content||content.length()<=0){
            return toast(R.string.content,getText(R.string.inputNotNull));
        }
        final Long planTime=mPlanTime.get();
        if (null==planTime||planTime<=0){
            return toast(R.string.planTime,getText(R.string.inputNotNull));
        }
        final Love love=new Love(title, planTime, content,mPhotoAdapter.getData());
        Debug.D(getClass(),"Save love "+title+" "+(null!=debug?debug:"."));
        return null!=call(prepare(Api.class, Address.LOVE_ADDRESS, null).save(new Gson().toJson(love)), (OnApiFinish)( what,  note,  data,  arg)-> {
            toast(note);
            if (what== What.WHAT_SUCCEED){
                finishActivity();
            }
        });
    }

    public ObservableField<Love> getLove() {
        return mLove;
    }

    public ObservableField<String> getContent() {
        return mContent;
    }

    public PhotoAdapter getPhotoAdapter() {
        return mPhotoAdapter;
    }

    public ObservableField<String> getTitle() {
        return mTitle;
    }

    public ObservableField<Long> getPlanTime() {
        return mPlanTime;
    }

    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        String id=null!=intent?intent.getStringExtra(Label.LABEL_ID):null;
        if (null!=id&&id.length()>0){
            call(prepare(Api.class,Address.LOVE_ADDRESS).getLovesDetail(id),(OnApiFinish<Reply<Love>>)( what,  note,  data,  arg)-> {
                Love love=what == What.WHAT_SUCCEED&&null!=data?data.getData():null;
                if (null!=love){
                    mLove.set(love);
                }
            });
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PHOTO_CHOOSE_ACTIVITY_RESULT_CODE:
                Bundle bundle=null!=data?data.getExtras():null;
                Object object=null!=bundle?bundle.get("select_result"):null;
                if (null!=object&&object instanceof ArrayList){
                    for (Object child:(ArrayList)object) {
                        if (null!=child&& child instanceof String){
                            mPhotoAdapter.add(-1,new Photo(null,child));
                        }
                    }
                }
                break;
        }
    }
}
