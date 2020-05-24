package com.merlin.task;

public interface Task<T> extends State{
    Controller execute(T arg, Callback ...callbacks);
    Status getStatus();
}
