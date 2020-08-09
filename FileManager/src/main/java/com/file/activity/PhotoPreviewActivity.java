package com.file.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.file.model.PhotoPreViewModel;
import com.merlin.api.Label;
import com.merlin.file.R;
import com.merlin.model.ModelActivity;

import java.util.ArrayList;

public class PhotoPreviewActivity extends ModelActivity<PhotoPreViewModel> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setModelContentView(R.layout.activity_photo_preview);
    }

    public static boolean startWithPaths(Context context, ArrayList<String> data, int index, String debug){
            if(null!=context&&null!=data&&data.size()>0){
                Intent intent=new Intent(context,PhotoPreviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putStringArrayListExtra(Label.LABEL_DATA,data);
                intent.putExtra(Label.LABEL_POSITION,index);
                context.startActivity(intent);
                return true;
            }
            return false;
    }

//    public static boolean start(Context context,ArrayList<?extends Parcelable> data,int index,String debug){
//            if(null!=context&&null!=data&&data.size()>0){
//            Intent intent=new Intent(context,PhotoPreviewActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.putParcelableArrayListExtra(Label.LABEL_DATA,data);
//            intent.putExtra(Label.LABEL_POSITION,index);
//            context.startActivity(intent);
//            return true;
//            }
//            return false;
//    }
}