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
import com.merlin.bean.FileMeta;
import com.merlin.bean.Media;
import com.merlin.client.R;
import com.merlin.media.MediaPlayService;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public final class MediaDisplayAllMediasModel extends Model implements OnTapClick,Label,What,OnTextChange {
    private final AllMediasAdapter mAdapter=new AllMediasAdapter() {
        @Override
        protected boolean onPageLoad(String filter, int page, OnApiFinish<Reply<PageData<FileMeta>>> finish) {
            return null!=call(Api.class,finish).queryAllMedias(filter,page,50);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<PageData<FileMeta>>> queryAllMedias(@Field(LABEL_NAME) String name,
                                                             @Field(LABEL_PAGE) int page,
                                                             @Field(LABEL_LIMIT) int limit);
        @POST(Address.PREFIX_FILE+"/favorite")
        @FormUrlEncoded
        Observable<Reply<Media>> makeFavorite(@Field(LABEL_PATH) String path, @Field(LABEL_DATA) boolean favorite);
    }

    public MediaDisplayAllMediasModel(){
        queryAllMedias("","While model create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.id.itemMediaAll_favoriteIV:
                return null!=data&&null!=view&&data instanceof Media&&
                        makeFavorite((Media)data,!view.isSelected());
            case R.id.itemMediaAll_rootRL:
                if (null!=view&&null!=data&&data instanceof Media&&(clickCount==1||clickCount==2)){
                    return MediaPlayService.play(view.getContext(),(Media)data,0,clickCount==2);
                }
                return false;
        }
        return false;
    }

    private boolean makeFavorite(Media meta, boolean favorite){
        final String path=null!=meta?meta.getPath():null;
        if (null==path||path.length()<=0){
            return false;
        }
        return null!=call(Api.class,(OnApiFinish<Reply<Media>>)(what, note, data, arg)->{
            AllMediasAdapter adapter=mAdapter;
            if (what==WHAT_SUCCEED&&null!=data){
                adapter.notifyFavoriteChange(path, favorite);
            }
        }).makeFavorite(path,favorite);
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
