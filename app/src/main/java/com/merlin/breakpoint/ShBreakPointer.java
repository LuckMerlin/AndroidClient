package com.merlin.breakpoint;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.merlin.debug.Debug;
import com.merlin.task.DownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShBreakPointer implements BreakPointer {
    private final static String LABEL_BREAKPOINT="breakpoint3";
    private final Context mContext;

    public ShBreakPointer(Context context){
        mContext=context;
    }

    @Override
    public boolean addBreakpoint(BreakPoint breakpoint) {
        DownloadTask task=null!=breakpoint?breakpoint.getTask():null;
        long total=null!=task?task.getTotal():0;
        String path=null!=task?task.getTargetPath():null;
        long length=total>0&&null!=path?new File(path).length():0;
        long progress=null!=task?task.getProgress():-1;
        if (progress<length){
            String text=new Gson().toJson(breakpoint);
            if (null==text||text.length()<=0){
                Debug.W(getClass(),"Can't add download breakpoint.text="+text+" "+path);
                return false;
            }
            SharedPreferences sh=mContext.getSharedPreferences(LABEL_BREAKPOINT,Context.MODE_PRIVATE);
            boolean succeed=sh.edit().putString(path,text).commit();
            Debug.D(getClass(),"Add download breakpoint."+succeed+" "+progress+" "+length+" "+path);
            return true;
        }
        Debug.W(getClass(),"Can't add download breakpoint.length="+length+" progress="+progress+" "+path);
        return false;
    }

    @Override
    public List<BreakPoint> getBreakpoints() {
        SharedPreferences sh=mContext.getSharedPreferences(LABEL_BREAKPOINT,Context.MODE_PRIVATE);
        Map<String,?> map=null!=sh?sh.getAll():null;
        Set<String> set=null!=map?map.keySet():null;
        if (null!=set){
            List<BreakPoint> list=new ArrayList<>();
            for (String path:set) {
                String value=null!=path?sh.getString(path,null):null;
                BreakPoint point=null!=value&&value.length()>0?new Gson().fromJson(value,BreakPoint.class):null;
                if (null!=point){
                    list.add(point);
                }
            }
            return list;
        }
        return null;
    }

    @Override
    public boolean removeBreakpoint(Object obj) {
        if (null!=obj){
            obj=null!=obj&&obj instanceof BreakPoint?((BreakPoint)obj).getTask():null;
            String path=null!=obj&&obj instanceof DownloadTask?((DownloadTask)obj).getTargetPath():null;
            if (null!=path) {
                SharedPreferences sh = mContext.getSharedPreferences(LABEL_BREAKPOINT, Context.MODE_PRIVATE);
                sh.edit().remove(path).commit();
                Debug.D(getClass(),"Remove download breakpoint."+path);
                return true;
            }
        }
        return false;
    }
}
