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
import com.merlin.api.SectionData;
import com.merlin.api.What;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasMedia;
import com.merlin.server.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @deprecated
 */
public final class MediaDisplayAlModel extends Model implements Label,What, BaseAdapter.OnItemClickListener<NasFile> {
    private PageQuery<String> mQuerying;
    private SectionData<NasFile> mLatestQueried=null;

    private final MediaAdapter mAdapter=new MediaAdapter(){
        @Override
        protected Retrofit.Canceler onPageLoad(String arg, int page, OnApiFinish<Reply<SectionData<NasFile>>> finish) {
            return call(prepare(Api.class).queryAllMedias(arg,page,page+20));
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<SectionData<NasFile>>> queryAllMedias(@Field(LABEL_FORMAT) String format,
                                                            @Field(LABEL_PAGE)int page,
                                                            @Field(LABEL_LIMIT)int limit);
    }


    public MediaDisplayAlModel(){
        queryAllMedias(0);
    }

    private boolean queryAllMedias(int page){
        return queryAllMedias(null,page);
    }

    private boolean queryAllMedias(String format,int page){
        return queryAllMedias(new PageQuery(format,page, 10));
    }


    private boolean queryAllMedias(PageQuery<String> query){
//        if (null==query){
//            return false;
//        }
//        PageQuery<String> current=mQuerying;
//        if (null!=current&&current.equals(query)){
//            return false;
//        }
//        String filter="我";
//        mQuerying=query;
//        return null!= call(Api.class, (OnApiFinish<Reply<SectionData<NasFile>>>)(what, note, data, arg)->{
//            PageQuery<String> newCurrent=mQuerying;
//            if (null!=newCurrent&&newCurrent.equals(query)){
//                mQuerying=null;
//                if (what== What.WHAT_SUCCEED){
//                    SectionData<NasFile> pageData=null!=data?data.getData():null;
//                    mLatestQueried=pageData;
////                    mAdapter.fillPage(pageData);
//                }
//            }
//        }).queryAllMedias(filter, query.getPage(), query.getLimit());
        return false;
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
