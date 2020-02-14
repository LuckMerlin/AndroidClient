package com.merlin.transport;

import com.merlin.bean.FileMeta;
import com.merlin.debug.Debug;
import com.merlin.retrofit.Retrofit;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;

public final class FileDownloader {
    private final List<FileDownload> mWaiting=new ArrayList<>();
    private FileDownload mDownloading=null;
    private Runnable mRunnable=null;

    public int indexWaiting(Object task){
        List<FileDownload> waiting=mWaiting;
        if (null!=task&&null!=waiting){
            synchronized (waiting){
                return waiting.indexOf(task);
            }
        }
        return -1;
    }

    public boolean isWaiting(Object task){
        return null!=task&&indexWaiting(task)>=0;
    }

    public boolean exist(Object object){
        return null!=object&&(isWaiting(object));
    }

    public boolean add(FileDownload download,String debug){
        if (null!=download){
            List<FileDownload> waiting=mWaiting;
            if (!exist(download)&&null!=waiting){
                synchronized (waiting){
                    if (waiting.add(download)){
                        Debug.D(getClass(),"Add download task into waiting "+(null!=debug?debug:"."));
                        return notifyStartIfPossible(debug);
                    }
                }
            }
        }
        return false;
    }

    private synchronized boolean notifyStartIfPossible(String debug){
        List<FileDownload> waiting=mWaiting;
        if (null!=waiting&&waiting.size()>0){
            Runnable runnable=mRunnable;
            if (null==runnable){
                final Thread thread=new Thread(mRunnable=new Worker() {
                    @Override
                    public void run() {
                        while (true){
                            if (mStop){
                                Debug.W(getClass(),"Stop downloader "+(null!=debug?debug:"."));
                                break;
                            }
                            List<FileDownload> waiting=mWaiting;
                            if (null==waiting){
                                Debug.W(getClass(),"Stop downloader While waiting queue NULL "+(null!=debug?debug:"."));
                                break;
                            }
                            synchronized (waiting){
                                if (waiting.size()<=0){
                                    try {
                                        waiting.wait();
                                    } catch (InterruptedException e) {
                                        //Do nothing
                                    }
                                }else{
                                    FileDownload download=waiting.remove(0);
                                    if (null!=download){
                                        download.onDownload();
                                    }
                                }
                            }
                        }
                    }
                });
                thread.setName("File Downloader.");
                thread.start();
            }else{
                synchronized (waiting){
                    Debug.D(getClass(),"Add download task into waiting "+(null!=debug?debug:"."));
                    waiting.notify();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean download(List<FileMeta> files,String folder,String debug){
        if (null!=files&&files.size()>0){
            for (FileMeta meta:files){
                if (null!=meta&&add(new FileDownload(meta,folder),debug)){

                }
            }
        }
        return false;
    }

    private static abstract class Worker implements Runnable{
        protected boolean mStop=false;
    }
}
