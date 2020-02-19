package com.merlin.model;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.ViewDataBinding;

import com.merlin.adapter.AllMediasAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.File;
import com.merlin.bean.Media;
import com.merlin.bean.Sheet;
import com.merlin.client.BR;
import com.merlin.client.R;
import com.merlin.client.databinding.MediasAllContextMenuBinding;
import com.merlin.dialog.Dialog;
import com.merlin.media.MediaPlayService;
import com.merlin.player1.MPlayer;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;
import com.merlin.view.Res;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public final class MediaDisplayAllMediasModel extends Model implements OnTapClick, OnLongClick,Label,What,OnTextChange {
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

        @POST(Address.PREFIX_MEDIA+"/play/addMediaIntoSheet")
        @FormUrlEncoded
        Observable<Reply<Media>> addIntoSheet(@Field(LABEL_MD5) String md5, @Field(LABEL_ID) String sheet_id);

    }

    public MediaDisplayAllMediasModel(){
        queryAllMedias("","While model create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.id.itemMediaAll_favoriteIV:
                return (null!=data&&null!=view&&data instanceof Media&&makeFavorite((Media)data,!view.isSelected()))|true;
            case R.string.play://Get through
                return (null!=data&&null!=view&&data instanceof Media&&play((Media)data,false))||true;
            case R.string.orderNext://Get through
                return (null!=data&&null!=view&&data instanceof Media&&play((Media)data,true))||true;
            case R.string.addToSheet://Get through
                return (null!=data&&null!=view&&data instanceof Media&&addToSheet((Media)data))||true;
        }
        return true;
    }

    private boolean play(Media media,boolean append){
        return MediaPlayService.play(getViewContext(),media,0, append?MPlayer.PLAY_TYPE_ORDER_INTO_NEXT:MPlayer.PLAY_TYPE_PLAY_NOW);
    }

    private boolean addToSheet(Media media){
        final String md5=null!=media?media.getMd5():null;
        if (null!=md5&&md5.length()>0){
            Dialog dialog=new Dialog(getViewContext());
            ViewDataBinding binding=inflate(R.layout.media_sheet_choose,new Res(BR.media,media));
//            return showAtLocation(getRoot(),inflate(R.layout.media_sheet_choose,new Res(BR.media,media)),PopupWindow.DISMISS_OUT_MASK);
            return dialog.setContentView(binding).title(R.string.addToSheet).right(R.string.cancel).
                    show((view,clickCount,resId,data)->{
                        dialog.dismiss();
                        String sheetId=null!=data&&data instanceof Sheet?((Sheet)data).getId():null;
                        if (null!=sheetId&&sheetId.length()>0){
                            call(Api.class,(OnApiFinish<Reply<Media>>)(what,note,m,arg)->{
                                toast(note);
                            }).addIntoSheet(md5,sheetId);
                        }
                        return false;},false);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        if (null!=data&&data instanceof Media){
            MediasAllContextMenuBinding binding=inflate(R.layout.medias_all_context_menu);
            binding.setMedia((Media)data);
            showAtLocationAsContext(view,binding);
            return true;
        }
        return true;
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
