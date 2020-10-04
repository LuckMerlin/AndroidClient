package com.luckmerlin.databinding;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.match.Matchable;
import java.lang.reflect.Method;

class BindingMethodIteratorImpl {

    public final MatchBinding iterate(ViewDataBinding binding, Matchable matchable){
        final Class bindingClass=null!=binding?binding.getClass().getSuperclass():null;
        MatchBinding matchBinding= null!=bindingClass?iterate(bindingClass,null):null;
        if (null!=matchBinding){
            Method getMethod=matchBinding.mGetMethod;
            if (null!=getMethod){
                boolean access=getMethod.isAccessible();
                try {
                    getMethod.setAccessible(true);
                    Object result=getMethod.invoke(binding);
                    matchBinding.setCurrent(result);
                    Integer matched=null!=matchable?matchable.onMatch(matchBinding):null;
                    if (null==matched||matched!=Matchable.MATCHED){
                        return null;
                    }
                } catch (Exception e) {
                    Debug.E("Exception iterate binding model.e="+e,e);
                    return null;
                }finally {
                    getMethod.setAccessible(access);
                }
            }
        }
        return matchBinding;
    }

    public final MatchBinding iterate(Class bindingClass, Matchable matchable){
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
                            MatchBinding matchBinding=new MatchBinding(method,getMethod,type,null,null);
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
