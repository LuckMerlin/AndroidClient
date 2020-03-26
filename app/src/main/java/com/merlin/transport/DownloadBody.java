package com.merlin.transport;

import com.merlin.server.Retrofit;

import okhttp3.ResponseBody;
import com.merlin.api.Canceler;

public abstract class DownloadBody implements Canceler,retrofit2.Callback<ResponseBody> {

}
