package com.merlin.browser;
import com.merlin.database.FileDao;
import com.merlin.debug.Debug;
import com.merlin.global.Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5Reader {
    private final Database mDatabase=new Database();

    public String load(File file){
        if (null!=file&&file.exists()&&file.canRead()&&file.isFile()){
            FileInputStream in = null;
            int len;
            try {
                MessageDigest  digest = MessageDigest.getInstance("MD5");
                in=null!=digest?new FileInputStream(file):null;
                if (null!=in){
                    int bufferSize=1024*1024;
                    long length=file.length();
                    byte buffer[] = new byte[length<=bufferSize?(int)length:bufferSize];
                    while ((len = in.read(buffer)) != -1) {
                        digest.update(buffer, 0, len);
                    }
                    String md5=new BigInteger(1, digest.digest()).toString(16);
                    com.merlin.database.File fileDao=new com.merlin.database.File();
                    fileDao.setCreateTime(System.currentTimeMillis());
                    fileDao.setMd5(md5);
                    fileDao.setPath(file.getAbsolutePath());
                    Debug.D(getClass(),""+md5);
                    FileDao dao=mDatabase.master().newSession().getFileDao();
//                    dao.deleteAll();
                    dao.insert(fileDao);
//                    dao.deleteAll();
                    Debug.D(getClass(),"A "+md5);
                    return md5;
                }
            } catch (Exception e) {
                Debug.E(getClass(),"Exception load file md5 "+e+" "+file.getAbsolutePath(),e);
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
