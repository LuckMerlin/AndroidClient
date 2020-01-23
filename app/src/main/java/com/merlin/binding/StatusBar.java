package com.merlin.binding;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.merlin.client.R;
import com.merlin.client.databinding.ActivityMediaPlayBinding;
import com.merlin.client.databinding.ActivityMediaPlayBindingImpl;
import com.merlin.client.databinding.StatusBinding;
import com.merlin.debug.Debug;
import com.merlin.view.StatusBarLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public final class StatusBar {
    private final @IdRes Integer mLeft,mCenter,mRight;
    public final static int LEFT =RelativeLayout.ALIGN_PARENT_LEFT;
    public final static int CENTER =RelativeLayout.CENTER_IN_PARENT;
    public final static int RIGHT =RelativeLayout.ALIGN_PARENT_RIGHT;

    private boolean applyRelativeRule(View view,int verb, Integer subject){
        if (null!=view){
            ViewGroup.LayoutParams lp=view.getLayoutParams();
            RelativeLayout.LayoutParams params=null!=lp&&lp instanceof RelativeLayout.LayoutParams?(RelativeLayout.LayoutParams) lp:new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (null!=subject){
                params.addRule(verb,subject);
            }else{
                params.addRule(verb);
            }
            view.setLayoutParams(params);
        }
        return false;
    }

    private StatusBinding createContent(ViewGroup vg){
        if (null!=vg){
            int count=vg.getChildCount();
            View child;
            for (int i = 0; i < count; i++) {
                if(null!=(child=vg.getChildAt(i))&&child instanceof StatusBarLayout){
                    ViewDataBinding binding=DataBindingUtil.getBinding(child);
                    if (null!=binding&&binding instanceof StatusBinding){
                        return ((StatusBinding)binding);
                    }
                }
            }
            final View first=count>0?vg.getChildAt(0):null;
            StatusBinding binding=DataBindingUtil.inflate(LayoutInflater.from(vg.getContext()), R.layout.status,vg,true);
            View root=null!=binding?binding.getRoot():null;
            if (null != root){
                if (root instanceof StatusBarLayout){
                    if (vg instanceof RelativeLayout) {
                        applyRelativeRule(root, RelativeLayout.ALIGN_PARENT_TOP, null);
                        int id=root.getId();
                        if (null!=first){
                            applyRelativeRule(first,RelativeLayout.BELOW,id);
                        }
                        }else if (vg instanceof LinearLayout){
                            vg.removeView(root);
                            vg.addView(root,0);
                        }
                }else{
                    vg.removeView(root);
                }
            }
            return  binding;
        }
        return null;
    }

    public boolean inflate(View view){
        if (null!=view&&view instanceof ViewGroup){
            StatusBinding statusBinding=createContent((ViewGroup)view);
            View statusView=null!=statusBinding?statusBinding.getRoot():null;
            if (null==statusView){
                return false;
            }
            ViewDataBinding binding=null!=statusBinding?DataBindingUtil.getBinding(view):null;
            if (null!=binding){
               Class<?> superClass= binding.getClass().getSuperclass();
               Field[] fields= null!=superClass?superClass.getDeclaredFields():null;
               Field field=null;
                for (int i = 0; i < fields.length; i++) {
                    if (null!=(field=fields[i])&&field.getType().equals(View.OnClickListener.class)){
                        field.setAccessible(true);
                        try {
                            Object object=field.get(binding);
                            if (null!=object&&object instanceof View.OnClickListener){
                                statusBinding.setClickListener((View.OnClickListener)object);
                                break;
                            }
                        } catch (IllegalAccessException e) {
                            //Do nothing
                        }
                    }
                }
            }
            if (statusView instanceof StatusBarLayout){
                StatusBarLayout content=(StatusBarLayout)statusView;
                content.set(mLeft,LEFT);
                content.set(mCenter,CENTER);
                content.set(mRight,RIGHT);
                return true;
            }
        }
        return false;
    }
//    private View findPosition(ViewGroup vg,int position){
//        int count=null!=vg?vg.getChildCount():0;
//        if (count>0){
//            View child;
//            for (int i = 0; i < count; i++) {
//                ViewGroup.LayoutParams params=null!=(child=vg.getChildAt(i))?child.getLayoutParams():null;
//                int[] rules=null!=params&&params instanceof RelativeLayout.LayoutParams?((RelativeLayout.LayoutParams)params).getRules():null;
//                int length=null!=rules?rules.length:-1;
//                for (int j = 0; j < length; j++) {
//                    if (rules[j]==position){
//                        return child;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    private boolean fill(ViewGroup vg,@IdRes Integer id,int position){
//        if (null!=vg&&null!=id){
//            ViewDataBinding binding=createView(vg.getContext(),id);
//            View root=null!=binding?binding.getRoot():null;
//            if (null!=root){
//                View last=findPosition(vg,position);
//                if (null!=last){
//                    vg.removeView(last);
//                }
//                RelativeLayout.LayoutParams rlp=null;
//                if (position== LEFT||position==CENTER||position==RIGHT){
//                    ViewGroup.LayoutParams params=root.getLayoutParams();
//                    rlp=null!=params&&params instanceof RelativeLayout.LayoutParams?
//                            ((RelativeLayout.LayoutParams)params):new RelativeLayout.LayoutParams
//                            (RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
//                    rlp.addRule(position);
//                    rlp.addRule(RelativeLayout.CENTER_VERTICAL);
//                }
//                vg.addView(root,rlp);
//                return true;
//            }
//            return null!=root;
//        }
//        return false;
//    }

//    private static boolean isExistResource(Class<?> cls,@IdRes Integer id){
//        Field[] fields=null!=cls&&null!=id?cls.getDeclaredFields():null;
//        if (null!=fields&&fields.length>0){
//            for (Field field:fields) {
//                if (null!=field){
//                    try {
//                        if (!field.isAccessible()){
//                            field.setAccessible(true);
//                        }
//                        Object object= field.get(null);
//                        if (null!=object&& object instanceof Integer&&(int) object==id){
//                            return true;
//                        }
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return false;
//    }

//    public static final ViewDataBinding createView(Context context,  Integer id){
//        Resources res=null!=context&&null!=id?context.getResources():null;
//        if (null!=res){
//            Drawable drawable=isExistResource(R.drawable.class,id)? android.os.Build.VERSION.SDK_INT >=
//                    android.os.Build.VERSION_CODES.LOLLIPOP?res.getDrawable(id, context.getTheme()):res.getDrawable(id):null;
//            if (null!=drawable){
//                View view=LayoutInflater.from(context).inflate(R.layout.status_icon,null);
//                if (null!=view&&view instanceof ImageView){
//                    ((ImageView)view).setImageDrawable(drawable);
//                }
//                return StatusIconBinding.bind(view);
//            }
//            String text=isExistResource(R.string.class,id)?res.getString(id):null;
//            if (null!=text){
//                View view=LayoutInflater.from(context).inflate(R.layout.status_text,null);
//                if (null!=view&&view instanceof TextView){
//                    ((TextView)view).setText(text);
//                }
//                return StatusTextBinding.bind(view);
//            }
//            return isExistResource(R.layout.class,id)?DataBindingUtil.inflate(LayoutInflater.from(context),id,null,false):null;
//        }
//        return null;
//    }

//    public boolean inflate(View view){
//        if (null!=view&&view instanceof ViewGroup){
//            RelativeLayout content=findContent((ViewGroup)view,true);
////            content.set
////            if (null!=content){
////                fill(content,mLeft,LEFT);
////                fill(content,mCenter,CENTER);
////                fill(content,mRight, RIGHT);
////                return true;
////            }
//        }
//        return false;
//    }

//    public RelativeLayout findContent(ViewGroup vg){
//        return findContent(vg,false);
//    }
//
//    private boolean applyRelativeRule(View view,int verb, Integer subject){
//        if (null!=view){
//            ViewGroup.LayoutParams lp=view.getLayoutParams();
//            RelativeLayout.LayoutParams params=null!=lp&&lp instanceof RelativeLayout.LayoutParams?(RelativeLayout.LayoutParams) lp:new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            if (null!=subject){
//                params.addRule(verb,subject);
//            }else{
//                params.addRule(verb);
//            }
//            view.setLayoutParams(params);
//        }
//        return false;
//    }

//    private RelativeLayout findContent(ViewGroup vg,boolean create){
//        Context context=null!=vg?vg.getContext():null;
//        if (null!=context){
//            int count=vg.getChildCount();
//            View child;
//            RelativeLayout content=null;
//            Object tag;
//            for (int i = 0; i < count; i++) {
//                tag=null!=(child=vg.getChildAt(i))&&child instanceof RelativeLayout?child.getTag(R.id.status_root_RL):null;
//                if (null!=tag&& tag instanceof Boolean&&(Boolean)tag){
//                    content=(RelativeLayout) child;
//                }
//            }
//            if (null==content&&create) {
//                int childCount=vg.getChildCount();
//                View first=childCount>0?vg.getChildAt(0):null;
//                StatusBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.status, vg, true);
//                View root = null != binding ? binding.getRoot() : null;
//                if (null != root){
//                    if (root instanceof RelativeLayout) {
//                        content=(RelativeLayout) root;
//                        root.setTag(R.id.status_root_RL, true);
//                    }else{
//                        vg.removeView(root);
//                    }
//                    if (null!=content){
//                        if (vg instanceof RelativeLayout) {
//                            applyRelativeRule(root, RelativeLayout.ALIGN_PARENT_TOP, null);
//                            int id=root.getId();
//                            if (id==View.NO_ID){
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                                    first.setId(id = View.generateViewId());
//                                } else {
//                                    first.setId(id = 2010);
//                                }
//                            }
//                            if (null!=first){
//                                applyRelativeRule(first,RelativeLayout.BELOW,id);
//                            }
//                        }else if (vg instanceof LinearLayout){
//                            vg.removeView(root);
//                            vg.addView(root,0);
//                        }
//                    }
//                }
//            }
//            return content;
//        }
//        return null;
//    }

    private StatusBar(Integer left, Integer center, Integer right){
        mLeft=left;
        mCenter=center;
        mRight=right;
    }

    public Integer getLeft() {
        return mLeft;
    }

    public Integer getCenter() {
        return mCenter;
    }

    public Integer getRight() {
        return mRight;
    }

    public static StatusBar statusBar(Object left){
        return statusBar(left,null);
    }

    public static StatusBar statusBar(Object left, Object center){
        return statusBar(left,center,null);
    }

    public static StatusBar statusBar(Object left, Object center, Object right){
        return new StatusBar(null!=left&&left instanceof Integer?(Integer)left:null,
                null!=center&&center instanceof Integer?(Integer)center:null,
                null!=right&&right instanceof Integer?(Integer)right:null);
    }

    public static int height(Context context){
        Resources res=null!=context?context.getResources():null;
        if (null!=res){
            int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
            return resourceId>0?res.getDimensionPixelSize(resourceId):0;
        }
        return 0;
    }
}
