package com.merlin.browser.binding;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.api.Res;
import com.merlin.click.Clicker;
import com.merlin.model.ModelBinder;

import java.util.Collection;

public class LMBinding {
    @BindingAdapter("src")
    public static void setSrc(ImageView view, Object path) {
        new PathGlider().glide(view,path,false);
    }

}
