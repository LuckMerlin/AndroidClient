package com.file.model;
import com.merlin.adapter.ChooseFileAdapter;
import com.merlin.lib.Canceler;

public class MediaChooseModel extends BaseModel {
    private final ChooseFileAdapter mAdapter=new ChooseFileAdapter();
    private Canceler mCanceler;

    public boolean loadMedias(String uri,String ...mimes){

        return false;
    }

    public ChooseFileAdapter getAdapter() {
        return mAdapter;
    }
}
