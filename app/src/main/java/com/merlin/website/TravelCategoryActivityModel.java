package com.merlin.website;

import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.adapter.WebsiteBannerAdapter;
import com.merlin.adapter.WebsiteCategoriesAdapter;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.model.Model;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class TravelCategoryActivityModel extends Model implements Label{
    private String mUrl;
    private interface Api{
        @POST("/travel/category")
        @FormUrlEncoded
        Observable<Reply<PageData<TravelCategory>>> getCategories(@Field(LABEL_WHAT) String type, @Field(LABEL_NAME) String name, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);

        @POST("/travel/category/create")
        @FormUrlEncoded
        Observable<Reply<TravelCategory>> createCategory(@Field(LABEL_ID) Long id,@Field(LABEL_TITLE) String title,
                                                         @Field(LABEL_BANNER) boolean banner,@Field(LABEL_NOTE) String note,
                                                         @Field(LABEL_URL) Integer coverId);
    }

    private ObservableField<RecyclerView.Adapter> mAdapter=new ObservableField<>();
    private final WebsiteBannerAdapter mImagesAdapter=new WebsiteBannerAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
            return call(prepare(Api.class,mUrl).getCategories(null,arg,from,from+10),finish);
        }
    };

    private final WebsiteCategoriesAdapter mCategoriesAdapter=new WebsiteCategoriesAdapter(mUrl){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<TravelCategory>>> finish) {
            return call(prepare(Api.class,mUrl).getCategories(Label.LABEL_BANNER,arg,from,from+10),finish);
        }
    };

    private boolean queryCategories(){
        WebsiteCategoriesAdapter categoriesAdapter=mCategoriesAdapter;
        return null!=categoriesAdapter&&categoriesAdapter.loadPage("","");
    }

    public boolean queryImages(){
        WebsiteBannerAdapter bannerAdapter=mImagesAdapter;
        return null!=bannerAdapter&&bannerAdapter.loadPage("","");
    }

    public boolean createCategory(){
//        Dialog dialog=new Dialog(getViewContext());
        call(prepare(Api.class,mUrl).createCategory(0l,"林强37",true,"Note", 1));
//        return dialog.show();
        return false;
    }

}
