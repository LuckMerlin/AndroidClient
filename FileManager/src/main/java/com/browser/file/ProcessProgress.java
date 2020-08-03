package com.browser.file;

import com.merlin.bean.Path;

public interface ProcessProgress {
    void onFileActionProgressChange(Object note, Path instant,Float progress);
}
