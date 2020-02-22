package com.merlin.model;

import android.content.Context;
import android.view.View;

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
import com.merlin.bean.NasFile;
import com.merlin.bean.NasMedia;
import com.merlin.client.R;
import com.merlin.debug.Debug;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @deprecated
 */
public final class MediaDisplayAlModel extends BaseModel implements Label,What, BaseModel.OnModelViewClick, BaseAdapter.OnItemClickListener<NasFile> {
    private PageQuery<String> mQuerying;
    private PageData<NasFile> mLatestQueried=null;

    private final MediaAdapter mAdapter=new MediaAdapter(){
        @Override
        public boolean onLoadMore(RecyclerView recyclerView, int state, String debug) {
            PageData<NasFile> query=mLatestQueried;
            return null!=query&&queryAllMedias(query.getPage()+1);
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<PageData<NasFile>>> queryAllMedias(@Field(LABEL_FORMAT) String format,
                                                            @Field(LABEL_PAGE)int page,
                                                            @Field(LABEL_LIMIT)int limit);
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
        return null!= call(Api.class, (OnApiFinish<Reply<PageData<NasFile>>>)(what, note, data, arg)->{
            PageQuery<String> newCurrent=mQuerying;
            if (null!=newCurrent&&newCurrent.equals(query)){
                mQuerying=null;
                if (what== What.WHAT_SUCCEED){
                    PageData<NasFile> pageData=null!=data?data.getData():null;
                    mLatestQueried=pageData;
                    mAdapter.fillPage(pageData);
                }
            }
        }).queryAllMedias(filter, query.getPage(), query.getLimit());
    }

    @Override
    public void onViewClick(View v, int id, Object object) {
        switch (id){
//            case R.id.itemMedia_favoriteIV:
//                if (null!=v&&null!=object&&object instanceof NasFile){
//                    makeFavorite(((NasFile)object).getMd5(),!v.isSelected());
//                }
//                break;

        }
    }

    @Override
    public void onItemClick(View view, int sourceId, int position, NasFile data) {
        NasMedia media=null!=data?data.getMeta():null;
        if (null!=media&&null!=data&&null!=view){
//            MediaPlayService.play(view.getContext(),media,0,false);
        }
    }

    public MediaAdapter getAdapter() {
        return mAdapter;
    }
}
