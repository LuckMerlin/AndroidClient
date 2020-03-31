package com.merlin.browser;

import com.merlin.api.Label;

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
            RequestBody body=null!=file?new FileUploadBody(file):null;
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
            headersBuilder.add(LABEL_NAME,encode(name,""));
            headersBuilder.add(LABEL_PARENT,encode(folder,""));
            headersBuilder.add(LABEL_PATH_SEP,encode(File.separator,""));
            if (isDirectory){
                headersBuilder.add(LABEL_FOLDER,LABEL_FOLDER);
            }
        }
        return headersBuilder;
    }

    private String encode(String name, String def){
        try {
            return null!=name&&name.length()>0? URLEncoder.encode(name, "UTF-8"):def;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return def;
    }
}
