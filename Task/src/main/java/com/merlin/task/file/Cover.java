package com.merlin.task.file;

public class Cover {
   public final static int COVER_NONE=-1700;
   public final static int COVER_REPLACE=-1703;
   public final static int COVER_SKIP=-1704;

    public static int tapClickCountToMode(int clickCount){
        switch (clickCount){
            case 2:return Cover.COVER_SKIP;
            case 4: return Cover.COVER_REPLACE;
            case 1://Get through
            default:
                return Cover.COVER_NONE;
        }
    }

}
