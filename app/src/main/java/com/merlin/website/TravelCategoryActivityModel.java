package com.merlin.website;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.ObservableField;

import com.merlin.activity.LocalPhotoChooseActivity;
import com.merlin.adapter.ListAdapter;
import com.merlin.adapter.PhotoGridAdapter;
import com.merlin.adapter.WebsiteCategoriesAdapter;

import com.merlin.api.ApiList;
import com.merlin.api.ApiSaveFile;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.browser.FileSaveBuilder;
import com.merlin.client.R;
import com.merlin.client.databinding.SingleEditTextBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.model.Model;
import com.merlin.view.Clicker;
import com.merlin.view.OnTapClick;
import com.merlin.view.Res;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class TravelCategoryActivityModel extends Model implements OnTapClick,Label, Model.OnActivityResult, Model.OnActivityBackPress {
    private String mUrl=WebsiteModel.mUrl;
    private final static int PHOTO_CHOOSE_REQUEST_CODE=123141;
    private final static int COVER_PHOTO_CHOOSE_REQUEST_CODE=123142;
    private final ObservableField<TravelCategory> mCategory=new ObservableField<>();

    private interface Api{
        @POST("/travel/category")
        @FormUrlEncoded
        Observable<Reply<PageData<TravelCategory>>> getCategories(@Field(LABEL_WHAT) String type, @Field(LABEL_NAME) String name, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);

        @POST("/travel/category/save")
        @FormUrlEncoded
        Observable<Reply<TravelCategory>> saveCategory(@Field(LABEL_ID) String id,@Field(LABEL_URL) Long coverId,@Field(LABEL_TITLE) CharSequence title,
                                                         @Field(LABEL_BANNER) boolean banner,@Field(LABEL_NOTE) CharSequence note);
        @POST("/travel/category/photo")
        @FormUrlEncoded
        Observable<Reply<PageData<Path>>> getCategoryPhoto(@Field(LABEL_ID) String categoryId, @Field(LABEL_FROM) int from, @Field(LABEL_TO) int to);

    }

    private final WebsiteCategoriesAdapter mCategoriesAdapter=new WebsiteCategoriesAdapter(){
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<TravelCategory>>> finish) {
            return call(prepare(Api.class,mUrl).getCategories(null,arg,from,from+10),finish);
        }
    };

    private final WebsiteCategoryPhotoAdapter mPhotoAdapter=new WebsiteCategoryPhotoAdapter() {
        @Override
        protected Canceler onPageLoad(String arg, int from, OnApiFinish<Reply<PageData<Path>>> finish) {
            return call(prepare(Api.class,mUrl).getCategoryPhoto(arg,from,from+10),finish);
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
                return (null!=category?startActivity(WebsitePhotosActivity.class,PHOTO_CHOOSE_REQUEST_CODE)
                        :createCategory())||true;
            case R.string.save:
                return save()||true;
            case R.id.websiteTravelCategory_coverIV:
                return choosePhoto(clickCount);
            default:
                if (null!=data){
                    if(data instanceof TravelCategory){
                        showCategory((TravelCategory)data);
                    }else{

                    }
                }
                break;
        }
        return true;
    }

    private void showCategory(TravelCategory category){
        mCategory.set(category);
        mPhotoAdapter.loadPage(""+category.getId(),null);
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
                return (resId==R.string.sure&&null!=call(prepare(Api.class,mUrl).saveCategory(null,null,text,false,null),
                        (OnApiFinish<Reply<TravelCategory>>)(int what, String note, Reply<TravelCategory> reply, Object arg)-> {
                        toast(note); }))||true;
        });
    }

    private boolean save(){
        TravelCategory category=mCategory.get();
        Long id=null!=category?category.getId():null;
        if (null==id){
            return toast("没有选择相册");
        }
        View view=findViewById(R.id.websiteTravelCategory_coverIV);
        Res res=null!=view?Clicker.getRes(view,null):null;
        Object coverObj=null!=res?res.getArg():null;
        Path coverPath=null!=coverObj&&coverObj instanceof Path?((Path)coverObj):null;
//        String coverPathValue=null!=coverPath?coverPath.getPath():null;
//        FileSaveBuilder builder=new FileSaveBuilder();
//        if (coverPath.isLocal()){
//            File coverFile=null!=coverPathValue&&coverPathValue.length()>0?new File(coverPathValue):null;
//            if (null==coverFile||!coverFile.exists()){
//                return toast("封面文件不存在");
//            }else if (!coverFile.isFile()||coverFile.length()<=0){
//                return toast("封面文件不合法");
//            }
//            Dialog dialog=new Dialog(getViewContext());
//            dialog.setContentView(R.layout.dialog_loading,false).setCanceledOnTouchOutside(true).show();
//            MultipartBody.Part part=builder.createFilePart(coverFile,null,"website");
//            prepare(ApiSaveFile.class,mUrl).save(part).enqueue(new Callback<Reply<ApiList<Reply<Path>>>>() {
//                @Override
//                public void onResponse(Call<Reply<ApiList<Reply<Path>>>> call, Response<Reply<ApiList<Reply<Path>>>> response) {
//                    dialog.dismiss();
//                    Reply<ApiList<Reply<Path>>> reply=null!=response?response.body():null;
//                    ApiList<Reply<Path>> list=null!=reply?reply.getData():null;
//                    Reply<Path>  pathReply=null!=list&&list.size()>0?list.get(0):null;
//                    Path replyPath=null!=pathReply?pathReply.getData():null;
//                    Long coverId=replyPath.getId();
//                    if (null==id||id<0){
//                        toast("封面上传失败");
//                    }else{
//                        saveCategory(""+id,coverId);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Reply<ApiList<Reply<Path>>>> call, Throwable t) {
//                    dialog.dismiss();
//                    toast("封面上传失败");
//                }
//            });
//            return true;
//        }
        return saveCategory(""+id,category.getUrlId());
    }

    private boolean saveCategory(String id,Long coverId){
        return null!=call(prepare(Api.class, mUrl).saveCategory(id, coverId,getViewText(R.id.websiteTravelCategory_titleET,null),
                isViewChecked(R.id.websiteTravelCategory_bannerCB,false),getViewText(R.id.websiteTravelCategory_noteET,null)),
                (OnApiFinish<Reply<TravelCategory>>)(int what, String note, Reply<TravelCategory> data, Object arg)-> {
            toast(note);
            if (null!=data&&data.isSuccess()&&data.getWhat()== What.WHAT_SUCCEED){
                TravelCategory newCategory=data.getData();
                mCategory.set(null);
                if (null!=newCategory){
                    mCategoriesAdapter.replace(newCategory,"After save succeed.");
                }
            }
        });
    }

    private boolean choosePhoto(int clickCount){
        return startActivity(WebsitePhotosActivity.class,COVER_PHOTO_CHOOSE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case COVER_PHOTO_CHOOSE_REQUEST_CODE:
            case PHOTO_CHOOSE_REQUEST_CODE:
                Bundle bundle=null!=data?data.getExtras():null;
                Object object=null!=bundle?bundle.get(Label.LABEL_DATA):null;
                if (null!=object&&object instanceof List&&((List)object).size()>0){
                    List list=((List)object);
                    switch (requestCode){
                        case COVER_PHOTO_CHOOSE_REQUEST_CODE:
                            Object pathObj=list.get(0);
                            ObservableField<TravelCategory> field=null!=pathObj&&pathObj instanceof Path?mCategory:null;
                            TravelCategory category=null!=field?field.get():null;
                            if (null!=category){
                                field.set(null);
                                category.setUrl((Path)pathObj);
                                field.set(category);
                            }
                            break;
                        case PHOTO_CHOOSE_REQUEST_CODE:
                            for (Object child:list){
                                if (null==child||!(child instanceof Path)){
                                    toast("存在不合法文件");
                                }else{
                                   mPhotoAdapter.add((Path)child,true,"After photo choose.");
                                }
                            }
                            break;
                    }
                }
                break;
//           case PHOTO_CHOOSE_REQUEST_CODE:
//               Bundle bundle=null!=data?data.getExtras():null;
//               Object object=null!=bundle?bundle.get(Label.LABEL_DATA):null;
//               if (null!=object&&object instanceof ArrayList) {
//                   List<Path> paths=new ArrayList<>();
//                   for (Object child : (ArrayList) object) {
//                       if (null != child&&child instanceof Path) {
//                           paths.add((Path)child);
//                       }
//                   }
////                   uploadFiles(paths);
//               }
//               break;
        }
    }

    public WebsiteCategoriesAdapter getCategoriesAdapter() {
        return mCategoriesAdapter;
    }

    public ObservableField<TravelCategory> getCategory() {
        return mCategory;
    }

    public WebsiteCategoryPhotoAdapter getPhotoAdapter() {
        return mPhotoAdapter;
    }
}
