package com.merlin.browser;
import com.merlin.bean.IPath;

/**
 * @deprecated
 */
public interface OnPathUpdate {
    void onPathUpdate(int what, Object note, IPath from, IPath to);
}
