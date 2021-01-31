package com.luckmerlin.core.permission;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.luckmerlin.core.debug.Debug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.WeakHashMap;

public final class Permissions {
//       <uses-permission android:name="android.permission.CAMERA" />
//    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
//    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    private static WeakHashMap<Activity,Requesting> mRequestingPermissions;

    public boolean isGranted(Context context,String ... permissions){
        return isGrantedPackage(context,null,permissions);
    }

    public boolean isGrantedPackage(Context context,String pkgName, String ... permissions){
        if (null==permissions||permissions.length<=0){
            return false;
        }
        PackageManager manager=null!=context?context.getPackageManager():null;
        if (null==manager){
            return false;
        }
        pkgName=null!=pkgName?pkgName:context.getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String child:permissions){
                if (null!=child&&child.length()>0&&PackageManager.PERMISSION_GRANTED!=manager.checkPermission(child,pkgName)){
                    return false;
                }
            }
            return true;
        }
        try {
            PackageInfo pack = manager.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
            String[] permissionStrings = null!=pack?pack.requestedPermissions:null;
            if (null==permissionStrings||permissionStrings.length<=0){
                return false;
            }
            for (String child:permissions){
                if (null!=child&&child.length()>0){
                    boolean granted=false;
                    for (String permission:permissionStrings) {
                        if (null!=permission&&permission.equals(child)){
                            granted=true;
                            break;
                        }
                    }
                    if (!granted){
                        return false;
                    }
                }
            }
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    public List<String> checkNotGranted(Context context, String pkgName,String ... permissions){
        if (null==permissions||permissions.length<=0){
            return null;
        }
        PackageManager manager=null!=context?context.getPackageManager():null;
        if (null==manager){
            return Arrays.asList(permissions);
        }
        pkgName=null!=pkgName?pkgName:context.getPackageName();
        if (null==pkgName||pkgName.length()<=0){
            return Arrays.asList(permissions);
        }
        final int permissionCount = permissions.length;
        List<String> notGranted=new ArrayList<>();
        for (int i = 0; i < permissionCount; i++) {
            if (PackageManager.PERMISSION_GRANTED!=manager.checkPermission(permissions[i], pkgName)){
                notGranted.add(permissions[i]);
            }
        }
        return notGranted;
    }

    public boolean request(final Activity activity,OnPermissionRequestFinish callback,final String ... permissions) {
        return request(activity,2008,callback,permissions);
    }

    public boolean request(final Activity activity, Integer requestCode,OnPermissionRequestFinish callback,final String ... permissions){
        if (null==callback){
            return false;
        }
        if (null==permissions||permissions.length<=0){
            return false;
        }
        final Context context=activity;
        if (null==context||null==permissions||permissions.length<=0){
            return false;
        }
        PackageManager manager=null!=context?context.getPackageManager():null;
        if (null==manager){
            return false;
        }
        String pkgName=context.getPackageName();
        if (Build.VERSION.SDK_INT >= 23) {
            WeakHashMap<Activity,Requesting> request=mRequestingPermissions;
            request=null!=request?request:(mRequestingPermissions=new WeakHashMap<Activity, Requesting>());
            request.put(activity,new Requesting(activity,permissions));
            final View view=new View(activity){
                @Override
                public void onWindowFocusChanged(boolean hasWindowFocus) {
                    super.onWindowFocusChanged(hasWindowFocus);
                    Debug.D("EEEEEEEEEEEEEe "+hasWindowFocus);
                }
            };
            view.setBackgroundColor(Color.RED);
//            activity.addContentView(view,new ViewGroup.LayoutParams(1,1));
//            ActivittCmp
            Debug.D("qqqqqqqqddddd   qqqq "+requestCode);
            activity.requestPermissions(permissions, null!=requestCode?requestCode:2008);
            Debug.D("qqqqqqqqqqqq "+requestCode);
//            Handler handler=new Handler(Looper.getMainLooper());
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    activity.
////                    activity.isDestroyed();
////                    activity.hasWindowFocus();
//                }
//            }, 1000);
            return true;
        }
        callback.onPermissionRequestFinish(permissions,checkNotGranted(context,pkgName,permissions));
        return true;
    }

    private final static class Requesting{
        private final String[] mPermissions;
        private final Activity mActivity;

        private Requesting(Activity activity,String... permissions){
            mActivity=activity;
            mPermissions=permissions;
        }
    }
}

