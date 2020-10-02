package com.luckmerlin.match;

import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;

public interface Matchable extends PublishMethods, PublishFields {
    int MATCHED=-2010;
    int CONTINUE=-2011;
    int BREAK=-2012;
    Integer onMatch(Object arg);
}
