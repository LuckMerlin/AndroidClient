package com.merlin.browser;
import com.merlin.database.FileDB;
import com.merlin.database.FileDBDao;
import com.merlin.debug.Debug;
import com.merlin.global.Database;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5Reader {
    private final FileDBDao mFileDao;

    public Md5Reader( ){
        mFileDao=new Database().master().newSession().getFileDBDao();
    }

    public FileDB load(File file, boolean force){
        final FileDBDao fileDao=mFileDao;
        final String path=null!=file&&file.exists()&&file.canRead()&&file.isFile()&&file.length()>0&&null!=fileDao?
                file.getAbsolutePath():null;
        if (null!=path&&path.length()>0){
            FileDB fileData=force?null:fileDao.queryBuilder().where(FileDBDao.Properties.Path.eq(path)).unique();
            if (null==fileData){
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
                        if (null!=md5&&md5.length()>0){
                            fileData=new FileDB();
                            fileData.setCreateTime(System.currentTimeMillis());
                            fileData.setMd5(md5);
                            fileData.setPath(path);
                            fileDao.insertOrReplace(fileData);
                        }
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
            return fileData;
        }
        return null;
    }
}
