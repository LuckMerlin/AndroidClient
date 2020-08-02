package com.browser.file;

import com.merlin.bean.Path;

public interface Progress {
    void onFileProgressChange(String note, Path instant, Float progress);
}
