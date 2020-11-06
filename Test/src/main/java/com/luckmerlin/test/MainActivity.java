package com.luckmerlin.test;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luckmerlin.adapter.recycleview.OnSectionLoadFinish;
import com.luckmerlin.adapter.recycleview.SectionListAdapter;
import com.luckmerlin.adapter.recycleview.SectionRequest;
import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.debug.Dump;
import com.luckmerlin.core.permission.OnPermissionRequestFinish;
import com.luckmerlin.core.permission.Permissions;
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
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET}, 1);

        String[] permissions=new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE};


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "选择图片"), 2002);


        RecyclerView recyclerView=new RecyclerView(this);
        recyclerView.setAdapter(new DDD());
        setContentView(recyclerView);


//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if(null!=forResultCode){
//            if ((context instanceof Activity)){
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                Debug.D(getClass(),"Start activity for result."+forResultCode);
//                ((Activity)context).startActivityForResult(intent,forResultCode);


//        new Permissions().isGranted(this,permissions);
//        new Permissions().request(this, new OnPermissionRequestFinish() {
//            @Override
//            public void onPermissionRequestFinish(String[] src, List<String> notGranted) {
//
//            }
//        }, permissions);

//        ActivityCompat.requestPermissions(this,
//                new String[]{"android.permission.READ_EXTERNAL_STORAGE",
//                        android.Manifest.permission.ACCESS_FINE_LOCATION},3000);
    }

    @Override

    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        ModelLifeBinder.bindActivityLife(true,this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        Debug.D("SSSSSSSSSSS "+new Dump().dump(null!=data?data.getExtras():null)+" "+uri);
    }

}
