package com.merlin.browser;

import com.merlin.api.Reply;

interface OnPathModify {
     void onFileModify(int what,Reply<String> path);
}
