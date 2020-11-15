package com.luckmerlin.databinding.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBindingUtil;
import com.luckmerlin.databinding.MatchBinding;
import com.luckmerlin.databinding.ModelBinder;
import com.luckmerlin.databinding.ModelClassFinder;
import com.luckmerlin.core.match.Matchable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AlertDialog extends Dialog {
    private final ObservableField<Object> mTitle=new ObservableField<>();
    private final ObservableField<Object> mLeft=new ObservableField<>();
    private final ObservableField<Object> mCenter=new ObservableField<>();
    private final ObservableField<Object> mRight=new ObservableField<>();
    private final ObservableField<Object> mMessage=new ObservableField<>();

    public AlertDialog(Context context){
        this(context,null);
    }

    public AlertDialog(Context context, Integer windowType){
        this(context,windowType,null);
    }

    public AlertDialog(Context context,Integer windowType, Drawable background){
        this(null!=context?new android.app.Dialog(context):null,windowType,background);
    }

    public AlertDialog(android.app.Dialog dialog,Integer windowType, Drawable background){
        super(dialog,windowType,background);
    }

    protected void onRootViewChanged(View current, View last) {
        //Do nothing
    }

    protected void onDialogBindingAttached(ViewDataBinding dataBinding){
        //Do nothing
    }

    @Override
    protected final void onRootChanged(View current, View last) {
        super.onRootChanged(current, last);
        onRootViewChanged(current,last);
        ViewDataBinding dataBinding=null!=current?DataBindingUtil.getBinding(current):null;
        MatchBinding matchBinding=null!=dataBinding? new ModelClassFinder().find(dataBinding,null, getClass()):null;
        Method method=null!=matchBinding?matchBinding.mSetMethod:null;
        Class[] types=null!=method?method.getParameterTypes():null;
        Class type=null!=types&&types.length>0?types[0]:null;
        if (null!=type&&type.getName().equals(getClass().getName())){
            boolean access=method.isAccessible();
            try {
                method.setAccessible(true);
                method.invoke(dataBinding,this);
                onDialogBindingAttached(dataBinding);
            } catch (Exception e) {
                Debug.E("Exception set dialog into binding.e="+e,e);
                //Do nothing
            }finally {
                method.setAccessible(access);
            }
        }
    }

    @Override
    protected final void onDialogShow() {
        super.onDialogShow();
//        View ddd=getRoot();
//        Debug.D("QQQQQQQQQQQQQQq  "+ddd);
//        Context context=getContext();
//        new LinearLayout(context);
    }

    public final ObservableField<Object> getCenter() {
        return mCenter;
    }

    public final ObservableField<Object> getLeft() {
        return mLeft;
    }

    public final ObservableField<Object> getTitle() {
        return mTitle;
    }

    public final ObservableField<Object> getRight() {
        return mRight;
    }

    public final ObservableField<Object> getMessage() {
        return mMessage;
    }
}
