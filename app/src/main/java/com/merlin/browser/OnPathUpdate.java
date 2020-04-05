package com.merlin.browser;
import com.merlin.bean.Path;

public interface OnPathUpdate {
    void onPathUpdate(int what,Object note, Path from,Path to);
}
