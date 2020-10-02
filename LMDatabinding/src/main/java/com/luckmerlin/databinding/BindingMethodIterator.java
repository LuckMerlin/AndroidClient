package com.luckmerlin.databinding;

import androidx.databinding.ViewDataBinding;

import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.match.Matchable;

public class BindingMethodIterator implements PublishMethods {
    private final BindingMethodIteratorImpl mImpl=new BindingMethodIteratorImpl();

    public final MatchBinding iterate(ViewDataBinding binding, Matchable matchable){
            return mImpl.iterate(binding,matchable);
    }

    public final MatchBinding iterate(Class bindingClass, Matchable matchable){
        return mImpl.iterate(bindingClass,matchable);
    }
}
