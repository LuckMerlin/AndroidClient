package com.merlin.view;

import android.widget.SeekBar;

public interface OnSeekBarProgressChange {
    void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser);
}
