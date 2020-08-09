package com.merlin.mvvm;

import android.app.Activity;
import android.content.Intent;

public interface OnActivityResult {
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
}
