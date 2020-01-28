package com.merlin.binding;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.Space;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.merlin.activity.BaseActivity;
import com.merlin.adapter.LinearItemDecoration;
import com.merlin.api.Address;
import com.merlin.client.R;
import com.merlin.client.databinding.MediasAllBinding;
import com.merlin.debug.Debug;
import com.merlin.model.BaseModel;
import com.merlin.task.Status;
import com.merlin.util.Layout;

import java.lang.ref.WeakReference;

@BindingMethods({
        @BindingMethod(type = RecyclerView.class,attribute = "itemDecoration",method ="addItemDecoration" )
})

public class MBinding {

    @BindingAdapter(value = {"enableBarPadding"})
    public static void enableBarPadding(View view, int enable) {
        Context context=null!=view?view.getContext():null;
        int height=null!=context? StatusBar.height(context):-1;
        if (height>0){
            view.setPadding(view.getPaddingLeft(),view.getPaddingTop()+height,
                    view.getPaddingRight(),view.getPaddingBottom());
        }
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        if (null!=view){
            view.setImageResource(resId);
            view.setTag(R.id.resourceId,new IDs(resId,null ));
        }
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, String path) {
        RoundedCorners roundedCorners = new RoundedCorners(70);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(view.getWidth(), view.getHeight());
//        Debug.D(MBinding.class,"AAAAAAAAAa "+Address.URL+path);
//         String ddd="https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1579668574979&di=2c09057e986a070149d31ba342ec5985&imgtype=0&src=http%3A%2F%2Farticle.fd.zol-img.com.cn%2Ft_s640x2000%2Fg3%2FM04%2F0C%2F03%2FCg-4V1RjLO2IIKzYAATUS9gV0gUAARNqwD3bwkABNRj460.jpg";
        if (null!=path){
            if (path.startsWith("/")){
                path= Address.URL+path;
            }
        }
//        Debug.D(MBinding.class," "+path);
        Glide.with(view.getContext())
                .load(path)
                .centerCrop()
                .apply(options)
                .thumbnail(1f)
                .placeholder(R.drawable.ic_picture_default)
//                .error(R.drawable.ic_default_pic)
                .into(view);
    }

    @BindingAdapter(value = {"createModel"})
    public static void createModel(View view, String modeClass) {
        new ModelBinder().bind(view,modeClass);
    }

    @BindingAdapter(value = {"enableModelClick"})
    public static void enableModelClick(View view, Object object) {
        if (null!=object){
            ClickBinding binding = null;
            if (object instanceof Boolean){
                binding=new ClickBinding((Boolean)object,null);
            }else if (object instanceof ClickBinding){
                binding=(ClickBinding)object;
            }
            if (null!=binding){
                binding.bind(view);
            }
        }
    }

    @BindingAdapter(value = {"statusBar"})
    public static void statusBar(View view, StatusBar statusBar) {
        if (null!=view &&null!=statusBar){
            statusBar.inflate(view);
        }
    }


    @BindingAdapter("adapter")
    public static void adapter(RecyclerView view, RecyclerView.Adapter adapter) {
        if (null!=view){
            view.setAdapter(adapter);
        }
    }

    @BindingAdapter("adapter")
    public static void adapter(RecyclerView view, Adapter object) {
        if (null!=object&&null!=view){
            object.inflate(view);
        }
    }

    @BindingAdapter("layoutManager")
    public static void linear(RecyclerView view, Layout layout) {
        if (null!=layout){
            Context context=view.getContext();
            int spanCount=layout.getSpanCount();
            int orientation=layout.getOrientation();
            boolean isReverseLayout=layout.isReverseLayout();
            switch (layout.getLayout()){
                case Layout.STAGGERED_GRID_LAYOUT:
                    StaggeredGridLayoutManager sgm=new StaggeredGridLayoutManager(spanCount,orientation);
                    sgm.setReverseLayout(isReverseLayout);
                    view.setLayoutManager(sgm);
                    break;
                case Layout.LINEAR_LAYOUT:
                    view.addItemDecoration(new LinearItemDecoration(3));
                    LinearLayoutManager llm=new LinearLayoutManager(context, orientation,isReverseLayout);
                    llm.setSmoothScrollbarEnabled(true);
                    view.setLayoutManager(llm);
                    break;
                case Layout.GRID_LAYOUT:
                    GridLayoutManager glm=new GridLayoutManager(context, spanCount,orientation,isReverseLayout);
                    glm.setSmoothScrollbarEnabled(true);
                    view.setLayoutManager(glm);
                    break;
            }
        }

    }

}
