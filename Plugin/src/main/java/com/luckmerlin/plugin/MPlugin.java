package com.luckmerlin.plugin;

import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import dalvik.system.DexClassLoader;

public final class MPlugin {

    public ClassLoader createClassLoader(Context context,File file){
        return createClassLoader(context,file,null,null);
    }

    public ClassLoader createClassLoader(Context context,File file,String librarySearchPath,ClassLoader loader) {
        if (null == context || null == file || !file.exists() || file.length() <= 0 || !file.canRead()) {
            return null;
        }
        FileInputStream inputStream=null;
        try {
            inputStream=new FileInputStream(file);
            return createClassLoader(context,inputStream,file.getName(),librarySearchPath,loader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (null!=inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public ClassLoader createClassLoader(Context context, InputStream stream,String name, String librarySearchPath, ClassLoader loader){
        if (null==context||null==stream){
            return null;
        }
        String dir="dir";
        name=null!=name&&name.length()>0?name:"test";
        File cacheFolder=context.getDir(dir, Context.MODE_PRIVATE);
        String cacheFolderPath=null!=cacheFolder?cacheFolder.getAbsolutePath():null;
        String cacheFilePath =null!=cacheFolderPath?cacheFolderPath+ File.separator+name:null;
        if (null==cacheFilePath||cacheFilePath.length()<=0){
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Files.copy(stream,new File(cacheFilePath).toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new DexClassLoader(cacheFilePath,cacheFilePath, librarySearchPath,
                null!=loader?loader:getClass().getClassLoader());
    }

}
