package com.merlin.player;

public final class Time {

    public  static String formatTime(long time){
        return formatTime(time,true);
    }

    public  static String formatTime(long time,boolean noneHour){
        int hours = time<=0?0:(int)time / 3600000;
        long less=time-hours*3600000;
        int minutes = less<=0?0:(int)less/60000;
        less=less-minutes*60000;
        return (noneHour?"":(String.format("%02d",hours)+":"))+String.format("%02d",minutes)+":"+String.format("%02d",less<=0?0:less/1000);
    }

}
