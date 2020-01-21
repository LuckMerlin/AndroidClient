package com.merlin.binding;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.merlin.api.Address;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.util.Layout;
import com.merlin.util.StatusBar;

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
        view.setImageResource(resId);
        Debug.D(MBinding.class,"$$$$$$$$$$$$ resId "+resId );
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, String path) {
        RoundedCorners roundedCorners= new RoundedCorners(70);
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners)
                .override(view.getWidth(), view.getHeight());
        Debug.D(MBinding.class,"AAAAAAAAAa "+path);
        Glide.with(view.getContext())
                .load(Address.URL+"/"+path)
//                .centerCrop()
                .apply(options)
//                .thumbnail(1f)
                .placeholder(R.drawable.ic_picture_default)
//                .error(R.drawable.ic_default_pic)
                .into(view);
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
