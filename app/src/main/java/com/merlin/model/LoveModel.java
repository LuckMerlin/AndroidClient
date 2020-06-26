package com.merlin.model;

import android.view.View;

import com.merlin.activity.LoveDetailActivity;
import com.merlin.adapter.LoveAdapter;
import com.merlin.api.Address;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.PageData;
import com.merlin.api.What;
import com.merlin.bean.INasFile;
import com.merlin.bean.Love;
import com.merlin.bean.NasFile;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.view.OnTapClick;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class LoveModel  extends Model implements OnTapClick,Label {

    private interface Api{
        @POST(Address.PREFIX_LOVE+"/get")
        @FormUrlEncoded
        Observable<Reply<PageData<Love>>> getLoves(@Field(LABEL_NAME) String name,
                                                            @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);

        @POST(Address.PREFIX_LOVE+"/delete")
        @FormUrlEncoded
        Observable<Reply> delete(@Field(LABEL_ID) String id);
    }

    private final LoveAdapter mAdapter=new LoveAdapter() {

        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Love>>> finish) {
            return call(prepare(Api.class,Address.LOVE_URL,null).getLoves(arg,from,from+2000),finish);
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
                            insert(position,love,"After delete fail.");
                        }
                    }
                };
                String msg=love.getName();
                msg=null!=msg?msg:love.getTitle();
                dialog.create().title(getText(R.string.deleteSure,msg)).left(R.string.sure).right(R.string.cancel)
                        .show(( view,  clickCount,  resId, data2)-> {
                              dialog.dismiss();
                              if (resId ==R.string.sure){
                                  remove(love,"while delete.");
                                  long id=null!=data&&data instanceof Love?((Love)data).getId():null;
                                  if (null==call(prepare(Api.class,Address.LOVE_URL,null).delete(Long.toString(id)),(OnApiFinish<Reply>)(what,note, d, arg)->{
                                      boolean succeed= deleted[0] =what== What.WHAT_SUCCEED;
                                      if (!succeed){
                                          insert(position,love,"After delete fail.");
                                      } })){
                                      insert(position,love,"After delete fail.");
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
                Love love=null!=data&&data instanceof Love?((Love)data):null;
                String id=null;
                if (null!=love){
                    id=""+love.getId();
                }
                return startActivity(LoveDetailActivity.class,id);
            case R.string.add:
                return startActivity(LoveDetailActivity.class,"");
        }
        return false;
    }

    public LoveAdapter getAdapter() {
        return mAdapter;
    }

}
