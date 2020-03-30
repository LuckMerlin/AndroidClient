package com.merlin.task;

import com.merlin.client.__Client;

import java.util.List;

public interface Transporter {

    public List<Transport> getDownloadList();

    public __Client.Canceler download(Transport transport);

    public boolean isRunning(Transport transport);

    public boolean isDownloading(Transport transport);

    public boolean pause(Transport transport);

    public boolean cancel(Transport transport);

    public void setCallback(DownloadService.Callback callback);

}
