package com.merlin.bean;

public final class Love {
    private String name;
    private String content;

    public Love(){

    }
    public Love(String name,String content){
        this.name=name;
        this.content=content;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }
}
