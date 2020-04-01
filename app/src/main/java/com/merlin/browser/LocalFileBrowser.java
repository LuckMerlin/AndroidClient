package com.merlin.browser;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.view.View;
import android.webkit.MimeTypeMap;

import com.merlin.activity.PhotoPreviewActivity;
import com.merlin.api.Canceler;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.PageData;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FileMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.LocalFile;
import com.merlin.client.R;
import com.merlin.client.databinding.LocalFileDetailBinding;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.util.Thumbs;

import java.io.File;
import java.util.ArrayList;

public class LocalFileBrowser extends FileBrowser{

    public LocalFileBrowser(Context context, ClientMeta meta,Callback callback){
        super(context,meta,callback);
    }

    @Override
    protected Canceler onPageLoad(Object path, int from, OnApiFinish finish) {
        final Canceler canceler=(boolean cancel, String debug)->{
            return false;
        };
        return browserFolder(null!=path&&path instanceof String?(String)path:null,
                from,from+50,finish)?canceler:null;
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        switch (clickCount){

        }
        return super.onTapClick(view, clickCount, resId, data);
    }

    private boolean browserFolder(String path, int from, int to, OnApiFinish<Reply<PageData<LocalFile>>> finish){
        path=null!=path&&path.length()>0?path:getClientRoot();
        File folder=null!=path&&path.length()>0?new File(path):null;
        final Reply<PageData<LocalFile>>  reply=new Reply<>();
        Integer what=null;
        boolean succeed=false;
        String note=null;
        Object arg=null;
        if (null==folder||folder.length()<=0){
            note=getText(R.string.pathInvalid);
            what=What.WHAT_ARGS_INVALID;
        }else if(from<0||to<=0){
            note=getText(R.string.inputNotNull);
            what=What.WHAT_ARGS_INVALID;
        }else if(!folder.exists()){
            note=getText(R.string.fileNotExist);
            what=What.WHAT_NOT_EXIST;
        }else if(!folder.exists()){
            note=getText(R.string.fileNotExist);
            what=What.WHAT_NOT_EXIST;
        }else if(!folder.isDirectory()){
            note=getText(R.string.pathInvalid);
            what=What.WHAT_NOT_DIRECTORY;
        } else if(!folder.canRead()){
            note=getText(R.string.nonePermission);
            what=What.WHAT_NONE_PERMISSION;
        }
        if (null==what){
            succeed=true;
            final File[] files=folder.listFiles();
            final int length=null!=files?files.length:0;
//            if (length>0){
//                Arrays.sort(files,mComparator);
//            }
            FolderData<LocalFile> folderData=new FolderData<>();
            folderData.setParent(folder.getParent());
            folderData.setPathSep(File.separator);
            folderData.setName(folder.getName());
            if (from>=length){
                what=What.WHAT_OUT_OF_BOUNDS;
                note=getText(R.string.outOfBounds);
            }
            if (what==null){
                what=What.WHAT_SUCCEED;
                succeed=true;
                to = Math.min(to,length);
                Debug.D(getClass(),"Browsing local folder from "+from+" to "+to+" "+path);
                ArrayList<LocalFile> list=new ArrayList();
                for (int i = from; i < to; i++) {
                    File child=files[i];
                    if (null!=child){
                        list.add(LocalFile.create(child,null));
                    }
                }
                folderData.setData(list);
            }
            folderData.setFrom(from);
            folderData.setLength(length);
            reply.setData(folderData);
        }
        if (!succeed){
            Debug.D(getClass(),"Fail browser local folder."+note+" "+folder);
        }
        reply.setSuccess(succeed);
        reply.setNote(note);
        reply.setWhat(null!=what?what:What.WHAT_INVALID);
        if (null!=finish){
            finish.onApiFinish(reply.getWhat(),note,reply,arg);
        }
        return true;
    }

    @Override
    protected boolean onShowFileDetail(View view, FileMeta meta, String debug) {
        if (null!=view){
            String path=null!=meta&&meta instanceof LocalFile?meta.getPath():null;
            File file=null!=path&&path.length()>0&&path.startsWith(File.separator)?new File(path):null;
            if (null!=file&&file.exists()){
                LocalFileDetailBinding binding=(LocalFileDetailBinding)inflate(R.layout.local_file_detail);
                binding.setFile(((LocalFile)meta).getFile());
                String title=meta.getTitle();
                binding.setTitle(null!=title?title:file.getName());
                Dialog dialog=new Dialog(view.getContext());
                return dialog.setContentView(binding).setBackground(new Thumbs().getThumb(path)).title(file.getName()).show();
            }
            Debug.W(getClass(),"Can't show local file detail which not exist "+path+" "+(null!=debug?debug:"."));
            toast(R.string.fileNotExist);
            return false;
        }
        Debug.W(getClass(),"Can't show local file detail view arg is NULL "+(null!=debug?debug:"."));
        return false;
    }

    @Override
    protected boolean onSetAsHome(View view, String path, String debug) {
        File file=null!=path&&path.length()>0&&path.startsWith(File.separator)?new File(path):null;
        if (null!=file&&file.exists()&&file.isDirectory()&&new LocalBrowserHome().set(getViewContext(),path)){
            return toast(R.string.succeed)||true;
        }
        return toast(R.string.fail)||false;
    }

    @Override
    protected boolean openFile(FileMeta meta, String debug) {
        LocalFile localFile=null!=meta&&meta instanceof LocalFile?((LocalFile)meta):null;
        String path=localFile.getPath();
        if (null!=path&&path.length()>0){
            final File file=new File(path);
            if (!file.exists()){
                return toast(R.string.fileNotExist)&&false;
            }
            Thumbs thumbs=new Thumbs();
            String extension=thumbs.getExtension(path);
            if (thumbs.isImageExtension(extension)){
                Intent intent=new Intent(getViewContext(), PhotoPreviewActivity.class);
                intent.putExtra(Label.LABEL_DATA, Uri.fromFile(file));
                return startActivity(intent);
            }
            String mimeType=null!=extension&&extension.length()>0? MimeTypeMap.getSingleton().
                    getMimeTypeFromExtension(extension):null;
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);//动作，查看
            intent.setDataAndType(Uri.fromFile(file), mimeType);//设置类型
            startActivity(intent);
            return true;
        }
        return super.openFile(meta, debug);
    }
}
