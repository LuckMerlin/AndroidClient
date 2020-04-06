package com.merlin.browser;

import com.merlin.api.Address;
import com.merlin.api.ApiMap;
import com.merlin.api.Canceler;
import com.merlin.api.CoverMode;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.FileMeta;
import com.merlin.bean.LocalFile;
import com.merlin.bean.NasFile;
import com.merlin.bean.Path;
import com.merlin.client.R;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;
import com.merlin.util.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.merlin.api.Label.LABEL_MODE;
import static com.merlin.api.Label.LABEL_PARENT;
import static com.merlin.api.Label.LABEL_PATH;

public final class FileCopyProcess extends FileProcess<FileMeta> {
    private final Integer mCoverMode;
    private final String mFolder;

    private interface Api{
        @POST(Address.PREFIX_FILE+"/copy")
        @FormUrlEncoded
        Call<Reply<ApiMap<String,Reply<String>>>> copyPaths(@Field(LABEL_PARENT)String folder,@Field(LABEL_MODE) Integer coverMode, @Field(LABEL_PATH) String ...paths);
    }

    public FileCopyProcess(Object title, ArrayList<FileMeta> files, String folder, Integer coverMode){
        super(title,files);
        mFolder=folder;
        mCoverMode=coverMode;
    }

    @Override
    public Canceler onProcess(OnProcessUpdate update, OnApiFinish apiFinish, Retrofit retrofit) {
        final String folder=mFolder;
        if (null==update||null==apiFinish||null==folder){
            Debug.W(getClass(),"Can't process file copy with invalid args "+update+" "+apiFinish);
            return null;
        }
        final Integer coverMode=mCoverMode;
        synchronized (this) {
            if (size() > 0) {
                for (FileMeta meta:this) {
                    if (null==meta){
                        continue;
                    }
                    final String path=meta.getPath(false);
                    Path fromPath=new Path(meta instanceof NasFile ?((NasFile)meta).getHost():null,meta.getParent(), meta.getName(false),meta.getExtension());
                    if (null==path||path.length()<=0) {
                        update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.fail, fromPath, null, meta);
                    }else if (meta instanceof LocalFile){//Copy local file
                        File file=new File(path);
                        copyLocalFile(file,folder,coverMode,update);
                        update.onProcessUpdate(What.WHAT_SUCCEED, R.string.succeed,fromPath,null,meta);
                    }else if (meta instanceof NasFile){
                        if (null==retrofit){
                            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.inputNotNull,fromPath,null,meta);
                        }else {
                            try {
                                Response<Reply<ApiMap<String, Reply<String>>>> response = retrofit.prepare(Api.class,
                                        Address.URL, null).copyPaths(folder,coverMode,path).execute();
                                Reply<ApiMap<String, Reply<String>>> reply = null != response ? response.body() : null;
                                ApiMap<String, Reply<String>> map = null != reply ? reply.getData() : null;
                                Reply<String> apiReply = null != map ? map.get(path) : null;
                                if (null != apiReply) {
                                    String note = apiReply.getNote();
                                    boolean succeed = apiReply.isSuccess() && apiReply.getWhat() == What.WHAT_SUCCEED;
                                    update.onProcessUpdate(succeed ? What.WHAT_SUCCEED : What.WHAT_FAIL_UNKNOWN,
                                            succeed ? R.string.succeed : (null != note && note.length() > 0 ? note : R.string.fail), fromPath, null, meta);
                                } else {
                                    update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.fail, fromPath, null, meta);
                                }
                            } catch (IOException e) {
                                update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.exception, fromPath, null, meta);
                            }
                        }
                    }
                }
            }
        }
//        apiFinish.onApiFinish(What.WHAT_SUCCEED,"Finish copy.",null,null);
        return null;
    }

    private boolean copyLocalFile(final File file,String folderPath,Integer coverMode,OnProcessUpdate update){
        final Path fromPath=null!=file&&null!=update?Path.build(file,null):null;
        final Path toPath=null;
        if (null==fromPath||null==folderPath){
            return false;
        }
        if (!file.exists()){
            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.fileNotExist,fromPath,toPath,file);
        }else if (!file.canRead()){
            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.nonePermission,fromPath,toPath,file);
        }else{
            final File targetFile=new File(folderPath+File.separator+file.getName());
            if (file.getAbsolutePath().equals(targetFile.getAbsolutePath())){//If same file
                Debug.D(getClass(),"Copy target file is same as source file."+targetFile);
                update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.fileAlreadyExist,fromPath,toPath,file);
                return true;
            }
            if (file.isDirectory()){
                if (!targetFile.exists()){
                    targetFile.mkdirs();
                }
                if (!targetFile.exists()||!targetFile.isDirectory()){
                    Debug.D(getClass(),"Fail create copy folder."+targetFile);
                    update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.createFail,fromPath,toPath,file);
                    return false;
                }
                File[] files=file.listFiles();
                if (null!=files&&files.length>0){
                    for (File child:files) {
                        copyLocalFile(child,targetFile.getAbsolutePath(),coverMode,update);
                    }
                }
            }
            if (targetFile.exists()&&null!=coverMode){
                switch (coverMode){
                    case CoverMode.REPLACE:
                        Debug.D(getClass(),"Copy replacing file "+targetFile);
                        break;
                    case CoverMode.SKIP:
                        update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.skip,fromPath,toPath,file);
                        return true;
                    case CoverMode.KEEP:
                    case CoverMode.NONE:
                    default:
//                        Interrupt interrupt= new Interrupt();
//                        update.onProcessUpdate(What.WHAT_INTERRUPT,R.string.fileAlreadyExist,fromPath,toPath,interrupt);
//                        while (true){
//                            synchronized (interrupt){
//                                try {
//                                    interrupt.wait(10*1000);
//                                    Debug.D(getClass(),"AAAAAAAAA 与全国了 "+interrupt.getWhat());
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                    if (null!=folderPath){
//                                        break;
//                                    }
//                                }
//                            }
//                        }
                        return false;
                }
            }
            FileInputStream fis=null;
            FileOutputStream fos=null;
            File temp=new File(targetFile.getAbsolutePath()+"_"+System.currentTimeMillis());
            temp.deleteOnExit();
            try {
                 fis=new FileInputStream(file);
                 fos=new FileOutputStream(temp);
                 int maxBufferSize=1024*1024*1;
                 long length=file.length();
                 byte[] buffer=new byte[(int)(length<=0?1:length>maxBufferSize?maxBufferSize:length)];
                 int readCount;
                 Debug.D(getClass(),"Copying file from "+file+" to "+targetFile);
                 while ((readCount=fis.read(buffer))>0){
                     Debug.D(getClass(),""+readCount);
                     fos.write(buffer,0,readCount);
                 }
                 if (length==temp.length()){
                     temp.renameTo(targetFile);
                     temp.delete();
                 }
                 boolean succeed=length==targetFile.length();
                 Debug.D(getClass(),"Finish copy file "+succeed+" "+targetFile);
                 update.onProcessUpdate(succeed?What.WHAT_SUCCEED:What.WHAT_FAIL_UNKNOWN,
                        succeed?R.string.succeed:R.string.fail,fromPath,toPath,file);
            } catch (Exception e) {
                Debug.E(getClass(),"Exception copy file "+e+" "+targetFile,e);
                e.printStackTrace();
            }finally {
                new Closer().close(fos,fis);
            }
        }
        return false;
    }
}
