package com.merlin.website;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import com.merlin.activity.LocalPhotoChooseActivity;
import com.merlin.adapter.WebsiteCategoriesAdapter;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.bean.Path;
import com.merlin.client.R;
import com.merlin.client.databinding.SingleEditTextBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.model.Model;
import com.merlin.view.OnTapClick;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class TravelCategoryActivityModel extends Model implements OnTapClick,Label, Model.OnActivityResult, Model.OnActivityBackPress {
    private String mUrl=WebsiteModel.mUrl;
    private final static int PHOTO_CHOOSE_REQUEST_CODE=123141;
    private final ObservableField<TravelCategory> mCategory=new ObservableField<>();
    private interface Api{
        @POST("/travel/category")
        @FormUrlEncoded
        Observable<Reply<PageData<TravelCategory>>> getCategories(@Field(LABEL_WHAT) String type, @Field(LABEL_NAME) String name, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);

        @POST("/travel/category/create")
        @FormUrlEncoded
        Observable<Reply<TravelCategory>> createCategory(@Field(LABEL_ID) Long id,@Field(LABEL_TITLE) CharSequence title,@Field(LABEL_BANNER) boolean banner,@Field(LABEL_NOTE) String note, @Field(LABEL_URL) Integer coverId);
    }

//    private final WebsiteBannerAdapter mImagesAdapter=new WebsiteBannerAdapter(){
//        @Override
//        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
//            return call(prepare(Api.class,mUrl).getCategories(null,arg,from,from+10),finish);
//        }
//    };

    private final WebsiteCategoriesAdapter mCategoriesAdapter=new WebsiteCategoriesAdapter(mUrl){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<TravelCategory>>> finish) {
            return call(prepare(Api.class,mUrl).getCategories(null,arg,from,from+10),finish);
        }
    };

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
        queryCategories();
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (resId){
            case R.string.add:
                ObservableField<TravelCategory> categoryField=mCategory;
                TravelCategory category=null!=categoryField?categoryField.get():null;
                return (null!=category?startActivity(LocalPhotoChooseActivity.class,PHOTO_CHOOSE_REQUEST_CODE)
                        :createCategory())||true;
            default:
                if (null!=data&&data instanceof TravelCategory){
                    mCategory.set((TravelCategory)data);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        ObservableField<TravelCategory> category=mCategory;
        if (null!=category&&null!=category.get()){
            category.set(null);
            return true;
        }
        return false;
    }

    private boolean queryCategories(){
        WebsiteCategoriesAdapter categoriesAdapter=mCategoriesAdapter;
        return null!=categoriesAdapter&&categoriesAdapter.loadPage("","");
    }

    private boolean createCategory(){
        Dialog dialog=new Dialog(getViewContext());
        SingleEditTextBinding binding=inflate(R.layout.single_edit_text);
        return dialog.setContentView(binding,true).title(getText(R.string.gallery)).left(R.string.sure)
                .right(R.string.cancel).show((View view, int clickCount, int resId, Object data)-> {
                    View root = null != binding ? binding.getRoot() : null;
                    CharSequence text = null != root && root instanceof TextView ? ((TextView) root).getText() : null;
                    if (resId==R.string.sure&&(null == text || text.length() <= 0)) {
                        return toast(R.string.inputNotNull) || true;
                    }
                    dialog.dismiss();
                return (resId==R.string.sure&&null!=call(prepare(Api.class,mUrl).createCategory(null,text,false,null, null),
                        (OnApiFinish<Reply<TravelCategory>>)(int what, String note, Reply<TravelCategory> reply, Object arg)-> {
                        toast(note); }))||true;
        });
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (requestCode){
           case PHOTO_CHOOSE_REQUEST_CODE:
               Bundle bundle=null!=data?data.getExtras():null;
               Object object=null!=bundle?bundle.get(Label.LABEL_DATA):null;
               if (null!=object&&object instanceof ArrayList) {
                   List<Path> paths=new ArrayList<>();
                   for (Object child : (ArrayList) object) {
                       if (null != child&&child instanceof Path) {
                           paths.add((Path)child);
                       }
                   }
//                   uploadFiles(paths);
               }
               break;
        }
    }

    public WebsiteCategoriesAdapter getCategoriesAdapter() {
        return mCategoriesAdapter;
    }

    public ObservableField<TravelCategory> getCategory() {
        return mCategory;
    }
}
