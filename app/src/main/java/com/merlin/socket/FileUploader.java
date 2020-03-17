package com.merlin.socket;

import com.merlin.api.What;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public abstract class FileUploader implements OnResponse{
    private final File mFile;
    private final Frame mFrame;
    private FileReader mFileReader;
    private final byte[] mBuffer=new byte[1024*1024];

    protected FileUploader(File file,Frame frame){
        mFile=file;
        mFrame=frame;
    }

    protected abstract Canceler onFrameSend(Frame frame,String debug);

    @Override
    public Integer onResponse(int what, String note, Frame frame, Frame response, Object arg) {
        if (what == What.WHAT_SUCCEED){
            NasFile nasFile=null!=response?response.getData(NasFile.class,null):null;
            long length=null!=nasFile?nasFile.getLength():-1;
            if (length<=0){ //If empty or not exist,Just continue to upload it
                try {
                    mFileReader=new FileReader(mFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                FileReader fileReader=mFileReader;
                final Frame serverFrame=mFrame;
                long fileLength=fileReader.getSize();
                long offset=0;
                 int readCount=null!=fileReader?fileReader.read(offset, mBuffer):-1;
                 if (readCount>0){
//                     long length,long position,String to,String format,String unique,String key,byte[] body,String version,String access,String encoding
//                     Debug.D(getClass(),"读取到 "+readCount);
//                     onFrameSend(new Frame(fileLength,offset+readCount,serverFrame.getTo(),Frame.FORMAT_BYTES,
//                             serverFrame.getUnique(),null,mBuffer,null,null,null),null);
                 }
            }
        }
        return null;
    }

    private static class FileReader extends RandomAccessFile{
        protected FileReader(File file) throws FileNotFoundException {
            super(file,"r");
        }

        public long getSize(){
            try {
                return length();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        private int read(long offset,byte[] buffer){
            final int bufferSize=null!=buffer?buffer.length:-1;
            if (bufferSize<=0||offset<0){
                Debug.W(getClass(),"Can't read file bytes "+offset+" "+ bufferSize);
                return -1;
            }
            FileChannel channel=getChannel();
            if (null==channel||!channel.isOpen()){
                Debug.W(getClass(),"Can't read file bytes which if close?"+channel);
                return -1;
            }
            long length=getSize();
            if (length<=offset){
                Debug.D(getClass(),"读完啦");
                return -1;
            }else{
                try {
                    seek(offset);
                    return read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return -1;
        }

        public boolean isOpen(){
            FileChannel channel=getChannel();
            return null!=channel&&channel.isOpen();
        }

    }
}
