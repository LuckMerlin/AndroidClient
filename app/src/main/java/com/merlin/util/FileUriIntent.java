package com.merlin.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

public class FileUriIntent {

    public Intent apply(Intent intent, File file){
        intent= null!=file?null!=intent?intent:new Intent():null;
        if (null!=intent){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
//                    String ss = "com.example.administrator.devicemanagerapp" + ".fileprovider";
//                    Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), ss, apkfile);
                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
//                intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        }
        return null;
    }
}
