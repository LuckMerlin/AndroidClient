package com.merlin.binding;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.merlin.api.Address;
import com.merlin.debug.Debug;

@BindingMethods({
        @BindingMethod(type = RecyclerView.class,attribute = "itemDecoration",method ="addItemDecoration" )
})

public class MBinding {
    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, int resId) {
        view.setImageResource(resId);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, String path) {
        path=null!=path?""+ Address.URL+"/image/thumbs/"+path :null;
        Debug.D(MBinding.class,"$$$$$$$$$$$$ path "+path );
        Glide.with(view.getContext())
                .load(path)
                .centerCrop()
                .thumbnail(0.1f)
//                .placeholder(R.drawable.ic_default_pic)
//                .error(R.drawable.ic_default_pic)
                .into(view);
    }


}
