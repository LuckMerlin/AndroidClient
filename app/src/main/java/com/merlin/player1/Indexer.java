package com.merlin.player1;

public interface Indexer {
    Integer mode(Integer mode,String debug);
    int pre(int index,int size);
    int next(int index,int size,boolean user);
}
