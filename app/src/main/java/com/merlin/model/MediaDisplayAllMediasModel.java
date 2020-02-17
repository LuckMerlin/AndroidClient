package com.merlin.model;
import android.view.View;
import android.widget.EditText;

import com.merlin.adapter.AllMediasAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.File;
import com.merlin.bean.Media;
import com.merlin.client.R;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public final class MediaDisplayAllMediasModel extends Model implements OnTapClick,Label,What,OnTextChange {
    private final AllMediasAdapter mAdapter=new AllMediasAdapter() {
        @Override
        protected boolean onPageLoad(String name, int page, OnApiFinish<Reply<PageData<Media>>> finish) {
            return null!=call(Api.class,finish).queryAllMedias(page,50,name);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<PageData<Media>>> queryAllMedias(@Field(LABEL_PAGE) int page, @Field(LABEL_LIMIT) int limit,
                                                          @Field(LABEL_NAME) String name,
                                                          @Field(LABEL_FORMAT) String... formats);
        @POST(Address.PREFIX_MEDIA+"/favorite")
        @FormUrlEncoded
        Observable<Reply<Media>> makeFavorite(@Field(LABEL_MD5) String md5, @Field(LABEL_DATA) boolean favorite);
    }

    public MediaDisplayAllMediasModel(){
        queryAllMedias("","While model create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.id.itemMediaAll_favoriteIV:
                return null!=data&&null!=view&&data instanceof Media &&
                        makeFavorite((Media)data,!view.isSelected());
            case R.id.itemMediaAll_rootRL:
//                if (null!=view&&null!=data&&data instanceof  &&(clickCount==1||clickCount==2)){
//                    return MediaPlayService.play(view.getContext(),(Media)data,0,clickCount==2);
//                }
                return false;
        }
        return false;
    }

    private boolean makeFavorite(Media meta, boolean favorite){
        final String md5=null!=meta?meta.getMd5():null;
        if (null==md5||md5.length()<=0){
            return false;
        }
        return null!=call(Api.class,(OnApiFinish<Reply<File>>)(what, note, data, arg)->{
            AllMediasAdapter adapter=mAdapter;
            toast(note);
            if (what==WHAT_SUCCEED&&null!=data){
                adapter.notifyFavoriteChange(md5, favorite);
            }
        }).makeFavorite(md5,favorite);
    }

    private boolean queryAllMedias(String name,String debug){
        AllMediasAdapter adapter=mAdapter;
       return  null!=adapter&&adapter.loadPage(name,debug);
    }

    @Override
    public void onTextChanged(EditText et, CharSequence s, int start, int before, int count) {
           queryAllMedias(null!=s&&s.length()>0?""+s:"","After text change.");
    }


    public AllMediasAdapter getAdapter() {
        return mAdapter;
    }
}
