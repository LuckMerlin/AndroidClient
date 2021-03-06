package com.luckmerlin.core.match;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Matcher implements PublishMethods {
    public final<T> List<T> match(T[] values, Matchable matchable){
        return match(values,matchable,-1);
    }

    public final<T> List<T> match(T[] values, Matchable matchable, int max){
        int length=null!=values?values.length:0;
        if (length>0){
            List<T> list=new ArrayList<>(length>=max?length:Math.max(0,max));
            synchronized (values) {
                for (T child : values) {
                    if (list.size() >= max) {
                        break;
                    }
                    if (null == child) {
                        continue;
                    } else if (null == matchable) {
                        list.add(child);
                        continue;
                    }
                    Integer integer = matchable.onMatch(child);
                    if (null == integer || integer == Matchable.CONTINUE) {
                        continue;
                    } else if (integer == Matchable.BREAK) {
                        break;
                    } else if (integer == Matchable.MATCHED) {
                        list.add(child);
                    }
                }
            }
            return list;
        }
        return null;
    }

    public final<T> List<T> match(Collection<T> values, Matchable matchable, int max){
        int length=null!=values?values.size():0;
        if (length>0){
            List<T> list=new ArrayList<>(length>=max?max:length);
            synchronized (values){
                for (T child:values) {
                    if (list.size()>=max){
                        break;
                    }
                    if (null==child){
                        continue;
                    }else if (null==matchable){
                        list.add(child);
                        continue;
                    }
                    Integer integer= matchable.onMatch(child);
                    if (null==integer||integer==Matchable.CONTINUE){
                        continue;
                    }else if (integer==Matchable.BREAK){
                        break;
                    }else if (integer==Matchable.MATCHED){
                        list.add(child);
                    }
                }
            }
            return list;
        }
        return null;
    }

}
