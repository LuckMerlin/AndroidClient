package com.merlin.model;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.PageQuery;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.FileMeta;
import com.merlin.bean.Media;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.media.MediaPlayService;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @deprecated
 */
public final class MediaDisplayAlModel extends BaseModel implements Label,What, BaseModel.OnModelViewClick, BaseAdapter.OnItemClickListener<FileMeta> {
    private PageQuery<String> mQuerying;
    private PageData<FileMeta> mLatestQueried=null;

    private final MediaAdapter mAdapter=new MediaAdapter(){
        @Override
        public boolean onLoadMore(RecyclerView recyclerView, int state, String debug) {
            PageData<FileMeta> query=mLatestQueried;
            return null!=query&&queryAllMedias(query.getPage()+1);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<PageData<FileMeta>>> queryAllMedias(@Field(LABEL_FORMAT) String format,
                                                          @Field(LABEL_PAGE)int page,
                                                          @Field(LABEL_LIMIT)int limit);
        @POST(Address.PREFIX_FILE+"/favorite")
        @FormUrlEncoded
        Observable<Reply<FileMeta>> makeFavorite(@Field(LABEL_MD5) String md5,@Field(LABEL_DATA) boolean favorite );
    }


    public MediaDisplayAlModel(Context context){
        super(context);
        mAdapter.setOnItemClickListener(this);
        queryAllMedias(0);
    }

    private boolean queryAllMedias(int page){
        return queryAllMedias(null,page);
    }

    private boolean queryAllMedias(String format,int page){
        return queryAllMedias(new PageQuery(format,page, 10));
    }

    private boolean makeFavorite(String md5,boolean favorite){
        if (null==md5||md5.length()<=0){
            return false;
        }
        Debug.D(getClass(),"favorite "+favorite);
        return null!=call(Api.class,(OnApiFinish<Reply<FileMeta>>)(what, note, data, arg)->{
            MediaAdapter adapter=mAdapter;
            if (what==WHAT_SUCCEED&&null!=data){
                adapter.notifyFavoriteChange(md5, favorite);
            }
        }).makeFavorite(md5,favorite);
    }

    private boolean queryAllMedias(PageQuery<String> query){
        if (null==query){
            return false;
        }
        PageQuery<String> current=mQuerying;
        if (null!=current&&current.equals(query)){
            return false;
        }
        String filter="我";
        mQuerying=query;
        return null!= call(Api.class, (OnApiFinish<Reply<PageData<FileMeta>>>)(what, note, data, arg)->{
            PageQuery<String> newCurrent=mQuerying;
            if (null!=newCurrent&&newCurrent.equals(query)){
                mQuerying=null;
                if (what== What.WHAT_SUCCEED){
                    PageData<FileMeta> pageData=null!=data?data.getData():null;
                    mLatestQueried=pageData;
                    mAdapter.fillPage(pageData);
                }
            }
        }).queryAllMedias(filter, query.getPage(), query.getLimit());
    }

    @Override
    public void onViewClick(View v, int id, Object object) {
        switch (id){
            case R.id.itemMedia_favoriteIV:
                if (null!=v&&null!=object&&object instanceof FileMeta){
                    makeFavorite(((FileMeta)object).getMd5(),!v.isSelected());
                }
                break;

        }
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, FileMeta data) {
        Media media=null!=data?data.getMeta():null;
        if (null!=media&&null!=data&&null!=view){
            MediaPlayService.play(view.getContext(),media,0,false);
        }
    }

    public MediaAdapter getAdapter() {
        return mAdapter;
    }
}
