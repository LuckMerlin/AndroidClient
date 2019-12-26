package com.merlin.task;

import com.merlin.client.Client;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Downloader {

    public List<Download> getDownloadList();

    public Client.Canceler download(Download download);

    public boolean isRunning(Download download);

    public boolean isDownloading(Download download);

    public boolean pause(Download download);

    public boolean cancel(Download download);

    public void setCallback(DownloadService.Callback callback);

}
