package com.merlin.transport;

import okhttp3.ResponseBody;

public abstract class DownloadBody extends Canceler implements retrofit2.Callback<ResponseBody> {

}
