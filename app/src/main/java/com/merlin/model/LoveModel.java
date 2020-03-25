package com.merlin.model;

import android.view.View;

import com.merlin.activity.LoveDetailActivity;
import com.merlin.adapter.LoveAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.SectionData;
import com.merlin.api.What;
import com.merlin.bean.Love;
import com.merlin.client.R;
import com.merlin.dialog.Dialog;
import com.merlin.server.Retrofit;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoveModel  extends Model implements OnTapClick,Label {

    private interface Api{
        @POST(Address.PREFIX_LOVE+"/get")
        @FormUrlEncoded
        Observable<Reply<SectionData<Love>>> getLoves(@Field(LABEL_NAME) String name,
                                                      @Field(LABEL_FROM) int from,@Field(LABEL_TO) int to);

        @POST(Address.PREFIX_LOVE+"/delete")
        @FormUrlEncoded
        Observable<Reply> delete(@Field(LABEL_ID) String id);
    }

    private final LoveAdapter mAdapter=new LoveAdapter() {
        @Override
        protected Retrofit.Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<SectionData<Love>>> finish) {
            return call(prepare(Api.class,Address.LOVE_ADDRESS,null).getLoves(arg,from,from+2000),finish);
        }

        @Override
        public void onItemSlideRemoved(int position, Object data) {
            Love love = null != data && data instanceof Love ? (Love) data : null;
            if (null != love) {
                final boolean[] deleted=new boolean[]{false};
                Dialog dialog=new Dialog(getViewContext()){
                    @Override
                    protected void onDismiss() {
                        super.onDismiss();
                        if (!deleted[0]){
                            add(position,love);
                        }
                    }
                };
                dialog.create().title(R.string.deleteSure).left(R.string.sure).right(R.string.cancel)
                        .show(( view,  clickCount,  resId, data2)-> {
                              dialog.dismiss();
                              if (resId ==R.string.sure){
                                  long id=null!=data&&data instanceof Love?((Love)data).getId():null;
                                  if (null==call(prepare(Api.class,Address.LOVE_ADDRESS,null).delete(Long.toString(id)),(OnApiFinish<Reply>)(what,note, d, arg)->{
                                      if (what!= What.WHAT_SUCCEED){
                                          deleted[0] =true;
                                          add(position,love);
                                      } })){
                                      add(position,love);
                                  }
                              }
                            return true;
                        });
            }
        }
    };

    public LoveModel(){
        mAdapter.loadPage(null,"While model create.");
    }


    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){
            case 1:
                return onSingleTap(view,resId,data);
        }
        return false;
    }

    private boolean onSingleTap(View view,int resId,Object data){
        switch (resId){
            case R.id.item_love:
                Long id=null!=data&&data instanceof Love?((Love)data).getId():null;
                return startActivity(LoveDetailActivity.class,Label.LABEL_ID,null!=id?Long.toString(id):null);
            case R.string.add:
                return startActivity(LoveDetailActivity.class,Label.LABEL_ID,null);
        }
        return false;
    }

    public LoveAdapter getAdapter() {
        return mAdapter;
    }

}
