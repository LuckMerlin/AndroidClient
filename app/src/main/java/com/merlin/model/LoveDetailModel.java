package com.merlin.model;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import androidx.databinding.ObservableField;

import com.merlin.activity.LocalPhotoChooseActivity;
import com.merlin.activity.PhotoPreviewActivity;
import com.merlin.adapter.PhotoAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Document;
import com.merlin.bean.LocalFile;
import com.merlin.bean.Love;
import com.merlin.bean.NasFile;
import com.merlin.bean.Path;
import com.merlin.bean._Photo;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.browser.FileSaveBuilder;
import com.merlin.view.OnTapClick;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public class LoveDetailModel extends Model implements OnTapClick, Model.OnActivityIntentChange, Model.OnActivityResult {
    private final ObservableField<Love> mLove=new ObservableField<>();
    private final ObservableField<String> mContent=new ObservableField<>();
    private final ObservableField<String> mTitle=new ObservableField<>();
    private final ObservableField<Long> mPlanTime=new ObservableField<>(System.currentTimeMillis());
    private final PhotoAdapter mPhotoAdapter=new PhotoAdapter(3,true);
    private final static int PHOTO_CHOOSE_ACTIVITY_RESULT_CODE=20242;

    private interface Api {
        @POST(Address.PREFIX_LOVE + "/detail")
        @FormUrlEncoded
        Observable<Reply<Love<NasFile>>> getLovesDetail(@Field(Label.LABEL_ID) String id);

        @POST(Address.PREFIX_LOVE + "/save")
        @Multipart
        Observable<Reply<Love<NasFile>>> save(@QueryMap Map<String,String> map, @Part() List<MultipartBody.Part> list);
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                switch (resId){
                    case  R.id.loveDetail_saveTV:
                        return save("After save tap.");
                    case R.drawable.selector_photo_add:
                        Context context=getViewContext();
                        if (null!=context&&context instanceof Activity){
                            startActivity(new Intent(context, LocalPhotoChooseActivity.class),PHOTO_CHOOSE_ACTIVITY_RESULT_CODE);
                        }
                        return true;
                    case R.id.loveDetail_planDateTV:
                        Calendar curr=Calendar.getInstance();
                        curr.setTime(new Date(mPlanTime.get()));
                        DatePickerDialog dpDialog= new DatePickerDialog(view.getContext(),(child,  year,  month,  dayOfMonth)-> {
                            curr.set(year,month,dayOfMonth);
                            mPlanTime.set(curr.getTimeInMillis());
                        }, curr.get(Calendar.YEAR), curr.get(Calendar.MONTH), curr.get(Calendar.DAY_OF_MONTH));
                        dpDialog.show();
                        return true;
                }
                break;
            case 2:
                if (resId == R.id.loveDetail_planDateTV){
                    Calendar curr=Calendar.getInstance();
                    curr.setTime(new Date(mPlanTime.get()));
                    TimePickerDialog tpD=new TimePickerDialog(view.getContext(), (TimePicker v, int hourOfDay, int minute)-> {
                        curr.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        curr.set(Calendar.MINUTE,minute);
                        mPlanTime.set(curr.getTimeInMillis());
                    },curr.get(Calendar.HOUR_OF_DAY),curr.get(Calendar.MINUTE),true);
                    tpD.show();
                    return true;
                }
                break;
        }
        if (null!=data&&data instanceof _Photo){
            Object imageUrlObj=((_Photo)data).getImageUrl();
            String imageUrl=null!=imageUrlObj&&imageUrlObj instanceof String?(String)imageUrlObj:null;
            Uri uri=null!=imageUrl?Uri.fromFile(new File(imageUrl)):null;
            if (null!=uri){
                Bundle bundle=new Bundle();
                bundle.putParcelable(Label.LABEL_DATA,uri);
                startActivity(PhotoPreviewActivity.class,bundle);
            }
            return true;
        }
        return false;
    }

    private void applyLove(Love love){
        mLove.set(love);
        mContent.set(null!=love?love.getContent():null);
        mTitle.set(null!=love?love.getTitle():null);
        mPlanTime.set(null!=love?love.getTime():null);
        mPhotoAdapter.set(null!=love?love.getImage():null,"After love apply.");
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
        Map<String,String> loveTextMap=new HashMap<>();
        String mode=null;
        loveTextMap.put(Label.LABEL_NAME, title);
        loveTextMap.put(Label.LABEL_TIME, Long.toString(planTime));
        loveTextMap.put(Label.LABEL_DATA, content);
        loveTextMap.put(Label.LABEL_MODE, null!=mode?mode:"");
        Debug.D(getClass(),"Save love "+title+" "+(null!=debug?debug:"."));
        final List<Path> adapterPhotos=mPhotoAdapter.getData();
        List<MultipartBody.Part> parts=null;
        if (null!=adapterPhotos&&adapterPhotos.size()>0){
            FileSaveBuilder builder=new FileSaveBuilder();
            parts = new ArrayList<>();
            for (Path photo:adapterPhotos) {
                Object imageUrlObj = null != photo ? photo.getPath(null) : null;
                String imageUrl = null != imageUrlObj && imageUrlObj instanceof String ? ((String) imageUrlObj) : null;
                if (null != imageUrl && imageUrl.length() > 0) {
                    if (photo instanceof Path) {
                        File file = new File(imageUrl);
                        String name = "" + title + "_" + planTime + "_" + file.getName();
                        MultipartBody.Part part = null != file && file.exists() ? builder.createFilePart(file, name, "./lovesPhotos") : null;
                        if (null == part || !parts.add(part)) {
                            continue;
                        }
                    }
                }
            }};
        if (parts==null||parts.size()<=0){
            return toast(R.string.photo,getText(R.string.inputNotNull));
        }
        final String dialogKey=showLoading(R.string.loading);
        return null!=LoveDetailModel.this.call(prepare(Api.class,Address.LOVE_ADDRESS).save(loveTextMap,parts), null,(OnApiFinish<Reply<Love>>)(what, note, data,arg)-> {
            dismissLoading(dialogKey);
            toast(note);
            Love love=null!=data&&data.isSuccess()&&data.getWhat()==What.WHAT_SUCCEED?data.getData():null;
            if (null!=data&&data.isSuccess()&&data.getWhat()==What.WHAT_SUCCEED) {
                applyLove(love);
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
            call(prepare(Api.class,Address.LOVE_ADDRESS).getLovesDetail(id),(OnApiFinish<Reply<Love<NasFile>>>)( what,  note,  data,  arg)-> {
                Love love=what == What.WHAT_SUCCEED&&null!=data?data.getData():null;
                if (null!=love){
                    applyLove(love);
                }
            });
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PHOTO_CHOOSE_ACTIVITY_RESULT_CODE:
                Bundle bundle=null!=data?data.getExtras():null;
                Object object=null!=bundle?bundle.get(Label.LABEL_DATA):null;
                PhotoAdapter adapter=mPhotoAdapter;
                if (null!=adapter&&null!=object&&object instanceof ArrayList){
                    for (Object child:(ArrayList)object) {
                        if (null!=child){
                            if (child instanceof Path){
                                adapter.add((Path)child,true,"After activity choose result.");
                            }
                        }
                    }
                }
                break;
        }
    }
}
