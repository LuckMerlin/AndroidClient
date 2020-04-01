package com.merlin.api;

public enum CoverMode {
    NONE(What.WHAT_INVALID),REPLACE(What.WHAT_REPLACE),KEEP(What.WHAT_KEEP),SKIP(What.WHAT_SKIP);
    private int mWhat;
    CoverMode(int what){
        this.mWhat=what;
    }

    public int what() {
        return mWhat;
    }

    public static CoverMode tapClickCountToMode(int clickCount){
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
