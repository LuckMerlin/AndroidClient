package com.merlin.socket;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.merlin.api.Label;
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
    private final Frame mFrame;
    private FileReader mFileReader;
    private final byte[] mBuffer=new byte[1024*1024];
    private final OnResponse mCallback;
    private Canceler mCanceler;

    protected FileUploader(File file,Frame frame,OnResponse callback){
        mFrame=frame;
        mCallback=callback;
        try {
            mFileReader=new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected abstract Canceler onFrameSend(Frame frame,String debug);

    @Override
    public Integer onResponse(int what, String note, Frame frame, Frame response, Object arg) {
        Debug.D(getClass(),"回应 "+what+" "+note);
        if (what == What.WHAT_SUCCEED){
            final Frame serverFrame=mFrame;
            if (null!=serverFrame){
                Reply reply=response.getDataReply();
                Object object=null!=reply?reply.getData():null;
                long currentPosition=((long)(null!=object&&object instanceof Double?(Double) object:0));
                Debug.D(getClass(),"AAA "+currentPosition);
                FileReader reader=mFileReader;
                byte[] buffer=null!=reader?reader.read(currentPosition,mBuffer):null;
                int readCount=null!=buffer?buffer.length:0;
                Frame bytesFrame=readCount>0?new Frame(reader.getSize(),currentPosition+readCount,serverFrame.getTo(),
                        serverFrame.getUnique(),null,serverFrame.getData(),buffer,null,null,null):null;
                if (null!=bytesFrame&&null!=(mCanceler=onFrameSend(bytesFrame,null))){
                    return NEXT_FRAME;
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
