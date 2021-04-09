package com.luckmerlin.databinding;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.match.Matchable;
import java.lang.reflect.Method;

/**
 * @deprecated
 */
public  class BindingMethodIterator {

    public final MatchBinding iterate(ViewDataBinding binding, Matchable matchable){
        final Class bindingClass=null!=binding?binding.getClass().getSuperclass():null;
        return null!=bindingClass?iterate(bindingClass,binding,matchable):null;
    }

    public final MatchBinding iterate(Class bindingClass, Matchable matchable){
        return iterate(bindingClass,null,matchable);
    }

    private final MatchBinding iterate(Class bindingClass,ViewDataBinding binding, Matchable matchable){
        if (null!=bindingClass){
            Method[] methods=null!=bindingClass?bindingClass.getDeclaredMethods():null;
            if (null!=methods&&methods.length>0){
                String methodName=null;
                for (int i = 0; i < methods.length; i++) {
                    Method method=methods[i];
                    if (null==(methodName=null!=method?method.getName():null)||!methodName.startsWith("set")){
                        continue;
                    }
                    Class[] types=method.getParameterTypes();
                    if (null==types||types.length!=1){
                        continue;
                    }
                    Class type=types[0];
                    try {
                        Method getMethod=bindingClass.getDeclaredMethod(methodName.replaceFirst("set","get"));
                        if (null!=getMethod){
                            ViewDataBinding currentBinding=null;
                            Object current=null;
                            if (null!=binding){//Get current object
                               Class[] parameters=getMethod.getParameterTypes();
                               Class returnCls=null==parameters||parameters.length<=0?getMethod.getReturnType():null;
                               if (null!=returnCls&&!returnCls.getName().equals(void.class.getName())){
                                   boolean access=getMethod.isAccessible();
                                   try {
                                       getMethod.setAccessible(true);
                                       current=getMethod.invoke(binding);
                                       currentBinding=binding;
                                   }catch (Exception e){
                                       //Do nothing
                                   }finally {
                                       getMethod.setAccessible(access);
                                   }
                               }
                            }
                            MatchBinding matchBinding=new MatchBinding(method,getMethod,type, current,currentBinding);
                            Integer matched=Matchable.MATCHED;
                            if (null!=matchable){
                                matched=matchable.onMatch(matchBinding);
                            }
                            if (null!=matched&&matched==Matchable.MATCHED){
                                return matchBinding;
                            }
                        }
                    } catch (Exception e) {
                        Debug.E("Exception iterate match binding."+e,e);
                    }
                }
            }
        }
        return null;
    }
}
