package com.merlin.browser;

import com.merlin.api.Label;
import com.merlin.util.Encoder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileSaveBuilder implements Label {

    public MultipartBody.Part createFilePart(File file,String name,String toFolder){
        if (null!=file){
            name=null!=name&&name.length()>0?name:file.getName();
            RequestBody body=null!=file?new FileUploadBody(file.getAbsolutePath()):null;
            Headers.Builder builder=null!=body?createFileHeadersBuilder(name,toFolder,file.isDirectory()):null;
            return createFilePart(builder,body);
        }
        return null;
    }

    public MultipartBody.Part createFilePart(Headers.Builder builder, RequestBody body){
        return null!=builder&&null!=body?MultipartBody.Part.create(builder.build(),body):null;
    }

    public Headers.Builder createFileHeadersBuilder(String name,String toFolder,boolean isDirectory){
        name=null!=name&&name.length()>0?name:"luckmerlin"+System.currentTimeMillis();
        StringBuilder disposition = new StringBuilder("form-data; name="+name+";filename=luckmerlin"+name);
        Headers.Builder headersBuilder = new Headers.Builder().addUnsafeNonAscii(
                "Content-Disposition", disposition.toString());
        if (null!=name&&name.length()>0){
            String folder=toFolder;
            name= null!=name?name:"";
            Encoder encoder=new Encoder();
            String encoding="utf-8";
            headersBuilder.add(LABEL_NAME,encoder.encode(name,"",encoding));
            headersBuilder.add(LABEL_PARENT,encoder.encode(folder,"",encoding));
            headersBuilder.add(LABEL_PATH_SEP,encoder.encode(File.separator,"",encoding));
            if (isDirectory){
                headersBuilder.add(LABEL_FOLDER,LABEL_FOLDER);
            }
        }
        return headersBuilder;
    }
}
