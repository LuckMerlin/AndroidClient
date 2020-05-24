package com.merlin.task;

public interface Canceler<T,M> extends Controller{
    M cancel(T arg,String debug);
}
