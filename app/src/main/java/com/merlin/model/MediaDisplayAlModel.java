package com.merlin.model;

import android.view.View;

import com.merlin.adapter.BaseAdapter;
import com.merlin.adapter.MediaAdapter;
import com.merlin.api.Address;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageQuery;
import com.merlin.api.Reply;
import com.merlin.api.PageData;
import com.merlin.api.What;
import com.merlin.bean.INasFile;
import com.merlin.bean.INasMedia;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @deprecated
 */
public final class MediaDisplayAlModel extends Model implements Label,What, BaseAdapter.OnItemClickListener<INasFile> {
    private PageQuery<String> mQuerying;
    private PageData<INasFile> mLatestQueried=null;

    private final MediaAdapter mAdapter=new MediaAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int page, OnApiFinish<Reply<PageData<INasFile>>> finish) {
            return call(prepare(Api.class,Address.HOST).queryAllMedias(arg,page,page+20));
        }
    };

    private interface Api{
        @POST(Address.PREFIX_MEDIA_PLAY+"/media/all")
        @FormUrlEncoded
        Observable<Reply<PageData<INasFile>>> queryAllMedias(@Field(LABEL_FORMAT) String format,
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
    public void onItemClick(View view, int sourceId, int position, INasFile data) {
        INasMedia media=null!=data?data.getMeta():null;
        if (null!=media&&null!=data&&null!=view){
//            MediaPlayService.play(view.getContext(),media,0,false);
        }
    }

    public MediaAdapter getAdapter() {
        return mAdapter;
    }
}
