package com.luckmerlin.file;

import java.util.List;

public interface OnScanFinish<S,T> extends Callback{
    void onScanFinish(int what, String note, S src, List<T> files);
}
