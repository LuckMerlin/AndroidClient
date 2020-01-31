package com.merlin.util;

public class Time {

    public static String formatMediaDuration(long mills){
            long hours=mills/(1000*60*60);
            long wholeHours=hours*(1000 * 60 * 60 );
            long minutes = (mills-wholeHours)/(1000* 60);
            long seconds= (mills-wholeHours-(minutes*1000*60))/1000;
            return String.format("%02d", hours)+":"+ String.format("%02d", minutes)+":"+
                    String.format("%02d", seconds);
    }
}
