package com.luckmerlin.mvvm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.databinding.DataBindingUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


class ModelBinder {
    private static Map<View,Model> mBoundedMap;

    static final class Created{
        final Model mModel;
        final View mRoot;
        Created(Model model,View root){
            mModel=model;
            mRoot=root;
        }
    }

    protected final boolean bindFromAdapter(View view, Object modelObj,String debug){
        if (null!=view&&null!=modelObj){
            if (null!=getViewModel(view)){
                return false;//Already bound
            }
            if (modelObj instanceof Boolean){
                if (!(Boolean)modelObj){
                    return false;//Not need bind model
                }
            }
            final ModelBinder binder=new ModelBinder();
            Created created=null!=modelObj?binder.createModel(null,modelObj):null;
            Model model=null!=created?created.mModel:null;
            if (null!=model&&binder.attachModel(view,model,debug)){
                return true;
            }
            return false;
        }
        return false;
    }

    protected final Class<? extends Model> getModelClass(Object object){
        if (null!=object){
            if (object instanceof String){
                try {
                    object=Class.forName((String)object);
                } catch (ClassNotFoundException e) {
                    //Do nothing
                }
            }
            Class cls=null!=object&&object instanceof Class?(Class)object:null;
            return isExistClass(cls, Model.class.getName())?(Class<? extends Model>)cls:null;
        }
        return null;
    }

    protected final boolean isExistClass(Class srcClass,final String clsTargetName){
        if (null!=srcClass&&null!=clsTargetName){
            while (null!=srcClass){
                String clsName=null!=srcClass?srcClass.getName():null;
                if (null!=clsName&&clsName.equals(clsTargetName)){
                    return true;
                }
                srcClass=srcClass.getSuperclass();
            }
        }
        return false;
    }

    protected final Created createModel(Object object) {
        return createModel(null,object);
    }

    protected final Created createModel(Context context,Object object) {
        if (null!=object){
            if (object instanceof Created){
                return (Created)object;
            }
            object=object instanceof OnModelResolve ?getIfNotNull(((OnModelResolve)object).onResolveModel(),object):object;
            object=null!=object&&object instanceof Activity?getIfNotNull(getActivityFirstRoot((Activity)object),object):object;
            if (null!=context&&null!=object&&object instanceof Integer){
               object= getIfNotNull(createLayoutView(context,(Integer)object),object);
            }
            View root=null;
            if (null!=object&&object instanceof View){
                root=(View)object;
                object=DataBindingUtil.getBinding(root);
            }
            object= null!=object&&object instanceof View?getIfNotNull(DataBindingUtil.getBinding((View)object),object):object;
            if (null!=object&&object instanceof ViewDataBinding){
                ViewDataBinding binding=(ViewDataBinding)object;
                root=binding.getRoot();
                object=getIfNotNull(findBindingModelClass((ViewDataBinding)object),object);
            }
            object=null!=object&&object instanceof String?getIfNotNull(getModelClass((String)object),object):object;
            if (null!=object&&object instanceof Class){
                if (!Modifier.isAbstract(((Class)object).getModifiers())) {
                    Constructor[] constructors = null != getModelClass((Class) object) ? ((Class) object).getDeclaredConstructors() : null;
                    if (null != constructors && constructors.length > 0) {
                        Constructor constructor = null;
                        for (Constructor child : constructors) {
                            Class[] types = null != child ? child.getParameterTypes() : null;
                            if (null != types && types.length == 0) {
                                constructor = child;
                                break;
                            }
                        }
                        if (null != constructor) {//Found model constructor,Now create it
                            try {
                                object = constructor.newInstance();
                            } catch (Exception e) {
                                //Do nothing
                            }
                        }
                    }
                }
            }
            Model model=null!=object&&object instanceof Model?(Model)object:null;
            return null!=root||null!=model?new Created(model,root):null;
        }
        return null;
    }

    protected final View createLayoutView(Context context,final Integer layoutId){
        if (null!=context&&null!=layoutId){
            View view=null;
                try {
                    boolean isViewDataBindLayout=true;
                    if (isViewDataBindLayout) {
                        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false);
                        view = null != binding ? binding.getRoot() : null;
                    }
                    }catch (Exception e){
                        //Do nothing
                    }
                    try {
                        view=null==view?View.inflate(context,layoutId,null):view;
                    }catch (Exception e){
                        //Do nothing
                    }
            if (null!=view&&view.getParent()==null){
                return view;
            }
            return null;
        }
        return null;
    }

    protected Class<? extends Model> findBindingModelClass(ViewDataBinding binding){
        Method method=null!=binding?findModelClassSetMethod(binding, Model.class):null;
        Class[] types=null!=method?method.getParameterTypes():null;
        Class parm=null!=types&&types.length==1?types[0]:null;
        return null!=parm?getModelClass(parm):null;
    }

    protected final Method findModelClassSetMethod(View view,Class<?extends Model> modelClass){
        return null!=view?findModelClassSetMethod(DataBindingUtil.getBinding(view),modelClass):null;
    }

    protected final Method findModelClassSetMethod(ViewDataBinding binding,Class<?extends Model> modelClass) {
        final String modelClassName=null!=modelClass?modelClass.getName(): Model.class.getName();
        if (null!=modelClassName&&modelClassName.length()>0){
            Class cls=null!=binding?binding.getClass():null;
            Method[] methods=null!=cls?cls.getDeclaredMethods():null;
            if (null!=methods&&methods.length>0){
                for (Method method:methods) {
                    Type[] types=null!=method?method.getParameterTypes():null;
                    Type type=null!=types&&types.length==1?types[0]:null;
                    if (null!=type&&type instanceof Class&&isExistClass((Class)type,modelClassName)){
                        return method;
                    }
                }
            }
        }
        return null;
    }

    protected final boolean attachModel(View view,Model model,String debug){
        if (null!=model&&bindViewModel(view,model,debug)&&model.attachRoot(view,debug)){
            return true;
        }
        return false;
    }

    protected final int unbindViewModel(String debug,Object ...objects){
        final Map<View,Model> boundMap=mBoundedMap;
        int count=0;
        if (null!=objects&&objects.length>0){
            if (null!=boundMap&&boundMap.size()>0) {
                for (Object object:objects) {
                    synchronized (boundMap){
                        Set<View> set=boundMap.keySet();
                        if (null!=set&&set.size()>0){
                            for (View child:set) {
                                if (null!=child){
                                    Model value=boundMap.get(child);
                                    if (null!=value&&(child==object||value==object)){
                                        Debug.D("Unbind view model "+(null!=debug?debug:".")+value);
                                        boundMap.remove(child);
                                        value.detachRoot(debug);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (null!=boundMap&&boundMap.size()<=0){
            mBoundedMap=null;
        }
        return count;
    }

    public static Model getViewModel(View view){
        Map<View,Model> boundMap=mBoundedMap;
        if (null!=boundMap&&boundMap.size()<=0){
            mBoundedMap=null;
        }
        return null!=boundMap?boundMap.get(view):null;
    }

    protected final boolean bindViewModel(View view,Model model,String debug){
        if (null!=model&&null!=view){
            if (model.isRootAttached()){
                return false;//Model already attached view
            }
            Map<View,Model> boundMap=mBoundedMap;
            boundMap=null!=boundMap?boundMap:(mBoundedMap=new WeakHashMap<View,Model>());
            Model existModel=null!=boundMap?boundMap.get(view):null;
            if (null!=existModel){
                return false;//Already bound
            }
            ViewDataBinding binding= DataBindingUtil.getBinding(view);
            Method method=null!=binding?findModelClassSetMethod(binding, model.getClass()):null;
            try {
                if (null!=method) {
                    method.invoke(binding, model);
                    Debug.D("Bind view model " + (null != debug ? debug : ".") + model);
                    if (null != boundMap) {
                        boundMap.put(view, model);
                    }
                    return true;
                }
            } catch (Exception e) {
                //Do nothing
            }
        }
        return false;
    }

    protected final Object getIfNotNull(Object arg,Object def){
        return null!=arg?arg:def;
    }

    protected final View getActivityFirstRoot(Activity activity){
        View content=null!=activity?activity.findViewById(android.R.id.content):null;
        return null!=content&&content instanceof ViewGroup&&((ViewGroup)content).
                getChildCount()>0?((ViewGroup)content).getChildAt(0):null;
    }
}
