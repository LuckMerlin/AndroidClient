package com.luckmerlin.filescanner;

import com.luckmerlin.file.Callback;

import java.io.File;
import java.util.List;

public interface OnFileScan extends Callback {
    void onFileScanned(File root, File file, List<File> files);
}
