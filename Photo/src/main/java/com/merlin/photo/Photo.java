package com.merlin.photo;

public final class Photo {
    private final String mTitle;
    private final String mPath;
    private final String mNote;
    private final String mMimeType;
    private final int mWidth;
    private final int mHeight;

    public Photo(String path, String title, String mimeType, int width, int height, String note){
        mTitle=title;
        mMimeType=mimeType;
        mWidth=width;
        mHeight=height;
        mPath=path;
        mNote=note;
    }

    public String getPath() {
        return mPath;
    }

    @Override
    public boolean equals( Object obj) {
        if (null!=obj&&obj instanceof Photo){
            String path=((Photo)obj).mPath;
            String currPath=mPath;
            return (null==path&&null==currPath)||(null!=path&&null!=currPath&&path.equals(currPath));
        }
        return super.equals(obj);
    }
}
