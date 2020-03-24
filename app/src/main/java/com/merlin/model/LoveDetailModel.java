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

import com.google.gson.Gson;
import com.merlin.activity.PhotoPreviewActivity;
import com.merlin.adapter.PhotoAdapter;
import com.merlin.api.Address;
import com.merlin.api.ApiSaveFile;
import com.merlin.api.Client;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Love;
import com.merlin.bean.Photo;
import com.merlin.client.R;
import com.merlin.conveyor.FileUploadConvey;
import com.merlin.debug.Debug;
import com.merlin.file.FileSaveBuilder;
import com.merlin.view.OnTapClick;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
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
//        String name,String url,String account,String imageUrl,String folder,String pathSep
//        Client meta=new Client("",Address.LOVE_ADDRESS,null,null,null,null);
//        File file=new File("/sdcard/Musics");
//        File file=new File("/sdcard/youku");

//        File file2=new File("/sdcard/Musics/大壮 - 我们不一样.mp3");
//        ConveyorService.upload(getViewContext(),file,meta,"操蛋 d算法 ",0,null);
//        ConveyorService.upload(getViewContext(),file2,meta,"操蛋 d算法 ",0,null);
//        startActivity(ConveyorActivity.class);
//        post(()->{mBinder.run();},5000);
//        Conveyor conveyor=new Conveyor(Looper.getMainLooper());
//        conveyor.listener(mChange, ConveyStatus.ADD,"");
//        FileUploadConvey convey=new FileUploadConvey(new Retrofit(), file,"林强");
////        new Conveyor(getViewContext(), Looper.getMainLooper()).start(, null, null);
//        conveyor.add(null,"",convey);
//      RequestBody fileBody = new FileSaveRequestBody(file){
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
                switch (resId){
                    case  R.id.loveDetail_saveTV:
                        return save("After save tap.");
                    case R.drawable.selector_photo_add:
                        Context context=getViewContext();
                        if (null!=context&&context instanceof Activity){
                            MultiImageSelector.create().showCamera(true).count(-1) .multi() .start((Activity)context, PHOTO_CHOOSE_ACTIVITY_RESULT_CODE);
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
        if (null!=data&&data instanceof Photo){
            Object imageUrlObj=((Photo)data).getImageUrl();
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
        List<Photo> photos=mPhotoAdapter.getData();
        final Love love=new Love(title, planTime, content,photos);
        Debug.D(getClass(),"Save love "+title+" "+(null!=debug?debug:"."));
        final Callback<Reply> photoUpload=new Callback<Reply>(){
            @Override
            public void onFailure(Call<Reply> call, Throwable t) {

            }

            @Override
            public void onResponse(Call<Reply> call, Response<Reply> response) {

            }
        };
        Map<String,MultipartBody.Part> parts=null;
        if (null!=photos&&photos.size()>0){
            FileSaveBuilder builder=new FileSaveBuilder();
            parts=new HashMap<>();
            for (Photo photo:photos){
                Object imageUrlObj=null!=photo?photo.getImageUrl():null;
                String imageUrl=null!=imageUrlObj&&imageUrlObj instanceof String?((String)imageUrlObj):null;
                File file=null!=imageUrl&&imageUrl.length()>0?new File(imageUrl):null;
                MultipartBody.Part part=null!=file&&file.isFile()&&file.exists()?builder.createFilePart(file,
                        "love"+File.separator+"images", RequestBody.create(MediaType.parse("multipart/form-data"), file)):null;
                if (null!=part){
                    parts.put(file.getName(),part);
                }
            }
        }
        if (null!=parts&&parts.size()>0){
            prepare(ApiSaveFile.class, Address.LOVE_ADDRESS).save(parts).enqueue(photoUpload);
        }else{
            photoUpload.onResponse(null,null);
        }
//        return null!=call(prepare(Api.class, Address.LOVE_ADDRESS, null).save(new Gson().toJson(love)), (OnApiFinish)( what,  note,  data,  arg)-> {
//            toast(note);
//            if (what== What.WHAT_SUCCEED){
//                finishActivity();
//            }
//        });
        return false;
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
                            mPhotoAdapter.add(0,new Photo(null,child));
                        }
                    }
                }
                break;
        }
    }
}
