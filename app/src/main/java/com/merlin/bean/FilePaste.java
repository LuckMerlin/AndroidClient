package com.merlin.bean;

import androidx.annotation.NonNull;

import com.merlin.api.Label;

import org.json.JSONException;
import org.json.JSONObject;

public final class FilePaste {
    private String from;
    private String to;
    private int mode;

    public FilePaste(String from, String to, int mode){
        this.from=from;
        this.to=to;
        this.mode=mode;
    }

    public int getMode() {
        return mode;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @NonNull
    @Override
    public String toString() {
        JSONObject object=new JSONObject();
        try {
            object.putOpt(Label.LABEL_FROM,null!=from?from:"");
            object.putOpt(Label.LABEL_TO,null!=to?to:"");
            object.putOpt(Label.LABEL_MODE,mode);
        } catch (JSONException e) {
        }
        return object.toString();
    }
}
