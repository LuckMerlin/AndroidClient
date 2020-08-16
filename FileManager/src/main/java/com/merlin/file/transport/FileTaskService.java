package com.merlin.file.transport;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.merlin.api.Label;
import com.merlin.bean.Path;
import com.merlin.debug.Debug;
import com.merlin.task.Task;
import com.merlin.task.TaskGroup;
import com.merlin.task.TaskService;

import java.util.ArrayList;


public class FileTaskService extends TaskService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle=null!=intent?intent.getExtras():null;
        Object what=null!=bundle?bundle.get(Label.LABEL_WHAT):null;
        if (null!=what){
            Task task=createFileTask(bundle);
            if (null!=task){
                super.execute(task,null,"While service onStart.");
                Debug.D(getClass(),"EEEEEEEEEE "+task);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static boolean uploadPaths(Context context, ArrayList<Path> files, Path path, int coverMode, String debug){
        if (null==context||null==files||files.size()<=0||null==path){
            return false;
        }
        Intent intent=new Intent(context,FileTaskService.class);
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
        Intent intent=new Intent(context,FileTaskService.class);
        intent.putParcelableArrayListExtra(Label.LABEL_FROM,files);
        intent.putExtra(Label.LABEL_TO,path);
        intent.putExtra(Label.LABEL_WHAT,Label.LABEL_DOWNLOAD);
        intent.putExtra(Label.LABEL_MODE,coverMode);
        return null!=context.startService(intent);
    }

    private Task createFileTask(Bundle bundle) {
        Object whatObj = null != bundle ? bundle.get(Label.LABEL_WHAT) : null;
        if (null != whatObj && (whatObj.equals(Label.LABEL_UPLOAD) || whatObj.equals(Label.LABEL_DOWNLOAD))) {
            ArrayList<Parcelable> fromList = bundle.getParcelableArrayList(Label.LABEL_FROM);
            Object toPathObj = bundle.getParcelable(Label.LABEL_TO);
            Object modelObj = bundle.getParcelable(Label.LABEL_MODE);
            if (null == toPathObj || !(toPathObj instanceof Path) || null == fromList || fromList.size() <= 0) {
                return null;
            }
            Integer mode = null != modelObj && modelObj instanceof Integer ? (Integer) modelObj : null;
            Path toPath = (Path) toPathObj;
            TaskGroup taskGroup = new TaskGroup("");
            for (Parcelable child : fromList) {
                if (null != child && child instanceof Path) {
                    Path path = (Path) child;
                    if (whatObj.equals(Label.LABEL_DOWNLOAD)) {
                        String toName=toPath.generateChildPath(path.getName(true));
                        if (null==toName||toName.length()<=0){
                            continue;
                        }
                        taskGroup.add(new NasFileDownloadTask(path, toName, mode));
                    } else if (whatObj.equals(Label.LABEL_UPLOAD)) {
                        taskGroup.add(new FileUploadNasTask(path.getName(), path.getPath(), toPath, null, mode));
                    }
                }
            }
            return null!=taskGroup&&taskGroup.size()>0?taskGroup:null;
        }
        return null;
    }
}
