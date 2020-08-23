package com.luckmerlin.mvvm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

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

    protected final boolean bindFromAdapter(View view, Object modelObj,String debug){
        if (null!=view&&null!=modelObj){
            if (null!=getViewModel(view)){
                return false;//Already bound
            }
            final ModelBinder binder=new ModelBinder();
            if (modelObj instanceof Boolean){
                if (!(Boolean)modelObj){
                    return false;//Not need bind model
                }
                ViewDataBinding binding=DataBindingUtil.getBinding(view);
                modelObj=null!=binding?binder.findBindingModelClass(binding):null;
            }
            Model model=null!=modelObj?binder.createModel(modelObj):null;
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

    protected final Model createModel(Object object) {
        if (null!=object){
            object=object instanceof OnModelResolve ?((OnModelResolve)object).onResolveModel():object;
            object=object instanceof ViewDataBinding?findBindingModelClass((ViewDataBinding)object):object;
            object=object instanceof String?getModelClass((String)object):object;
            if (object instanceof Class){
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
                        if (null != constructor) {//Found model constructor,Now create ir
                            try {
                                object = constructor.newInstance();
                            } catch (Exception e) {
                                //Do nothing
                            }
                        }
                    }
                }
            }
            return null!=object&&object instanceof Model?((Model)object):null;
        }
        return null;
    }

    protected final View createModelView(Context context, Object ...objects) {
        if (null!=context&&null!=objects&&objects.length>0){
            for (Object object:objects) {
                if (null!=object){
                    object= object instanceof OnModelLayoutResolve?((OnModelLayoutResolve)object).onResolveModeLayout():object;
                    if (object instanceof Integer){//Found as layout id
                        try {
                            boolean isViewDataBindLayout=true;
                            if (isViewDataBindLayout) {
                                ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), (Integer) object, null, false);
                                object = null != binding ? binding.getRoot() : object;
                            }
                        }catch (Exception e){
                            //Do nothing
                        }
                        try {
                            object=null!=object&&object instanceof Integer?View.inflate(context,(Integer)object,null):object;
                        }catch (Exception e){
                            //Do nothing
                        }
                    }
                    if (null!=object&&object instanceof View&&((View)object).getParent()==null){
                        return (View)object;
                    }
                }
            }
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

    protected final boolean bindViewModel(View view,String debug){
        ViewDataBinding binding=null!=view?DataBindingUtil.getBinding(view):null;
        Model model=null!=binding?createModel(binding):null;
        return null!=model&&bindViewModel(view,model,debug);
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

}
