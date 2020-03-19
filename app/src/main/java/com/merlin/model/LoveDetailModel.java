package com.merlin.model;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.PhotoAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.UploadFileApi;
import com.merlin.bean.Love;
import com.merlin.bean.Photo;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.view.OnTapClick;
import com.merlin.view.OnTextChanged;
import com.merlin.view.Res;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import io.reactivex.Observable;
import me.nereo.multi_image_selector.MultiImageSelector;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoveDetailModel extends Model implements OnTapClick, OnTextChanged, Model.OnActivityResult {
    private final ObservableField<Love> mLove=new ObservableField<>();
    private final static int PHOTO_CHOOSE_ACTIVITY_RESULT_CODE=20243242;

    private interface Api {
        @POST(Address.PREFIX_LOVE + "/detail")
        @FormUrlEncoded
        Observable<Reply<Love>> getLovesDetail(@Field(Label.LABEL_ID) String id);

        @POST(Address.PREFIX_LOVE + "/save")
        @FormUrlEncoded
        Observable<Reply> save(@Field(Label.LABEL_DATA) Love love);
    }

    private final PhotoAdapter mPhotoAdapter=new PhotoAdapter(3,true);

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        File file=new File("/sdcard/Musics/大壮 - 我们不一样.mp3");
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/otcet-stream"),file);
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("linqiang",fileBody);
//        Call ddd=prepare(UploadFileApi.class,Address.LOVE_ADDRESS).save(map);
//        ddd.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//
//            }
//        });

        // 执行请求 serviceApi.uploadSingleImg(description, body).
        // enqueue(new BaseViewModel.HttpRequestCallback<List<PicResultData>>()
        // { @Override public void onSuccess(List<PicResultData> result) { super.onSuccess(result); }
        // @Override public void onFailure(int status, String message) { super.onFailure(status, message); } });

//        findViewById(R.id.loveDetail_titleET);
//        findViewById(R.id.loveDetail_valueTimeTV);
//        findViewById(R.id.loveDetail_contentET);

//        mPhotoAdapter.add(new Photo());
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
                    MultiImageSelector.create().showCamera(true).count(-1) .multi() .start((Activity)context, PHOTO_CHOOSE_ACTIVITY_RESULT_CODE);
                }
                return true;
            case R.id.loveDetail_valueTimeTV:
                DatePickerDialog dialog= new DatePickerDialog(view.getContext(),(child,  year,  month,  dayOfMonth)-> {
                    Date date=new Date(year-1900,month-1,dayOfMonth);
//                    mPlanTime.set(date.getTime());
                    }, 2020, 02, 22);
                dialog.show();
                 return true;
        }
        return false;
    }

    @Override
    public void onTextChanged(TextView tv, int state, CharSequence text) {
            switch (tv.getId()){
                case R.id.loveDetail_titleET:
//                    String current=mTitle.get();
//                    if (!((null==text&&null==current)||(null!=text&&null!=current&&text.equals(current)))){
//                        mTitle.set((String) text);
//                    }
                    break;
            }
    }

    private boolean save(String debug){
        final String title=((EditText)findViewById(R.id.loveDetail_titleET)).getText().toString();
        if (null==title||title.length()<=0){
            return toast(R.string.title,getText(R.string.inputNotNull));
        }
        final String content=((EditText)findViewById(R.id.loveDetail_contentET)).getText().toString();
        if (null==content||content.length()<=0){
            return toast(R.string.content,getText(R.string.inputNotNull));
        }
        final Long planTime=(Long.parseLong(String.valueOf(((TextView)findViewById(R.id.loveDetail_valueTimeTV)).getText())));
        if (null==planTime||planTime<=0){
            return toast(R.string.planTime,getText(R.string.inputNotNull));
        }
        Debug.D(getClass(),"Save love "+title+" "+(null!=debug?debug:"."));
        final Love love=new Love(title, planTime, content,null);
        return null!=call(prepare(Api.class, Address.LOVE_ADDRESS, null).save(love), (OnApiFinish)( what,  note,  data,  arg)-> {
            toast(note);
        });
    }


    public ObservableField<Love> getLove() {
        return mLove;
    }

    public PhotoAdapter getPhotoAdapter() {
        return mPhotoAdapter;
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
                            mPhotoAdapter.add(new Photo(null,child));
                        }
                    }
                }
                break;
        }
    }
}
