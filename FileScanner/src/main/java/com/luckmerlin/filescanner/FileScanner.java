package com.luckmerlin.filescanner;

import com.luckmerlin.file.Callback;
import com.luckmerlin.core.match.Matchable;

import java.io.File;
import java.util.List;

public final class FileScanner implements Callback {

    public final boolean scan(File file, List<File> files,Matchable matchable,Callback callback){
        return scanFile(file,file,files,matchable,callback);
    }

    private final boolean scanFile(File file, File root,List<File> files,Matchable matchable,Callback callback){
        if (null==file||null==root){
            notifyScanFinish(ARGS_INVALID,"File or root is NONE.",null,files,callback);
            return true;
        }else if (!file.exists()){
            notifyScanFinish(NONE_EXIST,"File not exist.",file,files,callback);
        }
        Integer match=null!=matchable?matchable.onMatch(file):null;
        if (null!=match&&match==Matchable.BREAK){
            return false;
        }else if (match==Matchable.MATCHED&&null!=files){
            files.add(file);
            notifyFileScanned(root,file,files,callback);
        }
        if (file.isDirectory()){
           File[] directoryFiles= file.listFiles();
           if (null!=directoryFiles&&directoryFiles.length>0){
               for (File child:directoryFiles) {
                    if (null!=child&&!scanFile(child,root,files,matchable,callback)){
                        return false;
                    }
               }
           }
        }
        if (file==root){
            notifyScanFinish(SUCCEED,"Scan finish",file,files,callback);
        }
        return true;
    }

    private void notifyScanFinish(int what, String note, File src,List<File> files,Callback callback){
        if (null!=callback&&callback instanceof OnFileScanFinish){
            ((OnFileScanFinish) callback).onScanFinish(what,note,src,files);
        }
    }

    private void notifyFileScanned(File root,File file,List<File> files,Callback callback){
        if (null!=callback&&callback instanceof OnFileScan){
            ((OnFileScan)callback).onFileScanned(root,file,files);
        }
    }

}
