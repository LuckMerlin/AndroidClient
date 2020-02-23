package com.merlin.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.merlin.debug.Debug;
import com.merlin.util.Resource;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.WeakHashMap;

public final class StatusBarLayout extends RelativeLayout implements OnTapClick{
    private final WeakHashMap<OnTapClick,Long> mTapClicks=new WeakHashMap(0);

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
                Res res=null!=(child=getChildAt(i))?Clicker.getRes(child,null):null;
                Object arg=null!=res?res.getArg():null;
                if (null!=arg&&arg instanceof Integer&&(Integer)arg==position){
                    return child;
                }
            }
        }
        return null;
    }

    public boolean addTapClick(OnTapClick click){
        WeakHashMap<OnTapClick,Long> reference=null!=click?mTapClicks:null;
        return null!=reference&&null==reference.put(click,System.currentTimeMillis());
    }

    public boolean removeTapClick(OnTapClick click){
        WeakHashMap<OnTapClick,Long> reference=null!=click?mTapClicks:null;
        return null!=reference&&null!=reference.remove(click);
    }

    public boolean set(StatusBar statusBar){
        if (null!=statusBar){
            set(statusBar.getLeft(),StatusBar.LEFT);
            set(statusBar.getCenter(),StatusBar.CENTER);
            set(statusBar.getRight(),StatusBar.RIGHT);
            return true;
        }
        return false;
    }

    public boolean set(Object object, int position){
            Context context=getContext();
            View last= findPosition(position);
            if (null!=last){
                removeView(last);
            }
            if (null!=object){
                ViewDataBinding binding=null;
                int resourceId=Resources.ID_NULL;
                if (object instanceof Integer){
                    int id=(Integer)object;
                    if (id==StatusBar.ICON_BACK){
                        id=R.drawable.ic_back;
                    }
                    binding=createResourceIdView(context,id);
                    resourceId=id;
                }else if (object instanceof String){
                    binding=createStatusTextView(context,(String)object);
                }else if (object instanceof Drawable){
                    binding=createStatusImageView(context,(Drawable) object);
                }
                View root=null!=binding?binding.getRoot():null;
                if (null!=root){
                    Clicker.putRes(root,new Res(resourceId,position));
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
            }
        return true;
    }

    private final ViewDataBinding createResourceIdView(Context context, Integer id){
        Resources res=null!=context&&null!=id?context.getResources():null;
        if (null!=res){
            Drawable drawable=Resource.isExistResource(R.drawable.class,id)? android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.LOLLIPOP?res.getDrawable(id, context.getTheme()):res.getDrawable(id):null;
            if (null!=drawable){
                return createStatusImageView(context,drawable);
            }
            String text=Resource.isExistResource(R.string.class,id)?res.getString(id):null;
            if (null!=text){
                return createStatusTextView(context,text);
            }
            return Resource.isExistResource(R.layout.class,id)? DataBindingUtil.inflate(LayoutInflater.from(context),id,this,true):null;
        }
        return null;
    }

    private final ViewDataBinding createStatusImageView(Context context,Drawable image){
        if (null!=image){
            StatusIconBinding iconBinding=DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.status_icon,this,true);
            View view= null!=iconBinding?iconBinding.getRoot():null;//LayoutInflater.from(context).inflate(R.layout.status_icon,null);
            if (null!=view&&view instanceof ImageView){
                ((ImageView)view).setImageDrawable(image);
            }
            return iconBinding;
        }
        return null;
    }

    private final ViewDataBinding createStatusTextView(Context context,String text){
        if (null!=context){
            StatusTextBinding textBinding=DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.status_text,this,true);
            View view=null!=textBinding?textBinding.getRoot():null;
            if (null!=view&&view instanceof TextView){
                ((TextView)view).setText(text);
            }
            return textBinding;
        }
        return null;
    }

    @Override
    public final boolean onTapClick(View view, int clickCount, int resId, Object data) {
        WeakHashMap<OnTapClick,Long> reference=mTapClicks;
        Set<OnTapClick> set=null!=reference?reference.keySet():null;
        if (null!=set){
            synchronized (set){
                for (OnTapClick click:set){
                    if (null!=click&&click.onTapClick(view,clickCount,resId,data)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
