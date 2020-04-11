package com.merlin.conveyor;

import androidx.annotation.Nullable;

import com.merlin.api.ApiList;
import com.merlin.api.Label;
import com.merlin.api.Reply;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;
import com.merlin.server.Retrofit;

import java.io.File;

import retrofit2.Call;

public final class FileUploadConvey extends _ConveyGroup<FileUploadConvey.FileConvey> implements Label{
    private final File mFile;
    private final String mFolder;
    private final int mCoverMode;

    public FileUploadConvey(File file,String url,String folder,int coverMode){
        super(null!=file?file.getName():null);
        mFile=file;
        mFolder=folder;
        mCoverMode=coverMode;
    }

    @Override
    protected Reply onPrepare(Retrofit retrofit,String debug) {
        if (null==retrofit){
            return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
        }
        final File file=mFile;
        if (null==file||!file.exists()){
            return new Reply(false,WHAT_FILE_EXIST,"File not exist.",file);
        }else if (!file.canRead()){
            return new Reply(false,WHAT_NONE_PERMISSION,"File none read permission.",file);
        }
        return iteratorAddAllFileInDirectory(file.getParent(),file,mFolder,debug);
    }

    private Reply iteratorAddAllFileInDirectory(String root,File file,String folder, String debug){
        if (null!=file&&null!=root&&root.length()>0){
            String parent=file.getParent();
            if (null==parent||parent.length()<=0){
                Debug.W(getClass(),"Can't iterator add all file while parent is NULL."+file);
                return null;
            }
            String targetFolderName=parent.replaceAll(root,"");
            if (null!=folder&&folder.length()>0){
                folder= folder.endsWith(File.separator)?folder:folder+File.separator;
                if(null!=targetFolderName&&targetFolderName.length()>0){
                    targetFolderName= targetFolderName.startsWith(File.separator)?
                            targetFolderName.replaceFirst(File.separator,""): targetFolderName;
                }
                targetFolderName=folder+targetFolderName;
            }
            addChild(new FileConvey(file,targetFolderName),debug);
            File[] files=file.isDirectory()?file.listFiles():null;;
            if (null!=files){
                for (File child:files) {
                    if (null!=child){
                        iteratorAddAllFileInDirectory(root, child,folder,debug);
                    }
                }
            }
        }
        return null;
    }

    public File getFile() {
        return mFile;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (null!=obj&&obj instanceof FileUploadConvey){
            FileUploadConvey convey=((FileUploadConvey)obj);
            String folder=convey.mFolder;
            File file=convey.mFile;
            String filePath=null!=file?file.getAbsolutePath():null;
            File currFile=mFile;
            String currFilePath=null!=currFile?currFile.getAbsolutePath():null;
            if (((null==folder&&null==mFolder)||(null!=folder&&null!=mFolder&&folder.equals(mFolder)))&&
                    (null==filePath&&null==currFilePath)||(null!=filePath&&null!=currFilePath&&filePath.equals(currFilePath))){
                return true;
            }
        }
        return super.equals(obj);
    }

    protected final static class FileConvey extends _Convey {
        private final String mFilePath;
        private final String mFolder;
        private Call<Reply<ApiList<Reply<NasFile>>>> mUploadingCall;

        private FileConvey(File file,String folder){
            super(null!=file?file.getName():null);
            mFilePath= null!=file?file.getAbsolutePath():null;
            mFolder=folder;
        }

//        @Override
//        protected Reply onPrepare(Retrofit retrofit,String debug) {
//            if (null==retrofit){
//                return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
//            }
//            String filePath=mFilePath;
//            final File file=null!=filePath&&filePath.length()>0?new File(filePath):null;
//            if (null==file||!file.exists()){
//                return new Reply(false,WHAT_FILE_EXIST,"File not exist.",null);
//            }else if (!file.canRead()){
//                return new Reply(false,WHAT_NONE_PERMISSION,"File none read permission.",null);
//            }
//            return null;
//        }

//        @Override
//        protected Reply onStart(Retrofit retrofit,Finisher finish, String debug) {
//            if (null==retrofit){
//                Debug.W(getClass(),"Can't upload file with NULL retrofit."+(null!=debug?debug:"."));
//                return new Reply(false,WHAT_ARGS_INVALID,"None retrofit.",null);
//            }
//            String filePath=mFilePath;
//            final File file=null!=filePath&&filePath.length()>0?new File(filePath):null;
//            if (null==file||!file.exists()){
//                Debug.W(getClass(),"Can't upload file which not exist."+(null!=debug?debug:"."));
//                return new Reply(false,WHAT_FILE_EXIST,"File not exist.",null);
//            }else if (!file.canRead()){
//                Debug.W(getClass(),"Can't upload file which none read permission."+(null!=debug?debug:"."));
//                return new Reply(false,WHAT_NONE_PERMISSION,"File none read permission.",null);
//            }else if (null!=mUploadingCall){
//                Debug.W(getClass(),"Can't upload file which is already uploading."+(null!=debug?debug:"."));
//                return new Reply(false,WHAT_ALREADY_DOING,"File already uploading.",null);
//            }
//
//            final FileUploadBody requestBody = new FileUploadBody(file.getAbsolutePath()){
//                @Override
//                protected void onTransportProgress(long uploaded, long total, float speed) {
//                    if (null!=finish){
//                        finish.onProgress(uploaded,total,speed,FileConvey.this);
//                    }
//                }
//
//                @Override
//                protected boolean isCancel() {
//                    return FileConvey.this.isCancel();
//                }
//            };
//            FileSaveBuilder builder=new FileSaveBuilder();
//            MultipartBody.Part part=builder.createFilePart(builder.createFileHeadersBuilder(file.getName()
//                    ,mFolder,file.isDirectory()),requestBody);
//            Debug.D(getClass(),"Upload file "+file.getName()+" to "+mFolder+" "+(null!=debug?debug:"."));
//            Reply<ApiList<Reply<NasFile>>> responseReply=null;
//            Call<Reply<ApiList<Reply<NasFile>>>> call=null;
//            try {
//                if (null==part){
//                    Debug.W(getClass(),"Can't upload file which part is NULL."+(null!=debug?debug:"."));
//                    return new Reply(false,WHAT_ERROR_UNKNOWN,"Error on part NULL while file upload.",null);
//                }
//                call=mUploadingCall=retrofit.prepare(ApiSaveFile.class, Address.LOVE_ADDRESS).save(part);
//                if (null==call){
//                    Debug.W(getClass(),"Can't upload file which upload call is NULL."+(null!=debug?debug:"."));
//                    return new Reply(false,WHAT_ERROR_UNKNOWN,"Error on NULL file upload call.",null);
//                }
//                Response<Reply<ApiList<Reply<NasFile>>>> response=call.execute();
//                responseReply=null!=response?response.body():null;
//            } catch (IOException e) {
//                if (e instanceof SocketTimeoutException){
//                    responseReply=new Reply(false,WHAT_TIMEOUT,"Timeout call file upload api."+e,e);
//                }else if (e instanceof ConnectException){
//                    responseReply=new Reply(false,WHAT_NONE_NETWORK,"Network exception call file upload api."+e,e);
//                }else if(e instanceof ProtocolException){
//                    String message=e.getMessage();
//                    if (null!=message&&message.equals(Integer.toString(What.WHAT_CANCEL))){
//                        responseReply=new Reply(false,WHAT_CANCEL,"Canceled call file upload api."+e,e);
//                    }else{
//                        responseReply=new Reply(false,WHAT_EXCEPTION,"Exception call file upload api."+e,e);
//                    }
//                }else{
//                    responseReply=new Reply(false,WHAT_EXCEPTION,"Exception call file upload api."+e,e);
//                }
//                Debug.E(getClass(),"Exception call file upload api."+e,e);
//            }
//            Call<Reply<ApiList<Reply<NasFile>>>> curr=mUploadingCall;
//            if (null!=curr&&null!=call&&curr==call){
//                mUploadingCall=null;
//            }
//            if (null!=finish){
//                ApiList<Reply<NasFile>> list=responseReply.getData();
//                finish.onFinish(null!=list&&list.size()==1?list.get(0):new Reply<>(true,
//                        What.WHAT_ERROR_UNKNOWN,"Unknown reply error.",null));
//            }
//            return null;
//        }

        @Override
        protected Boolean onCancel(Retrofit retrofit,boolean cancel, String debug) {
            Call<Reply<ApiList<Reply<NasFile>>>> curr=mUploadingCall;
            if (null!=retrofit&&null!=curr){
                if (cancel&&!curr.isCanceled()){
                    Debug.D(getClass(),"Canceling file upload "+mFilePath+" "+(null!=debug?debug:"."));
                    curr.cancel();
                    return true;
                }
            }
            return false;
        }
    }
}
