package com.merlin.task;

public interface Progress {
    int SPEED=-2012;
    int DONE=-2013;
    int TOTAL=-2014;
    int PROGRESS=-2015;
    String getText(int progressWhat);
    float getProgress();
}
