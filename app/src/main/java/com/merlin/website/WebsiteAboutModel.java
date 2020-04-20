package com.merlin.website;

import android.view.View;

import com.merlin.model.Model;
import com.merlin.view.OnTapClick;

public class WebsiteAboutModel extends Model implements OnTapClick {

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data) {
        return false;
    }
}
