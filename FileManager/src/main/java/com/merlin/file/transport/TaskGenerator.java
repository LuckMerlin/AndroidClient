package com.merlin.file.transport;

import android.os.Bundle;
import android.os.Parcelable;

import com.merlin.api.Label;
import com.merlin.bean.Path;
import com.merlin.task.Task;
import com.merlin.task.TaskGroup;

import java.util.ArrayList;

/**
 * @deprecated
 */
class TaskGenerator {

    public Task fromBundle(Bundle bundle){
        Object whatObj=null!=bundle?bundle.get(Label.LABEL_WHAT):null;
        if (null!=whatObj&&(whatObj.equals(Label.LABEL_UPLOAD)||whatObj.equals(Label.LABEL_DOWNLOAD))){
            ArrayList<Parcelable> fromList=bundle.getParcelableArrayList(Label.LABEL_FROM);
            Object toPathObj=bundle.getParcelable(Label.LABEL_TO);
            Object modelObj=bundle.getParcelable(Label.LABEL_MODE);
            if (null==toPathObj||!(toPathObj instanceof Path) ||null==fromList||fromList.size()<=0){
                return null;
            }
            Integer mode=null!=modelObj&&modelObj instanceof Integer?(Integer)modelObj:null;
            Path toPath=(Path)toPathObj;
            TaskGroup taskGroup=new TaskGroup("");
            for (Parcelable child:fromList){
                if (null!=child&&child instanceof Path){
                    Path path=(Path)child;
                    if (whatObj.equals(Label.LABEL_DOWNLOAD)){
                        taskGroup.add(new NasFileDownloadTask(path,toPath.getPath(),mode));
                    }else if (whatObj.equals(Label.LABEL_UPLOAD)){
                        taskGroup.add(new FileUploadNasTask(path.getName(),path.getPath(),toPath,null,mode));
                    }
                }
            }
            return taskGroup;
        }
        return null;
    }
}
