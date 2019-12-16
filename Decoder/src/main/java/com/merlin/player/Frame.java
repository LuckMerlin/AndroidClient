package com.merlin.player;

public class Frame {
   public final static int CHANNEEL_MODE_SINGLE_CHANNEL = 0;/* single channel */
   public final static int CHANNEEL_MODE_DUAL_CHANNEL = 1;/* dual channel */
   public final static int CHANNEEL_MODE_JOINT_STEREO = 2;/* joint (MS/intensity) stereo */
   public final static int CHANNEEL_MODE_STEREO	= 3;/* normal LR stereo */
   private int mLayer;
   private int mChannelMode;
}
