package com.merlin.file;

import com.merlin.api.Label;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class FileSaveBuilder implements Label {

    public MultipartBody.Part createFilePart(File file,String toFolder, RequestBody body){
        Headers.Builder builder=null!=body&&null!=file?createFileHeadersBuilder(file,toFolder):null;
       return null!=builder?MultipartBody.Part.create(builder.build(),body):null;
    }

    public Headers.Builder createFileHeadersBuilder(File file,String toFolder){
        String name=file.getName();
        String folder=toFolder;
        name= null!=name?name:"";
        StringBuilder disposition = new StringBuilder("form-data; name=luckmerlin;filename=luckmerlin");
        Headers.Builder headersBuilder = new Headers.Builder().addUnsafeNonAscii(
                "Content-Disposition", disposition.toString());
        headersBuilder.add(LABEL_NAME,encode(name,""));
        headersBuilder.add(LABEL_PARENT,encode(folder,""));
        headersBuilder.add(LABEL_PATH_SEP,encode(File.separator,""));
        if (file.isDirectory()){
            headersBuilder.add(LABEL_FOLDER,LABEL_FOLDER);
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
