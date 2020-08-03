package com.browser.file;

import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;

import java.io.File;

public class LocalFileDelete extends FileAction{

    public final Reply<Path> deleteFile(File file, ProcessProgress progress) {
        if (file == null || !file.exists()) {
            return new Reply(true, What.WHAT_NOT_EXIST,"File not exist",null);
        }
        if (isCanceled()){
            return new Reply(true, What.WHAT_CANCEL,"Cancel delete file",null);
        }
        File[] files=file.isDirectory()?file.listFiles():null;
        if (null!=files&&files.length>0){//Delete child
            for (File child : files) {
                deleteFile(child,progress); // 递规的方式删除文件夹
            }
        }
        Path path=Path.build(file);
        notify("Deleting file ",null,path,null,progress);
//        file.delete();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return file.exists()?new Reply(true,What.WHAT_EXCEPTION,"Fail delete file",path):
                new Reply<>(true,What.WHAT_SUCCEED,"Succeed delete file",path);
    }

}
