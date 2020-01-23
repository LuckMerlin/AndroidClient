package com.merlin.model;

import android.content.Context;
import android.view.View;

public final class TitleModel extends BaseModel{
    private View mLeft,mCenter,mRight;

    public TitleModel(Context context){
        super(context);
    }

    public View getLeft(){
        return mLeft;
    }

    public View getRight(){
        return mLeft;
    }

    public View getCenter(){
        return mLeft;
    }

    public boolean setText(int textId,int position){

        return false;
    }

    public boolean setView(int layoutId,int position){

        return false;
    }

    public void setVm(BaseModel vm){

    }

    public BaseModel getVm(){
        return null;
    }


}
