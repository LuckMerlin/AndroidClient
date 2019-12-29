package com.merlin.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.merlin.bean.FileMeta;
import com.merlin.bean.Meta;
import com.merlin.client.databinding.ActivityFileBrowserBinding;
import com.merlin.debug.Debug;
import com.merlin.model.FileBrowserModel;
import com.merlin.player1.MediaPlayer;
import com.merlin.protocol.Tag;
import com.merlin.task.Download;
import com.merlin.task.DownloadService;
import com.merlin.task.Downloader;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class FileBrowserActivity extends  NasActivity<ActivityFileBrowserBinding, FileBrowserModel> implements Tag {

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(()->{
//            findViewById(R.id.fileBrowser_transmitIV).performClick();
//            Download download=new Download("/src","/sdcard/linqiang.mp3","rrrrrr");
//            DownloadService.post(this,download);

        },6000);
        checkPermission();
        Intent intent=getIntent();
        String path="li";
//        path="/sdcard/Download/生日歌.mp3";
//        path = "/storage/151D-2906/Music/linqiang.mp3";
//        Debug.D(getClass(),"@@@@@@@@ "+new File("/sdcard/Musics/linqiang.mp3").exists());
        path="/sdcard/Musics/西单女孩 - 原点.mp3";
//        path="/sdcard/Musics/Lenka - Like A Song.mp3";
//        path="/mnt/sdcard/linqiang.mp3";
//        Debug.D(getClass(),"@@ "+new File(path).exists());
        MediaPlayer dd=new MediaPlayer();
//        mPlayer.setOnDecodeFinishListener(new OnMediaFrameDecodeFinish() {
//            @Override
//            public void onDecodeFinish(byte[] bytes, int channels, int sampleRate) {
//                dd.play(bytes,0,bytes.length);
//            }
//        });
//        try {
//            FileInputStream is=new FileInputStream(path);
//            byte[] buffer=new byte[1024*1024*5];
//            int length=is.read(buffer);
//            byte[] ddd=new byte[1];
////            System.arraycopy(buffer,1024*614,ddd,0,1024*600);
//            boolean result=mPlayer.playBytes(ddd,0,buffer.length,true);
//            Debug.D(getClass(),"播放结果 "+result);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Toast.makeText(this,""+mPlayer.play(path,0),Toast.LENGTH_LONG).show();
        Meta meta=getNasMetaFromIntent(getIntent());
        if (null==meta){
            toast("不能浏览非指定文件系统的终端");
            finish();
            return ;
        }
        getViewModel().setClientMeta(meta);
//
//        private void test(Downloader downloader){
//            String srcPath=null,name,from,target;
//            srcPath="./WMDYY.mp3";
//            srcPath="./NSWXADGN.mp4";
////        srcPath="/volumes/pythonCodes/linqiang.mp3";
////            srcPath="/volumes/pythonCodes/1576847957749986.mp4";
////            srcPath="/volumes/pythonCodes/1576846797997566.mp4";
////        srcPath="/volumes/pythonCodes/iPartment.S04E01.HDTV.720p.x264.AAC-sherry.mp4";
//            from="linqiang";
//            name="linqiang_two.mp4";
//            target="/sdcard/a";
//            Download download=new Download(from,srcPath,name,target,null);
////        download.setType(Download.TYPE_REPLACE);
////        Debug.D(getClass(),"#####ddddd###### "+name+" "+srcPath);
////        downloader.download(download);
//            post(()->{
////            Debug.D(getClass(),"开始取消");
////            download.setDeleteIncomplete(false);
////            downloader.pause(download);
//            },10000);
//            post(()->{
////            Debug.D(getClass(),"重新开始下载");
////            download.setDeleteIncomplete(false);
////            download.setType(Download.TYPE_NORMAL);
////            downloader.download(download);
//            },15000);
////        mDownloader.download(test);
//        }
        new Handler().postDelayed(()->{
//            Context context,String fromAccount,String folder, FileMeta meta
//            FileMeta fileMeta=new FileMeta();
//            fileMeta.setFile("F:\\LuckMerlin\\SLManager\\NSWXADGN.mp4");
//            fileMeta.setName("你是我心爱的姑娘.mp4");
//            DownloadService.postDownload(this,"linqiang",null,fileMeta);
            //
//            String fromAccount,String src,String name,String targetFolder,String unique
            Download download=new Download("linqiang","F:\\LuckMerlin\\SLManager\\NSWXADGN.mp4",
                    "你是我心爱的姑娘.mp4",null,null);
            download.setType(Download.TYPE_REPLACE);
            List<Download> list=new ArrayList<>();
            list.add(download);
//            DownloadService.postDownload(this,list);

            List<Download> list2=new ArrayList<>();
            Download download2=new Download("linqiang",
                    "F:\\LuckMerlin\\SLManager\\WMDYY.mp3",
                    "我们都一样.mp3",null,null);
            download2.setType(Download.TYPE_REPLACE);
            list2.add(download2);
//            DownloadService.postDownload(this,list2);
            new Handler().postDelayed(()->{
//                DownloadService.postDownload(this,list);
            },3000);
        },5000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getViewModel().refreshCurrentPath("After activity onResume.");
    }

    @Override
    public void onBackPressed() {
        if (!getViewModel().onBackPressed()){
            super.onBackPressed();
        }
    }

}
