package com.merlin.binding;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.IdRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.merlin.client.BR;
import com.merlin.client.R;
import com.merlin.client.databinding.StatusBinding;
import com.merlin.debug.Debug;
import com.merlin.view.StatusBarLayout;

public final class StatusBar {
    private final @IdRes Integer mLeft,mCenter,mRight;
    public final static int LEFT =RelativeLayout.ALIGN_PARENT_LEFT;
    public final static int CENTER =RelativeLayout.CENTER_IN_PARENT;
    public final static int RIGHT =RelativeLayout.ALIGN_PARENT_RIGHT;
    public final static int IDLE =-19991;
    public final static int ICON_BACK = -2000;

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

    private StatusBinding createContent(final View view){
        ViewParent parent=null!=view?view.getParent():null;
        if (null!=parent&&parent instanceof RecyclerView){
            parent = parent.getParent();
        }
//        view =null!=view?view.getRootView():null;
//        view=null!=view?view.findViewById(android.R.id.content):null;
//        if (null!=view&&view instanceof ViewGroup){
//            int count=((ViewGroup)view).getChildCount();
//            view=count>=0?((ViewGroup)view).getChildAt(0):null;
//        }
        ViewGroup vg=null!=parent&&parent instanceof ViewGroup?(ViewGroup)parent:null;
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
                    Debug.D(getClass(),"EEEEEEEEEEEEE "+vg);
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
//                    StatusBar.enableStatusBarHeight(view,true);
                }else{
                    vg.removeView(root);
//                    StatusBar.enableStatusBarHeight(view,false);
                }
            }
            return  binding;
        }
        return null;
    }

    public boolean inflate(View view){
        if (null!=view){
            StatusBinding statusBinding=createContent(view);
            View statusView=null!=statusBinding?statusBinding.getRoot():null;
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

    public static boolean enableStatusBarHeight(View view,boolean enable){
        Context context=null!=view?view.getContext():null;
        int height=null!=context? StatusBar.height(context):-1;
        if (height>0){
            view.setPadding(view.getPaddingLeft(),view.getPaddingTop()+height,view.getPaddingRight(),view.getPaddingBottom());
        }
        return false;
    }
}
