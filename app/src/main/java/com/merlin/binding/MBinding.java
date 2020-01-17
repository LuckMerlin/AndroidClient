package com.merlin.binding;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.merlin.api.Address;
import com.merlin.client.R;
import com.merlin.debug.Debug;
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
        Glide.with(view.getContext())
                .load(path)
//                .centerCrop()
                .apply(options)
//                .thumbnail(1f)
                .placeholder(R.drawable.ic_picture_default)
//                .error(R.drawable.ic_default_pic)
                .into(view);
    }

}
