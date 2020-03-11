package com.merlin.util;

public class StringEquals {

    public boolean equals(String a,String b){
        return (null==a&&null==b)||(null!=a&&null!=b&&a.equals(b));
    }
}
