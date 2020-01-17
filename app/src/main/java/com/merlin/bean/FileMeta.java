package com.merlin.bean;

import java.util.List;

public class FileMeta {
    private String file;
    private double lastModifyTime;
    private String name;
    private boolean directory;
    private boolean read;
    private boolean write;
    private String extension;
    private int length;
    private int childCount;
    private List<FileMeta> childs;

}
