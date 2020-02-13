package com.merlin.file;

import com.merlin.binding.MBinding;
import com.merlin.debug.Debug;

public final class Permissions {
    private static final int FILE_TYPE_MASK = 0170000 ;//文件类型屏蔽字
    private static final int FILE_TYPE_IS_DIRECTORY = 0040000 ;//目录文件
    private static final int FILE_TYPE_IS_NORMAL_FILE = 0100000 ;//普通文件
    private static final int FILE_TYPE_IS_PIPE_OR_FIFO = 0010000 ;//管道或FIFO
    private static final int FILE_TYPE_CHAR = 0020000 ;//字符特殊文件
    private static final int FILE_TYPE_BLOCK= 0060000 ;//块特殊文件
    private static final int FILE_TYPE_LINK= 0120000 ;//链接文件
    private static final int FILE_TYPE_SOCKET= 0140000 ;//套接字文件
    private static final int FILE_PERMISSION_USER_EXECUTABLE= 0100 ;//用户可执行
    private static final int FILE_PERMISSION_USER_WRITEABLE= 0200 ;//用户可写
    private static final int FILE_PERMISSION_USER_READABLE= 0400 ;//用户可读
    private static final int FILE_PERMISSION_OTHER_EXECUTABLE= 00001 ;//其他用户可执行
    private static final int FILE_PERMISSION_OTHER_WRITEABLE= 00002 ;//其他用户可写
    private static final int FILE_PERMISSION_OTHER_READABLE= 00004 ;//其他用户可读


    public final boolean isDirectory(int permissions){
        return (permissions&FILE_TYPE_MASK) ==FILE_TYPE_IS_DIRECTORY;
    }

    public final boolean isNormalFile(int permissions){
        return (permissions&FILE_TYPE_MASK) ==FILE_TYPE_IS_NORMAL_FILE;
    }

    public final boolean isPipeOrFifoFile(int permissions){
        return (permissions&FILE_TYPE_MASK) == FILE_TYPE_IS_PIPE_OR_FIFO;
    }

    public final boolean isCharSequenceFile(int permissions){
        return (permissions&FILE_TYPE_MASK) ==FILE_TYPE_CHAR;
    }

    public final boolean isBlockFile(int permissions){
        return (permissions&FILE_TYPE_MASK) ==FILE_TYPE_BLOCK;
    }

    public final boolean isLinkFile(int permissions){
        return (permissions&FILE_TYPE_MASK) ==FILE_TYPE_LINK;
    }

    public final boolean isSocketFile(int permissions){
        return (permissions&FILE_TYPE_MASK) ==FILE_TYPE_SOCKET;
    }

    public final boolean isOtherExecutable(int permissions){
        return (permissions & FILE_PERMISSION_OTHER_EXECUTABLE) >0;
    }

    public final static boolean canDelete(int permissions){
        Permissions per=new Permissions();
//        Debug.D(MBinding.class,"&&&&& "+per.isOtherWriteable(permissions)+" "+per.isOtherReadable(permissions));
        return per.isOtherWriteable(permissions)&&per.isOtherReadable(permissions);
    }

    public final boolean isOtherReadable(int permissions){
//        String oct=Integer.toOctalString(permi);
//        int length=null!=oct?oct.length():-1;
//        oct=length>=3?oct.substring(length-3,length):null;
        return (permissions & FILE_PERMISSION_OTHER_READABLE) >0;
    }

    public final boolean isOtherWriteable(int permissions){
        return (permissions & FILE_PERMISSION_OTHER_WRITEABLE) >0;
    }

}
