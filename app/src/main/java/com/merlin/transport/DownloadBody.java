package com.merlin.transport;

import com.merlin.server.Retrofit;

import okhttp3.ResponseBody;

public abstract class DownloadBody extends Retrofit.Canceler implements retrofit2.Callback<ResponseBody> {

}
