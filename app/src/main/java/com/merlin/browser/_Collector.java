package com.merlin.browser;

import com.merlin.bean.Document;

import java.util.ArrayList;
import java.util.List;

public class _Collector<T extends Document> {
    private ArrayList<T> mFiles;

    public _Collector(T data){
       add(data);
    }

    public final boolean add(T data){
       if (null!=data){
          List<T> files=mFiles;
          files=null!=files?files:(mFiles=new ArrayList<>(1));
          return !files.contains(data)&&files.add(data);
       }
       return false;
    }

    public final ArrayList<T> getFiles() {
        return mFiles;
    }
}
