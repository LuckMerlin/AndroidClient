package com.merlin.browser;

import com.merlin.api.ApiMap;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.Path;
import com.merlin.file.R;
import com.merlin.debug.Debug;
import com.merlin.lib.Canceler;
import com.merlin.retrofit.Retrofit;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class FileDeleteProcess extends FileProcess<Path> {

    public FileDeleteProcess(){
        this(null,null);
    }

    public FileDeleteProcess(String title,ArrayList<Path> files){
        super(title,files);
    }

    private interface Api{
        @POST("/file/delete")
        @FormUrlEncoded
        Call<Reply<ApiMap<String,Reply<String>>>> delete(@Field(Label.LABEL_PATH) String... paths);
    }

    @Override
    public final Canceler onProcess(OnProcessUpdate update, OnApiFinish apiFinish, Retrofit retrofit) {
        if (null==update||null==apiFinish){
            Debug.W(getClass(),"Can't process file delete with invalid args "+update+" "+apiFinish);
            return null;
        }
        synchronized (this){
            if (size()>0){
                for (Path pathObj:this) {
                    final String path=null!=pathObj?pathObj.getPath():null;
                    if (null==path|path.length()<=0){
                        update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.deleteFail, pathObj, null, path);
                    }else if (pathObj.isLocal()){//Delete local file
                        if (null==path||path.length()<=0){
                            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.deleteFail,pathObj,null,path);
                        }else{
                            File file=new File(path);
                            deleteLocalFile(file,update);
                            boolean exist=file.exists();
                            update.onProcessUpdate(exist?What.WHAT_FAIL_UNKNOWN:What.WHAT_SUCCEED,
                                    exist?R.string.deleteFail:R.string.deleteSucceed,pathObj,null,path);
                        }
                    }else{//Delete cloud file
                        try {
                            Response<Reply<ApiMap<String,Reply<String>>>> response=retrofit.prepare(Api.class,
                                    pathObj.getHostUri(),null).delete(path).execute();
                            Reply<ApiMap<String,Reply<String>>> reply=null!=response?response.body():null;
                            ApiMap<String,Reply<String>> map=null!=reply?reply.getData():null;
                            Reply<String> apiReply=null!=map?map.get(path):null;
                            if (null!=apiReply){
                                String note=apiReply.getNote();
                                boolean succeed=apiReply.isSuccess()&&apiReply.getWhat()==What.WHAT_SUCCEED;
                                update.onProcessUpdate(succeed?What.WHAT_SUCCEED:What.WHAT_FAIL_UNKNOWN,
                                        succeed?R.string.succeed:(null!=note&&note.length()>0?note:R.string.fail),
                                        pathObj,null,path);
                            }else{
                                update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.fail,pathObj,null,path);
                            }
                        } catch (IOException e) {
                            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN, R.string.exception,pathObj,null,path);
                        }
                    }
                }
            }
        }
        apiFinish.onApiFinish(What.WHAT_SUCCEED,"Finish delete files.",null,null);
        return null;
    }

    private boolean deleteLocalFile(File file,OnProcessUpdate update){
        Path filePath=null;
        if (null==file||!file.exists()||null==(filePath=Path.build(file,false))){
            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.fileNotExist,filePath,null,file);
        }else if (!file.canWrite()){
            update.onProcessUpdate(What.WHAT_FAIL_UNKNOWN,R.string.nonePermission,filePath,null,file);
        }else{
            File[] files=file.isDirectory()?file.listFiles():null;
            if (null==files||files.length<=0){
                file.delete();
                Debug.D(getClass(),"Delete file "+file);
                boolean succeed=!file.exists();
                update.onProcessUpdate(succeed?What.WHAT_SUCCEED:What.WHAT_FAIL_UNKNOWN,
                        R.string.nonePermission,filePath,null,file);
                return succeed;
            }
            for (File child:files) {
                if (null==child||!deleteLocalFile(child,update)){
                    break;
                }
            }
            files=file.isDirectory()?file.listFiles():null;
            if (null==files||files.length<=0){
                Debug.D(getClass(),"Delete folder "+file);
                file.delete();
            }
            boolean succeed=!file.exists();
            update.onProcessUpdate(succeed?What.WHAT_SUCCEED:What.WHAT_FAIL_UNKNOWN,R.string.nonePermission,filePath,null,file);
            return succeed;
        }
        return false;
    }

}
