package com.luckmerlin.match;

import com.luckmerlin.core.proguard.PublishMethods;
import java.util.Collection;
import java.util.List;

public class Matcher implements PublishMethods {
    private final MatcherImpl mImpl=new MatcherImpl();

    public final<T> List<T> match(T[] values, Matchable matchable){
        return mImpl.match(values,matchable);
    }

    public final<T> List<T> match(T[] values, Matchable matchable, int max){
        return mImpl.match(values,matchable,max);
    }

    public final<T> List<T> match(Collection<T> values, Matchable matchable, int max){
        return mImpl.match(values,matchable,max);
    }

}
