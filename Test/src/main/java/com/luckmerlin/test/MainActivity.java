package com.luckmerlin.test;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import com.luckmerlin.file.media.MediaFile;
import com.luckmerlin.file.media.MediaScanner;
import com.luckmerlin.file.media.OnMediaScanFinish;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        AlertDialog dialog=new AlertDialog(this);
//        dialog.setContentView(new NotifyDialogModel(){
//
//                              }
//        );
//        dialog.show(new OnViewClick() {
//            @Override
//            public boolean onViewClick(View view, int resId, int count, Object tag) {
//                Debug.D("DDDonViewClickDDDDDDDDDD  "+view+" "+resId);
//                return false;
//            }
//        });
        MediaScanner scanner=new MediaScanner();
        scanner.scanImages(this, true, null,new OnMediaScanFinish() {
            @Override
            public void onScanFinish(int what, String note, Uri src, List<MediaFile> files) {

            }
        },null);

    }

    @Override

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        ModelLifeBinder.bindActivityLife(true,this);
    }

}
