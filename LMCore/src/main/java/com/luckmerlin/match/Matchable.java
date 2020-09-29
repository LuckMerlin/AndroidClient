package com.luckmerlin.match;

public interface Matchable {
    int MATCHED=-2010;
    int CONTINUE=-2011;
    int BREAK=-2012;
    Integer onMatch(Object arg);
}
