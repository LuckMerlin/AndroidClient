//package com.merlin.browser;
//
//import com.browser.file.FileDelete;
//import com.browser.file.FileProcess;
//import com.browser.file.Progress;
//import com.merlin.api.Label;
//import com.merlin.api.Processing;
//import com.merlin.api.Reply;
//import com.merlin.api.What;
//import com.merlin.bean.Path;
//import com.merlin.retrofit.Retrofit;
//import com.merlin.task.file.Cover;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.POST;
//
//public class FileCopyProcess extends FileProcess<Path> {
//    private final Path mFolder;
//    private final int mCoverMode;
//
//    public FileCopyProcess(String title,Path folder,int coverMode, ArrayList<Path> files){
//        super(title,files);
//        mFolder=folder;
//        mCoverMode=coverMode;
//    }
//
//    private interface Api{
//        @POST("/file/delete")
//        @FormUrlEncoded
//        Call<Reply<Processing>> copy(@Field(Label.LABEL_PATH) String path);
//    }
//
//    @Override
//    protected Reply onProcess(Path from,OnProcessUpdate update, Retrofit retrofit) {
////        return copyFile(from,mFolder,mCoverMode,update,retrofit);
//        return null;
//    }
//
//    private Reply copyFile(Path from,Path to,int coverMode,Progress update, Retrofit retrofit){
//        final String fromPath=null!=from?from.getPath():null;
//        final String toFolder=null!=to?to.getPath():null;
//        if (null==fromPath|fromPath.length()<=0||null==toFolder||toFolder.length()<=0){
//            return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Path invalid.",fromPath);
//        }
//        if (from.isLocal()){//Copy local file
//            File fromFile=new File(fromPath);
//            return to.isLocal()?copyLocalFileToLocal(fromFile,new File(toFolder),coverMode,update):
//                    copyLocalFileToNas(fromFile,to.getHostUri(),toFolder,coverMode,update,retrofit);
//        }
//        return to.isLocal()?downloadToLocal(from.getHostUri(),fromPath,new File(toFolder),coverMode,update,retrofit):
//                copyFromNasToNas(from,to,coverMode,update,retrofit);
//    }
//
//    private Reply copyFromNasToNas(Path from,Path toFolder,int coverMode,Progress update, Retrofit retrofit){
//        return null;
//    }
//
//    private Reply copyLocalFileToNas(File from,String toHostUri,String toFolder,int coverMode,Progress update,Retrofit retrofit){
//
//        return null;
//    }
//
//    private Reply<Path> copyLocalFileToLocal(File from, File toFolder, int coverMode, Progress update){
//        final String name=null!=from?from.getName():null;
//        final Path path=null!=from?Path.build(from):null;
//        if (null==name||name.length()<=0||null==toFolder){
//            return new Reply(true,What.WHAT_INVALID,"File invalid",path);
//        }else if (!from.exists()){
//            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",path);
//        }else if (!from.canRead()){
//            return new Reply(true,What.WHAT_NONE_PERMISSION,"File none read permission",path);
//        }else if (toFolder.exists()&&!toFolder.isDirectory()){//Copy file
//            return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
//        }
//        final File toFile=new File(toFolder,name);
//        if (toFile.exists()&&(from.isDirectory()==toFile.isDirectory())){
//            if (coverMode!= Cover.COVER_REPLACE) {
//                return new Reply(true, What.WHAT_EXIST, "File already exist", null);
//            }
//            File temp=null;
//            while ((temp=new File(toFolder,"."+name+"_"+(Math.random()*10000)+".temp")).exists()){
//                    //Do nothing
//            }
//            toFile.renameTo(temp);//Move exist to temp
//            if (toFile.exists()){
//                return new Reply(true, What.WHAT_FAIL, "Fail delete already exist", null);
//            }
//            Reply<Path> copyResult=copyLocalFileToLocal(from,toFolder,coverMode,update);
//            FileDelete fileDelete=new FileDelete();
//            if (null!=copyResult&&copyResult.isSuccess()&&copyResult.getWhat()==What.WHAT_SUCCEED){
////                notifyProgress("Deleting exist file ", Path.build(temp),0f,update);
//                fileDelete.deleteFile(temp,update);
//            }else{//Copy fail,Rollback just copied file(s)
////                notifyProgress("Rollback delete just copied file ", Path.build(temp),0f,update);
//                fileDelete.deleteFile(toFile,update);
//                if (!toFile.exists()&&temp.exists()){//Rollback delete succeed
//                    temp.renameTo(toFile);//Move backup back
//                }
//            }
//            return copyResult;
//        }
//        if (from.isDirectory()){//Copy file
//            File[] children=from.listFiles();
//            Reply<Path> lastFail=null;
//            if (null!=children&&children.length>0){
//                for (File child:children) {
//                    lastFail=copyLocalFileToLocal(child,null,coverMode,update);
//                }
//            }
//            return lastFail;
//        }else if (!toFolder.exists()){
//            toFolder.mkdirs();
//        }
//        if (!toFolder.exists()||!toFolder.isDirectory()){
//            return new Reply(true,What.WHAT_FAIL,"Target dir not folder or not exist",path);
//        }
//        try {
//            toFile.createNewFile();
//            if (toFile.exists()){
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //            !toFolder.exists()?toFolder.mkdirs()
////            if (toFolder.exists()){
////                if (!toFolder.isDirectory()){
////                    return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
////                }
////            }
////        else if (from.length()<=0){
////        }else{
////            notifyProgress("Copying file",path,null,update);
////            FileInputStream inputStream=null;FileOutputStream outputStream=null;
////            try {
////                long total=from.length();
////                inputStream= new FileInputStream(from);
////                outputStream= new FileOutputStream(toFile);
////                byte[] buffer=new byte[1024*1024];
////                int read=-1;long done=0;
////                while ((read=inputStream.read(buffer))>0){
////                    outputStream.write(buffer,0,read);
////                    done+=read;
////                    notifyProgress("Copying file",path,done/total,update);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////                return new Reply(true,What.WHAT_EXCEPTION,"Exception file copy",null);
////            }finally {
////                close(outputStream,inputStream);
////            }
////        }
//        return null;
//    }
//
//    private Reply downloadToLocal(String fromHostUri,String fromPath,File toFolder,int coverMode,Progress update,Retrofit retrofit){
//        return null;
//    }
//
////    private Reply<Path> deleteFile(File file,OnProcessUpdate update) {
////        if (file == null || !file.exists()) {
////            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",null);
////        }
////        File[] files=file.isDirectory()?file.listFiles():null;
////        if (null!=files&&files.length>0){//Delete child
////            for (File child : files) {
////                deleteFile(child,update); // 递规的方式删除文件夹
////            }
////        }
////        Path path=Path.build(file);
////        notifyProgress("Deleting file ",path,null,update);
////        file.delete();
////        return file.exists()?new Reply(true,What.WHAT_EXCEPTION,"Fail delete file",path):
////                new Reply<>(true,What.WHAT_SUCCEED,"Succeed delete file",path);
////    }
//
//}
//package com.merlin.browser;
//
//import com.browser.file.FileDelete;
//import com.browser.file.FileProcess;
//import com.browser.file.Progress;
//import com.merlin.api.Label;
//import com.merlin.api.Processing;
//import com.merlin.api.Reply;
//import com.merlin.api.What;
//import com.merlin.bean.Path;
//import com.merlin.retrofit.Retrofit;
//import com.merlin.task.file.Cover;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.POST;
//
//public class FileCopyProcess extends FileProcess<Path> {
//    private final Path mFolder;
//    private final int mCoverMode;
//
//    public FileCopyProcess(String title,Path folder,int coverMode, ArrayList<Path> files){
//        super(title,files);
//        mFolder=folder;
//        mCoverMode=coverMode;
//    }
//
//    private interface Api{
//        @POST("/file/delete")
//        @FormUrlEncoded
//        Call<Reply<Processing>> copy(@Field(Label.LABEL_PATH) String path);
//    }
//
//    @Override
//    protected Reply onProcess(Path from,OnProcessUpdate update, Retrofit retrofit) {
////        return copyFile(from,mFolder,mCoverMode,update,retrofit);
//        return null;
//    }
//
//    private Reply copyFile(Path from,Path to,int coverMode,Progress update, Retrofit retrofit){
//        final String fromPath=null!=from?from.getPath():null;
//        final String toFolder=null!=to?to.getPath():null;
//        if (null==fromPath|fromPath.length()<=0||null==toFolder||toFolder.length()<=0){
//            return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Path invalid.",fromPath);
//        }
//        if (from.isLocal()){//Copy local file
//            File fromFile=new File(fromPath);
//            return to.isLocal()?copyLocalFileToLocal(fromFile,new File(toFolder),coverMode,update):
//                    copyLocalFileToNas(fromFile,to.getHostUri(),toFolder,coverMode,update,retrofit);
//        }
//        return to.isLocal()?downloadToLocal(from.getHostUri(),fromPath,new File(toFolder),coverMode,update,retrofit):
//                copyFromNasToNas(from,to,coverMode,update,retrofit);
//    }
//
//    private Reply copyFromNasToNas(Path from,Path toFolder,int coverMode,Progress update, Retrofit retrofit){
//        return null;
//    }
//
//    private Reply copyLocalFileToNas(File from,String toHostUri,String toFolder,int coverMode,Progress update,Retrofit retrofit){
//
//        return null;
//    }
//
//    private Reply<Path> copyLocalFileToLocal(File from, File toFolder, int coverMode, Progress update){
//        final String name=null!=from?from.getName():null;
//        final Path path=null!=from?Path.build(from):null;
//        if (null==name||name.length()<=0||null==toFolder){
//            return new Reply(true,What.WHAT_INVALID,"File invalid",path);
//        }else if (!from.exists()){
//            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",path);
//        }else if (!from.canRead()){
//            return new Reply(true,What.WHAT_NONE_PERMISSION,"File none read permission",path);
//        }else if (toFolder.exists()&&!toFolder.isDirectory()){//Copy file
//            return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
//        }
//        final File toFile=new File(toFolder,name);
//        if (toFile.exists()&&(from.isDirectory()==toFile.isDirectory())){
//            if (coverMode!= Cover.COVER_REPLACE) {
//                return new Reply(true, What.WHAT_EXIST, "File already exist", null);
//            }
//            File temp=null;
//            while ((temp=new File(toFolder,"."+name+"_"+(Math.random()*10000)+".temp")).exists()){
//                    //Do nothing
//            }
//            toFile.renameTo(temp);//Move exist to temp
//            if (toFile.exists()){
//                return new Reply(true, What.WHAT_FAIL, "Fail delete already exist", null);
//            }
//            Reply<Path> copyResult=copyLocalFileToLocal(from,toFolder,coverMode,update);
//            FileDelete fileDelete=new FileDelete();
//            if (null!=copyResult&&copyResult.isSuccess()&&copyResult.getWhat()==What.WHAT_SUCCEED){
////                notifyProgress("Deleting exist file ", Path.build(temp),0f,update);
//                fileDelete.deleteFile(temp,update);
//            }else{//Copy fail,Rollback just copied file(s)
////                notifyProgress("Rollback delete just copied file ", Path.build(temp),0f,update);
//                fileDelete.deleteFile(toFile,update);
//                if (!toFile.exists()&&temp.exists()){//Rollback delete succeed
//                    temp.renameTo(toFile);//Move backup back
//                }
//            }
//            return copyResult;
//        }
//        if (from.isDirectory()){//Copy file
//            File[] children=from.listFiles();
//            Reply<Path> lastFail=null;
//            if (null!=children&&children.length>0){
//                for (File child:children) {
//                    lastFail=copyLocalFileToLocal(child,null,coverMode,update);
//                }
//            }
//            return lastFail;
//        }else if (!toFolder.exists()){
//            toFolder.mkdirs();
//        }
//        if (!toFolder.exists()||!toFolder.isDirectory()){
//            return new Reply(true,What.WHAT_FAIL,"Target dir not folder or not exist",path);
//        }
//        try {
//            toFile.createNewFile();
//            if (toFile.exists()){
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //            !toFolder.exists()?toFolder.mkdirs()
////            if (toFolder.exists()){
////                if (!toFolder.isDirectory()){
////                    return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
////                }
////            }
////        else if (from.length()<=0){
////        }else{
////            notifyProgress("Copying file",path,null,update);
////            FileInputStream inputStream=null;FileOutputStream outputStream=null;
////            try {
////                long total=from.length();
////                inputStream= new FileInputStream(from);
////                outputStream= new FileOutputStream(toFile);
////                byte[] buffer=new byte[1024*1024];
////                int read=-1;long done=0;
////                while ((read=inputStream.read(buffer))>0){
////                    outputStream.write(buffer,0,read);
////                    done+=read;
////                    notifyProgress("Copying file",path,done/total,update);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////                return new Reply(true,What.WHAT_EXCEPTION,"Exception file copy",null);
////            }finally {
////                close(outputStream,inputStream);
////            }
////        }
//        return null;
//    }
//
//    private Reply downloadToLocal(String fromHostUri,String fromPath,File toFolder,int coverMode,Progress update,Retrofit retrofit){
//        return null;
//    }
//
////    private Reply<Path> deleteFile(File file,OnProcessUpdate update) {
////        if (file == null || !file.exists()) {
////            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",null);
////        }
////        File[] files=file.isDirectory()?file.listFiles():null;
////        if (null!=files&&files.length>0){//Delete child
////            for (File child : files) {
////                deleteFile(child,update); // 递规的方式删除文件夹
////            }
////        }
////        Path path=Path.build(file);
////        notifyProgress("Deleting file ",path,null,update);
////        file.delete();
////        return file.exists()?new Reply(true,What.WHAT_EXCEPTION,"Fail delete file",path):
////                new Reply<>(true,What.WHAT_SUCCEED,"Succeed delete file",path);
////    }
//
//}
//package com.merlin.browser;
//
//import com.browser.file.FileDelete;
//import com.browser.file.FileProcess;
//import com.browser.file.Progress;
//import com.merlin.api.Label;
//import com.merlin.api.Processing;
//import com.merlin.api.Reply;
//import com.merlin.api.What;
//import com.merlin.bean.Path;
//import com.merlin.retrofit.Retrofit;
//import com.merlin.task.file.Cover;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import retrofit2.Call;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.POST;
//
//public class FileCopyProcess extends FileProcess<Path> {
//    private final Path mFolder;
//    private final int mCoverMode;
//
//    public FileCopyProcess(String title,Path folder,int coverMode, ArrayList<Path> files){
//        super(title,files);
//        mFolder=folder;
//        mCoverMode=coverMode;
//    }
//
//    private interface Api{
//        @POST("/file/delete")
//        @FormUrlEncoded
//        Call<Reply<Processing>> copy(@Field(Label.LABEL_PATH) String path);
//    }
//
//    @Override
//    protected Reply onProcess(Path from,OnProcessUpdate update, Retrofit retrofit) {
////        return copyFile(from,mFolder,mCoverMode,update,retrofit);
//        return null;
//    }
//
//    private Reply copyFile(Path from,Path to,int coverMode,Progress update, Retrofit retrofit){
//        final String fromPath=null!=from?from.getPath():null;
//        final String toFolder=null!=to?to.getPath():null;
//        if (null==fromPath|fromPath.length()<=0||null==toFolder||toFolder.length()<=0){
//            return new Reply(true,What.WHAT_FAIL_UNKNOWN,"Path invalid.",fromPath);
//        }
//        if (from.isLocal()){//Copy local file
//            File fromFile=new File(fromPath);
//            return to.isLocal()?copyLocalFileToLocal(fromFile,new File(toFolder),coverMode,update):
//                    copyLocalFileToNas(fromFile,to.getHostUri(),toFolder,coverMode,update,retrofit);
//        }
//        return to.isLocal()?downloadToLocal(from.getHostUri(),fromPath,new File(toFolder),coverMode,update,retrofit):
//                copyFromNasToNas(from,to,coverMode,update,retrofit);
//    }
//
//    private Reply copyFromNasToNas(Path from,Path toFolder,int coverMode,Progress update, Retrofit retrofit){
//        return null;
//    }
//
//    private Reply copyLocalFileToNas(File from,String toHostUri,String toFolder,int coverMode,Progress update,Retrofit retrofit){
//
//        return null;
//    }
//
//    private Reply<Path> copyLocalFileToLocal(File from, File toFolder, int coverMode, Progress update){
//        final String name=null!=from?from.getName():null;
//        final Path path=null!=from?Path.build(from):null;
//        if (null==name||name.length()<=0||null==toFolder){
//            return new Reply(true,What.WHAT_INVALID,"File invalid",path);
//        }else if (!from.exists()){
//            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",path);
//        }else if (!from.canRead()){
//            return new Reply(true,What.WHAT_NONE_PERMISSION,"File none read permission",path);
//        }else if (toFolder.exists()&&!toFolder.isDirectory()){//Copy file
//            return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
//        }
//        final File toFile=new File(toFolder,name);
//        if (toFile.exists()&&(from.isDirectory()==toFile.isDirectory())){
//            if (coverMode!= Cover.COVER_REPLACE) {
//                return new Reply(true, What.WHAT_EXIST, "File already exist", null);
//            }
//            File temp=null;
//            while ((temp=new File(toFolder,"."+name+"_"+(Math.random()*10000)+".temp")).exists()){
//                    //Do nothing
//            }
//            toFile.renameTo(temp);//Move exist to temp
//            if (toFile.exists()){
//                return new Reply(true, What.WHAT_FAIL, "Fail delete already exist", null);
//            }
//            Reply<Path> copyResult=copyLocalFileToLocal(from,toFolder,coverMode,update);
//            FileDelete fileDelete=new FileDelete();
//            if (null!=copyResult&&copyResult.isSuccess()&&copyResult.getWhat()==What.WHAT_SUCCEED){
////                notifyProgress("Deleting exist file ", Path.build(temp),0f,update);
//                fileDelete.deleteFile(temp,update);
//            }else{//Copy fail,Rollback just copied file(s)
////                notifyProgress("Rollback delete just copied file ", Path.build(temp),0f,update);
//                fileDelete.deleteFile(toFile,update);
//                if (!toFile.exists()&&temp.exists()){//Rollback delete succeed
//                    temp.renameTo(toFile);//Move backup back
//                }
//            }
//            return copyResult;
//        }
//        if (from.isDirectory()){//Copy file
//            File[] children=from.listFiles();
//            Reply<Path> lastFail=null;
//            if (null!=children&&children.length>0){
//                for (File child:children) {
//                    lastFail=copyLocalFileToLocal(child,null,coverMode,update);
//                }
//            }
//            return lastFail;
//        }else if (!toFolder.exists()){
//            toFolder.mkdirs();
//        }
//        if (!toFolder.exists()||!toFolder.isDirectory()){
//            return new Reply(true,What.WHAT_FAIL,"Target dir not folder or not exist",path);
//        }
//        try {
//            toFile.createNewFile();
//            if (toFile.exists()){
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //            !toFolder.exists()?toFolder.mkdirs()
////            if (toFolder.exists()){
////                if (!toFolder.isDirectory()){
////                    return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
////                }
////            }
////        else if (from.length()<=0){
////        }else{
////            notifyProgress("Copying file",path,null,update);
////            FileInputStream inputStream=null;FileOutputStream outputStream=null;
////            try {
////                long total=from.length();
////                inputStream= new FileInputStream(from);
////                outputStream= new FileOutputStream(toFile);
////                byte[] buffer=new byte[1024*1024];
////                int read=-1;long done=0;
////                while ((read=inputStream.read(buffer))>0){
////                    outputStream.write(buffer,0,read);
////                    done+=read;
////                    notifyProgress("Copying file",path,done/total,update);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////                return new Reply(true,What.WHAT_EXCEPTION,"Exception file copy",null);
////            }finally {
////                close(outputStream,inputStream);
////            }
////        }
//        return null;
//    }
//
//    private Reply downloadToLocal(String fromHostUri,String fromPath,File toFolder,int coverMode,Progress update,Retrofit retrofit){
//        return null;
//    }
//
////    private Reply<Path> deleteFile(File file,OnProcessUpdate update) {
////        if (file == null || !file.exists()) {
////            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",null);
////        }
////        File[] files=file.isDirectory()?file.listFiles():null;
////        if (null!=files&&files.length>0){//Delete child
////            for (File child : files) {
////                deleteFile(child,update); // 递规的方式删除文件夹
////            }
////        }
////        Path path=Path.build(file);
////        notifyProgress("Deleting file ",path,null,update);
////        file.delete();
////        return file.exists()?new Reply(true,What.WHAT_EXCEPTION,"Fail delete file",path):
////                new Reply<>(true,What.WHAT_SUCCEED,"Succeed delete file",path);
////    }
//
//}
