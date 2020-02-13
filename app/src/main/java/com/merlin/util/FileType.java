package com.merlin.util;

import com.merlin.client.R;
import com.merlin.debug.Debug;

public class FileType {
    public final static int MASK_DIRECTORY=0040000;
    public final static int MASK_CHARACTER_DEVICE=0020000;
    public final static int MASK_BLOCK_DEVICE=0060000;
    public final static int MASK_REGULAR_FILE=0100000;
    public final static int MASK_PIPE=0010000;
    public final static int MASK_LINK=0120000;
    public final static int MASK_SOCKET=0140000;
    private final static int MODE_MASK= 0170000;

    public static boolean isType(int mode,int mask){
        return (MODE_MASK&mode)==mask;
    }

    public static Object formatType(int mode){
        Debug.D(FileType.class,"AAA  "+mode);
        switch (mode&MODE_MASK){
            case MASK_DIRECTORY:
                return R.string.folder;
            case MASK_SOCKET:
                return R.string.socket;
            case MASK_CHARACTER_DEVICE:
                return R.string.characterDevice;
            case MASK_BLOCK_DEVICE:
                return R.string.blockDevice;
            case MASK_REGULAR_FILE:
                return R.string.file;
            case MASK_PIPE:
                return R.string.pipe;
            case MASK_LINK:
                return R.string.link;
        }
        return null;
    }
}
