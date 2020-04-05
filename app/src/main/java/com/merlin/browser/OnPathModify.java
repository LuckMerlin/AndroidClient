package com.merlin.browser;

import com.merlin.api.Reply;
import com.merlin.bean.FileMeta;

interface OnPathModify {
     void onFileModify(Object note,Reply<FileMeta> path);
}
