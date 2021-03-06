package com.browser.file;

import com.merlin.api.OnProcessChange;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;

import java.io.File;

public class LocalFileDelete extends FileAction{

    public final Reply<Path> deleteFile(File file, OnProcessChange progress) {
        if (file == null || !file.exists()) {
            return new Reply(true, What.WHAT_NOT_EXIST,"File not exist",null);
        }
        if (isCanceled()){
            return new Reply(true, What.WHAT_CANCEL,"Cancel delete file",null);
        }
        File[] files=file.isDirectory()?file.listFiles():null;
        if (null!=files&&files.length>0){//Delete child
            Reply reply=null;
            for (File child : files) {// 递规的方式删除文件夹
                if (null!=(reply=deleteFile(child,progress))&&reply.getWhat()==What.WHAT_CANCEL){
                    return reply;
                }
            }
        }
        Path path=Path.build(file);
        file.delete();
        notify(null,"Deleting file ",path,null,progress);
        return file.exists()?new Reply(true,What.WHAT_EXCEPTION,"Fail delete file",path):
                new Reply<>(true,What.WHAT_SUCCEED,"Succeed delete file",path);
    }

}
