package com.merlin.util;

import com.merlin.debug.Debug;

import java.io.File;
import java.io.IOException;

public class FileMaker {

    public File makeFile(String path){
        return makeFile(path,false);
    }

    public File makeFile(String path,boolean recreate){
        return makeFile(path,null,recreate);
    }

    public File makeFile(String folder,String name){
        return makeFile(folder,name,false);
    }

    public File makeFile(String folder,String name,boolean recreate){
        if (null!=folder&&folder.length()>0){
            File file=null!=name&&name.length()>0?new File(folder,name):new File(folder);
            if (file.exists()&&!recreate){
                return file.isFile()?file:null;
            }
            file.delete();//Try delete
            File parent=null!=file?file.getParentFile():null;
            if (null!=parent&&!parent.exists()){
                parent.mkdirs();
            }
            try {
                file.createNewFile();
                return file.exists()&&file.isFile()?file:null;
            } catch (IOException e) {
                Debug.E(getClass(),"Failed make new file.e="+e+" "+file,e);
            }
        }
        return null;
    }
}
