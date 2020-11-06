package com.luckmerlin.core.permission;

import java.util.List;

public interface OnPermissionRequestFinish {
    void onPermissionRequestFinish(String[] src, List<String> notGranted);
}
