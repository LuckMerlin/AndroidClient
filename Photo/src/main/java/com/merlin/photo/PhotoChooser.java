package com.merlin.photo;

public final class PhotoChooser {
    private final Display mDisplay;

    public PhotoChooser(Display display){
        mDisplay=display;
    }

    public PhotoChooser setAdapter(DefaultPhotoAdapter adapter){

        return this;
    }

    public final boolean choose(){

        return false;
    }
}
