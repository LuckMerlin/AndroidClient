package com.merlin.api;

import java.util.List;

public interface OnProcessChange<T> {
    void onProcessChanged(Float progress,String note,T instant, List<T> processed);
}
