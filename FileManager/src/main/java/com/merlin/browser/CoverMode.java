package com.merlin.browser;

/**
 * @deprecated
 */
public interface CoverMode {
    public final static int NONE=0x00; //0000 0000
    public final static int REPLACE=0x01;//0000 0001
    public final static int KEEP=0x02;//0000 0010
    public final static int SKIP=0x04;//0000 0100
    public final static int POSTFIX=0x08;//0000 1000

    static int tapClickCountToMode(int clickCount){
        switch (clickCount){
            case 2:return CoverMode.KEEP;
            case 3:return CoverMode.SKIP;
            case 4: return CoverMode.REPLACE;
            case 1://Get through
            default:
                return CoverMode.NONE;
        }
    }
}
