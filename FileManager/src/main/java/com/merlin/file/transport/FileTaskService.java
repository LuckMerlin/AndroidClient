package com.merlin.file.transport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.merlin.api.Label;
import com.merlin.bean.Path;
import com.merlin.task.TaskService;

import java.util.ArrayList;

public class FileTaskService extends TaskService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle=null!=intent?intent.getExtras():null;
        Object what=null!=bundle?bundle.get(Label.LABEL_WHAT):null;
        if (null!=what){
            if (what.equals(Label.LABEL_UPLOAD)){
                bundle.getParcelableArrayList(Label.LABEL_FROM);
            }else if (what.equals(Label.LABEL_DOWNLOAD)){
                bundle.getParcelableArrayList(Label.LABEL_FROM);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private boolean downloadPaths(Bundle bundle){

        return false;
    }

    public static boolean uploadPaths(Context context, ArrayList<Path> files, Path path, int coverMode, String debug){
        if (null==context||null==files||files.size()<=0||null==path){
            return false;
        }
        Intent intent=new Intent();
        intent.putParcelableArrayListExtra(Label.LABEL_FROM,files);
        intent.putExtra(Label.LABEL_TO,path);
        intent.putExtra(Label.LABEL_WHAT,Label.LABEL_UPLOAD);
        intent.putExtra(Label.LABEL_MODE,coverMode);
        return null!=context.startService(intent);
    }

    public static boolean downloadPaths(Context context,ArrayList<Path> files,Path path,int coverMode,String debug){
        if (null==context||null==files||files.size()<=0||null==path){
            return false;
        }
        Intent intent=new Intent();
        intent.putParcelableArrayListExtra(Label.LABEL_FROM,files);
        intent.putExtra(Label.LABEL_TO,path);
        intent.putExtra(Label.LABEL_WHAT,Label.LABEL_DOWNLOAD);
        intent.putExtra(Label.LABEL_MODE,coverMode);
        return null!=context.startService(intent);
    }
}
