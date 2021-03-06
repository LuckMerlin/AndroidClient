package com.merlin.breakpoint;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.merlin.debug.Debug;
import com.merlin.task.Transport;

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
        Transport task=null!=breakpoint?breakpoint.getTask():null;
        String path=null!=task?task.getTargetPath():null;
        if (null!=path&&path.length()>0){
            String text=new Gson().toJson(breakpoint);
            if (null==text||text.length()<=0){
                Debug.W(getClass(),"Can't add download breakpoint.title_text="+text+" "+path);
                return false;
            }
            SharedPreferences sh=mContext.getSharedPreferences(LABEL_BREAKPOINT,Context.MODE_PRIVATE);
            boolean succeed=sh.edit().putString(path,text).commit();
            Debug.D(getClass(),"Add download breakpoint."+succeed+" "+task.getProgress()+" "+path);
            return true;
        }
        Debug.W(getClass(),"Can't add download breakpoint. "+path);
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
            obj=obj instanceof BreakPoint?((BreakPoint)obj).getTask():obj;
            String path=null!=obj&&obj instanceof Transport ?((Transport)obj).getTargetPath():null;
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
