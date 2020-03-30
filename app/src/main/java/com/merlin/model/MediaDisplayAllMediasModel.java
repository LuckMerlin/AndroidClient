package com.merlin.model;
import android.view.View;
import android.widget.EditText;

import com.merlin.adapter.AllMediasAdapter;
import com.merlin.adapter.MediaPlayDisplayAdapter;
import com.merlin.api.Address;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.PageData;
import com.merlin.api.What;
import com.merlin.bean.File_;
import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.media.FavoriteApi;
import com.merlin.media.MediaPlayService;
import com.merlin.player1.MPlayer;
import com.merlin.server.Retrofit;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public final class MediaDisplayAllMediasModel extends Model implements OnTapClick, OnLongClick,Label,What,OnTextChange, MediaPlayDisplayAdapter.OnMediaPlayModelShow {
    private String mFilter;

    private final AllMediasAdapter mAdapter=new AllMediasAdapter() {
        @Override
        protected Canceler onPageLoad(String name, int from, OnApiFinish<Reply<PageData<NasMedia>>> finish) {
            return call(prepare(Api.class,Address.HOST).queryAllMedias(from,from+20,name),finish);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<PageData<NasMedia>>> queryAllMedias(@Field(LABEL_FROM) int from, @Field(LABEL_TO) int to,
                                                             @Field(LABEL_NAME) String name,
                                                             @Field(LABEL_FORMAT) String... formats);
    }

    public MediaDisplayAllMediasModel(){
        queryAllMedias(null,"While model create.");
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.drawable.selector_menu:
                return showContextMenu(view);
            case R.string.playAll:
                return playAll("After play all tap click.");
            case R.drawable.selector_heart:
                return (null!=data&&null!=view&&data instanceof NasMedia &&makeFavorite((NasMedia)data,!view.isSelected()))|true;
            case R.string.play://Get through
                return (null!=data&&null!=view&&data instanceof NasMedia &&play((NasMedia)data,MPlayer.PLAY_TYPE_PLAY_NOW))||true;
            case R.string.orderNext://Get through
                return (null!=data&&null!=view&&data instanceof NasMedia &&play((NasMedia)data,MPlayer.PLAY_TYPE_ORDER_NEXT))||true;
            case R.string.addToSheet://Get through
                return (null!=data&&null!=view&&data instanceof NasMedia &&addToSheet((NasMedia)data))||true;
            default:
                if (null!=data&&data instanceof NasMedia) {
                    NasMedia media=(NasMedia)data;
                    switch (clickCount) {
                        case 1:
                            return play(media,MPlayer.PLAY_TYPE_PLAY_NOW);
                        case 2:
                            return play(media,MPlayer.PLAY_TYPE_PLAY_NOW|MPlayer.PLAY_TYPE_ADD_INTO_QUEUE);
                        case 3:
                            return play(media,MPlayer.PLAY_TYPE_ORDER_NEXT);
                        default:
                            return play(media,MPlayer.PLAY_TYPE_ORDER_NEXT|MPlayer.PLAY_TYPE_ADD_INTO_QUEUE);
                    }
                }
                break;
        }
        return true;
    }

    private boolean showContextMenu(View view){
//        MediasAllMenusBinding binding=null!=view?inflate(R.layout.medias_all_menus):null;
//        if (null!=binding){
//            AllMediasAdapter adapter=mAdapter;
//            binding.setMediaCount(null!=adapter?adapter.getDataCount():0);
//            return showAtLocationAsContext(view,binding);
//        }
        return false;
    }

    private boolean playAll(String debug){
        AllMediasAdapter adapter=mAdapter;
        ArrayList<NasMedia> list=null!=adapter?adapter.getData():null;
        if (null==list||list.size()<=0){
            return toast(R.string.listEmpty);
        }
        return MediaPlayService.play(getViewContext(),list,0,(MPlayer.PLAY_TYPE_PLAY_NOW|MPlayer.PLAY_TYPE_ADD_INTO_QUEUE|MPlayer.PLAY_TYPE_CLEAN_QUEUE));
    }

    private boolean play(NasMedia media, int playType){
        return MediaPlayService.play(getViewContext(),media,0,playType);
    }

    private boolean addToSheet(NasMedia media){
        final String md5=null!=media?media.getMd5():null;
        if (null!=md5&&md5.length()>0){
//            Dialog dialog=new Dialog(getViewContext());
//            ViewDataBinding binding=inflate(R.layout.media_sheet_choose,new Res(BR.media,media));
//           return dialog.setContentView(binding).title(R.string.addToSheet).left(R.string.createSheet).right(R.string.cancel).
//                    show((view,clickCount,resId,data)->{
//                        dialog.dismiss();
//                        String sheetId=null!=data&&data instanceof Sheet?((Sheet)data).getId():null;
//                        if (null!=sheetId&&sheetId.length()>0){
//                            return null!=call(prepare(AddToSheetApi.class).addIntoSheet(md5,sheetId),(OnApiFinish<Reply<NasMedia>>)(what, note, m, arg)->{
//                                toast(note);
//                            })||true;
//                        }
//                        return false;},false);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        if (null!=data&&data instanceof NasMedia){
//            MediasAllContextMenuBinding binding=inflate(R.layout.medias_all_context_menu);
//            binding.setMedia((NasMedia)data);
//            showAtLocationAsContext(view,binding);
            return true;
        }
        return true;
    }

    private boolean makeFavorite(NasMedia meta, boolean favorite){
        final String md5=null!=meta?meta.getMd5():null;
        if (null==md5||md5.length()<=0){
            return false;
        }
        return null!=call(prepare(FavoriteApi.class,Address.HOST).makeFavorite(md5,favorite),(OnApiFinish<Reply<File_>>)(what, note, data, arg)->{
            AllMediasAdapter adapter=mAdapter;
            if (what==WHAT_SUCCEED&&null!=data){
                adapter.notifyFavoriteChange(md5, favorite);
            }else{
                toast(note);
            }
        });
    }

    private boolean queryAllMedias(String name,String debug){
        AllMediasAdapter adapter=mAdapter;
       return  null!=adapter&&adapter.loadPage(name,debug);
    }

    @Override
    public void onTextChanged(EditText et, CharSequence s, int start, int before, int count) {
           queryAllMedias(mFilter=null!=s&&s.length()>0?""+s:null,"After title_text change.");
    }

    public AllMediasAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onMediaPlayModelShow() {
        queryAllMedias(mFilter,"After media model show.");
    }
}
