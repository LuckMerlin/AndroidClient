package com.merlin.task;

public interface OnTaskUpdate {
    void onTaskUpdate(int status,int what,String note,Object obj,Task task);
}
