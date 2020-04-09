package com.merlin.browser;

import com.merlin.api.Reply;
import com.merlin.bean.Document;

interface OnPathModify {
     void onFileModify(Object note,Reply<Document> path);
}
