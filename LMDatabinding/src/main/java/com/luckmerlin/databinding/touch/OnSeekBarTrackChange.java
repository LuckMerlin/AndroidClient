package com.luckmerlin.databinding.touch;

import android.widget.SeekBar;

import com.luckmerlin.core.proguard.PublishMethods;

public class OnSeekBarTrackChange implements SeekBar.OnSeekBarChangeListener, PublishMethods {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //Do nothing
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //Do nothing
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //Do nothing
    }
}
