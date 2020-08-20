package com.file.model;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.ObservableField;

import com.merlin.debug.Debug;
import com.merlin.file.R;
import com.merlin.model.Model;

public class PhotoPreViewModel extends BaseModel implements Model.OnActivityIntentChange {
    private final ObservableField<Object> mImage=new ObservableField<>();


    @Override
    public void onActivityIntentChanged(Activity activity, Intent intent) {
        Debug.D(getClass(),"SSSSSSSSSSSSSS "+intent);
        mImage.set(R.drawable.hidisk_icon_7z);
    }


    public ObservableField<Object> getImage() {
        return mImage;
    }
}
