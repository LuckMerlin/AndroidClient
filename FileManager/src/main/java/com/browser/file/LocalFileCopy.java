//package com.browser.file;
//
//import com.merlin.api.Reply;
//import com.merlin.api.What;
//import com.merlin.bean.Path;
//import com.merlin.debug.Debug;
//import com.merlin.task.file.Cover;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//public final class FileCopy extends FileAction {
//
//    public Reply<Path> copy(File from, File toFolder, String name,int coverMode,Progress progress){
//        final String fromName=null!=from?from.getName():null;
//        final Path path=null!=from?Path.build(from):null;
//        if (null==fromName||fromName.length()<=0||null==toFolder||null==name||name.length()<=0){
//            return new Reply(true, What.WHAT_INVALID,"File invalid",path);
//        }else if (!from.exists()){
//            return new Reply(true,What.WHAT_NOT_EXIST,"File not exist",path);
//        }else if (!from.canRead()){
//            return new Reply(true,What.WHAT_NONE_PERMISSION,"File none read permission",path);
//        }else if (toFolder.exists()&&!toFolder.isDirectory()){//Copy file
//            return new Reply(true,What.WHAT_NOT_DIRECTORY,"Target dir not folder",path);
//        }
//        final File toFile=new File(toFolder,name);
//        if (toFile.exists()){
//            if (coverMode!= Cover.COVER_REPLACE) {
//                return new Reply(true, What.WHAT_EXIST, "File already exist", Path.build(toFile));
//            }
//            File temp=null;
//            while ((temp=new File(toFolder,"."+name+"_"+(Math.random()*10000)+".temp")).exists()){
//                //Do nothing
//            }
//            toFile.renameTo(temp);//Move exist to temp
//            if (toFile.exists()){
//                return new Reply(true, What.WHAT_FAIL, "Fail delete already exist", null);
//            }
//           Reply<Path> reply=copy(from,toFolder,name,coverMode,progress);
//           FileDelete fileDelete=new FileDelete();
//           if (null==reply||reply.getWhat()!=What.WHAT_SUCCEED){//Copy fail,rollback
//              fileDelete.deleteFile(toFile,progress);
//              if (!toFile.exists()){
//                  temp.renameTo(toFile);//Rollback
//              }
//           }else{//Copy succeed, delete temp
//               fileDelete.deleteFile(temp,progress);//Delete temp
//           }
//           return reply;
//        }
//        Path copyFrom=Path.build(from);
//        Path copyTo=Path.build(toFile);
//        notify("Copying ",copyFrom,copyTo,0f,progress);
//        if (from.isDirectory()){
//            File[] files=from.listFiles();
//            Reply<Path> dirReply=null;
//            if (null!=files&&files.length>0){
//                Reply<Path> reply=null;
//                for (File child:files){
//                    String childName=child.getName();
//                    if (null==childName||childName.length()<=0){
//                        Debug.W(getClass(),"Skip copy one file which NONE name");
//                        continue;
//                    }
//                    reply=copy(child,toFile,childName,coverMode,progress);
//                    dirReply=null!=reply&&reply.getWhat()!=What.WHAT_SUCCEED?reply:dirReply;
//                }
//            }else{
//                toFile.mkdirs();
//                boolean succeed=toFile.exists();
//                dirReply= new Reply(true,succeed?What.WHAT_SUCCEED:What.WHAT_CREATE_FAILED,succeed?"Succeed create folder":"Fail create folder",path);
//            }
//            return dirReply;
//        }else{
//            InputStream inputStream=null;OutputStream outputStream=null;
//            try {
//                toFile.createNewFile();
//                if (!toFile.exists()){
//                    return new Reply(true,What.WHAT_CREATE_FAILED,"Fail create target path",path);
//                }
//                final long total=from.length();
//                outputStream=new FileOutputStream(toFile);
//                inputStream=new FileInputStream(from);
//                byte[] buffer=new byte[1024*1024];
//                int read=-1;long write=0;
//                String note="Copying file";
//                while ((read=inputStream.read(buffer))>=0){
//                    if (read>0){
//                        outputStream.write(buffer,0,read);
//                        write+=read;
//                        notify(note, copyFrom,copyTo,write*100.f/total,progress);
//                    }
//                }
//                return new Reply(true,What.WHAT_SUCCEED,"Copy succeed",path);
//            } catch (IOException e) {
//                e.printStackTrace();
//                toFile.delete();//Delete target file
//                return new Reply(true,What.WHAT_EXCEPTION,"Exception copy file",path);
//            }finally {
//                close(inputStream,outputStream);
//            }
//        }
//    }
//
//}
