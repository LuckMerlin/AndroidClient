package com.luckmerlin.databinding;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishMethods;

import java.lang.reflect.Method;

 public final class ModelBinder implements PublishMethods {

    public MatchBinding bindModelForView(View view,String debug){
        ViewDataBinding binding=null!=view&&DataBindingUtil.checkDataBindingEnable(false)? DataBindingUtil.getBinding(view):null;
        return null!=binding?bindModelForBinding(binding,debug):null;
    }

    public MatchBinding bindModelForCreateModel(CreatedModel model,String debug) {
        if (null!=model){
            Model lModel=model.mModel;
            View modelRoot=model.mRoot;
            ViewDataBinding modelBinding=null!=modelRoot?DataBindingUtil.getBinding(modelRoot):null;
            MatchBinding matchBinding=model.mMatchBinding;
            Method setMethod=null!=matchBinding?matchBinding.mSetMethod:null;
            if (null!=modelRoot&&null!=setMethod&&null!=modelBinding&&null!=lModel){
                Class[] types=setMethod.getParameterTypes();//Check model set invalid
                Class modelType=null!=types&&types.length==1?types[0]:null;
                if (null==modelType||!modelType.getName().equals(lModel.getClass().getName())){
                    return null;
                }
                if (lModel.attachRoot(modelRoot,debug)){
                    try {
                        setMethod.invoke(modelBinding,lModel);
                        matchBinding.setCurrent(lModel);
                        return matchBinding;
                    } catch (Exception e) {
                        lModel.detachRoot("While root attach set exception "+(null!=debug?debug:"."));
                    }
                }
            }
        }
        return null;
    }

    public MatchBinding bindModelForBinding(ViewDataBinding binding,String debug) {
        View root=null!=binding?binding.getRoot():null;
        CreatedModel model=null!=root?new ModelCreator().createModel(root.getContext(),binding,debug):null;
        return null!=model?bindModelForCreateModel(model,debug):null;
    }

    public MatchBinding bindModelForMatchBinding(Context context,MatchBinding matchBinding,String debug){
        if (null!=matchBinding){
            if (null==matchBinding.getCurrent()){//If model not create
                CreatedModel createdModel=new ModelCreator().createModel(context,matchBinding, "While model need bind "+(null!=debug?debug:"."));
                Model model=null!=createdModel?createdModel.mModel:null;
                if (null==model){
                    return null;
                }
                //If need create model root view
                if (!model.isRootAttached()&&null==createdModel.mRoot&&model instanceof OnModelResolve){
                    Object modelViewObj=((OnModelResolve)model).onResolveModel();
                    View modelRoot=null!=modelViewObj?new ViewCreator().create(context,modelViewObj):null;
                    if (null==modelRoot){//Create model root view fail
                        return null;
                    }
                    MatchBinding newMatchBinding=new ModelClassFinder().findModel(modelRoot,null);
                    createdModel=null!=newMatchBinding?new CreatedModel(model,modelRoot,newMatchBinding):null;
                }
                return null!=createdModel?bindModelForCreateModel(createdModel,debug):null;
            }
            return bindModelForCreateModel(new CreatedModel(null, matchBinding.getRoot(),matchBinding),debug);
        }
        return null;
    }

    public MatchBinding bindModelForObject(Context context, Object object, String debug){
        if (null==object||!DataBindingUtil.checkDataBindingEnable(false)){
            return null;
        }else if (object instanceof ViewDataBinding){
            return bindModelForBinding((ViewDataBinding)object,debug);
        }else if (object instanceof Class){
            ModelClassFinder classFinder=new ModelClassFinder();
            MatchBinding matchBinding= classFinder.findModel((Class)object,null);
            matchBinding= null!=matchBinding?bindModelForMatchBinding(context,matchBinding,debug):null;
            if (null==matchBinding&&classFinder.isExistClass((Class)object, Model.class.getName())){//If need create by model class
               matchBinding=bindModelForMatchBinding(context,new MatchBinding(null,
                       null,((Class)object),null,null),debug);
            }
            return matchBinding;
        }else if (object instanceof String){
             Class cls=createClass((String)object);
             return null!=cls?bindModelForObject(context,cls,debug):null;
        }else if (object instanceof View){
            return bindModelForView((View)object,debug);
        }else if (object instanceof MatchBinding){
            return bindModelForMatchBinding(context,((MatchBinding)object),debug);
        }else if (object instanceof Model){
            Model lModel=(Model)object;
            if (lModel instanceof OnModelResolve&&!lModel.isRootAttached()){//If not attached
                Object modelObj=((OnModelResolve)lModel).onResolveModel();
                View modelView=null!=modelObj?new ViewCreator().create(context,modelObj):null;
                MatchBinding matchBinding=null!=modelView?new ModelCreator().findModel(modelView,null):null;
                return null!=matchBinding?new ModelBinder().bindModelForMatchBinding(modelView.getContext(),matchBinding,debug):null;
            }
            return null;
        }else if (object instanceof Integer&&null!=context){
            View modelView=new ViewCreator().create(context,object);
            return null!=modelView?bindModelForObject(context,modelView,debug):null;
        } else if (object instanceof OnModelResolve){
            Object modelObj=((OnModelResolve)object).onResolveModel();
            if (null!=modelObj){
                View modelView=null!=modelObj&&null!=context&&modelObj instanceof Integer?new ViewCreator().create(context,modelObj):null;
                MatchBinding binding=null!=modelView?bindModelForObject(context,modelView,debug):null;
                binding=null!=binding?binding:bindModelForObject(context,modelObj,debug);
                if (object instanceof Activity){
                    Activity activity=(Activity)object;
                    Object currentModel=null!=binding?binding.getCurrent():null;
                    Model model=null!=currentModel&&currentModel instanceof Model ?(Model)currentModel:null;
                    View modelRoot=null!=model?model.getRoot():null;
                    if (null!=modelRoot&&modelRoot.getParent()==null){//If root view not attach window
                        activity.setContentView(modelRoot,new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT));
                    }
                }
                return binding;
            }
        }
        return null;
    }



    private Class createClass(String className){
        if (null!=className&&className.length()>0){
            try {
                return Class.forName(className);
            }catch (Exception e){
                //Do nothing
            }
        }
        return null;
    }
}
