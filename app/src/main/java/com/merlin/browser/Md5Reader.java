package com.merlin.browser;

import com.merlin.util.Closer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5Reader {

    public String load(File file){
        if (null!=file&&file.exists()&&file.canRead()&&file.isFile()){
            FileInputStream in = null;
            int len;
            try {
                MessageDigest  digest = MessageDigest.getInstance("MD5");
                in=null!=digest?new FileInputStream(file):null;
                if (null!=in){
                    byte buffer[] = new byte[1024*1024];
                    while ((len = in.read(buffer)) != -1) {
                        digest.update(buffer, 0, len);
                    }
                    return new BigInteger(1, digest.digest()).toString(16);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }finally {
               if (null!=in){
                   try {
                       in.close();
                   } catch (IOException e) {
                       //Do nothing
                   }
               }
            }
        }
        return null;
    }
}
