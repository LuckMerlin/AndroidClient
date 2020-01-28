package com.merlin.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.binding.IDs;
import com.merlin.binding.StatusBar;
import com.merlin.client.R;
import com.merlin.client.databinding.StatusIconBinding;
import com.merlin.client.databinding.StatusTextBinding;
import com.merlin.util.Resource;

public final class StatusBarLayout extends RelativeLayout {

    public StatusBarLayout(Context context) {
        this(context, null);
    }

    public StatusBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private View findPosition(int position){
        int count=getChildCount();
        if (count>0){
            View child;
            for (int i = 0; i < count; i++) {
                Object object=null!=(child=getChildAt(i))?child.getTag(R.id.resourceId):null;
                Object arg=null!=object&&object instanceof IDs?((IDs)object).getArg():null;
                if (null!=arg&&object instanceof Integer&&(Integer)arg==position){
                    return child;
                }
            }
        }
        return null;
    }

    public boolean set( @IdRes Integer id, int position){
            Context context=getContext();
            if (null!=id&&id==StatusBar.ICON_BACK){
                id=R.drawable.ic_back;
            }
            ViewDataBinding binding=createView(context,id);
            View root=null!=binding?binding.getRoot():null;
            View last= findPosition(position);
            if (null!=last){
                removeView(last);
            }
            if (null!=root){
                root.setTag(R.id.resourceId,new IDs(id,position));
                RelativeLayout.LayoutParams rlp=null;
                if (position== StatusBar.LEFT||position==StatusBar.CENTER||position==StatusBar.RIGHT){
                    ViewGroup.LayoutParams params=root.getLayoutParams();
                    rlp=null!=params&&params instanceof RelativeLayout.LayoutParams?
                            ((RelativeLayout.LayoutParams)params):new RelativeLayout.LayoutParams
                            (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    rlp.addRule(position);
                    rlp.addRule(RelativeLayout.CENTER_VERTICAL);
                }
                root.setLayoutParams(rlp);
            }
        return true;
    }

    private final ViewDataBinding createView(Context context,  Integer id){
        Resources res=null!=context&&null!=id?context.getResources():null;
        if (null!=res){
            Drawable drawable=Resource.isExistResource(R.drawable.class,id)? android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.LOLLIPOP?res.getDrawable(id, context.getTheme()):res.getDrawable(id):null;
            if (null!=drawable){
                StatusIconBinding iconBinding=DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.status_icon,this,true);
                View view= null!=iconBinding?iconBinding.getRoot():null;//LayoutInflater.from(context).inflate(R.layout.status_icon,null);
                if (null!=view&&view instanceof ImageView){
                    ((ImageView)view).setImageDrawable(drawable);
                }
                return iconBinding;
            }
            String text=Resource.isExistResource(R.string.class,id)?res.getString(id):null;
            if (null!=text){
                StatusTextBinding textBinding=DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.status_text,this,true);
                View view=null!=textBinding?textBinding.getRoot():null;
                if (null!=view&&view instanceof TextView){
                    ((TextView)view).setText(text);
                }
                return textBinding;
            }
            return Resource.isExistResource(R.layout.class,id)? DataBindingUtil.inflate(LayoutInflater.from(context),id,this,true):null;
        }
        return null;
    }

}
