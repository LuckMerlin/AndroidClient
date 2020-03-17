package com.merlin.socket;

import com.google.gson.reflect.TypeToken;
import com.merlin.api.Reply;
import com.merlin.api.What;
import com.merlin.bean.NasFile;
import com.merlin.debug.Debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Arrays;

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
//            response.getData(new TypeToken<Reply<NasFile>>(){}.getType(),null);
//            Object nasFile=null!=response?response.getData<NasFile>(Reply,null):null;
            Debug.D(getClass(),"AAAAAAAAAAAAAAAAAAA "+response.getDataReply(NasFile.class,null));
            long length=-1;//null!=nasFile?nasFile.getLength():-1;
            if (length<=0){ //If empty or not exist,Just continue to upload it
//                try {
//                    mFileReader=new FileReader(mFile);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                FileReader fileReader=mFileReader;
//                final Frame serverFrame=mFrame;
//                long fileLength=fileReader.getSize();
//                long offset=(long)frame.getPosition();
//                byte[] bytes=null!=fileReader?fileReader.read(offset, mBuffer):null;
//                int bytesLength=null!=bytes?bytes.length:0;
//                if (bytesLength>0){
//                    Frame responseFrame=new Frame(fileLength,offset+bytesLength,serverFrame.getTo(),
//                            serverFrame.getUnique(),null,serverFrame.getData(),mBuffer,null,null,null);
//                     Debug.D(getClass(),"读取到 "+offset+" "+bytesLength);
//                     onFrameSend(responseFrame,null);
//                 }
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

        private byte[] read(long offset,byte[] buffer){
            final int bufferSize=null!=buffer?buffer.length:-1;
            if (bufferSize<=0||offset<0){
                Debug.W(getClass(),"Can't read file bytes "+offset+" "+ bufferSize);
                return null;
            }
            FileChannel channel=getChannel();
            if (null==channel||!channel.isOpen()){
                Debug.W(getClass(),"Can't read file bytes which if close?"+channel);
                return null;
            }
            long length=getSize();
            if (length<=offset){
                Debug.D(getClass(),"读完啦");
                return null;
            }else{
                try {
                  seek(offset);
                  int readCount= read(buffer);
                  return readCount>0?Arrays.copyOfRange(buffer,0,readCount):null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public boolean isOpen(){
            FileChannel channel=getChannel();
            return null!=channel&&channel.isOpen();
        }

    }
}
