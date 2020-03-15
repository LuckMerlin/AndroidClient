package com.merlin.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.merlin.debug.Debug;

import org.json.JSONException;
import org.json.JSONObject;

public final class JsonObject extends JSONObject {

    @NonNull
    @Override
    public JSONObject put(@NonNull String name, boolean value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
            Debug.E(getClass(),"Can't put boolean value into json.e="+e+" "+value,e);
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    @Override
    public JSONObject put(@NonNull String name, double value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
            Debug.E(getClass(),"Can't put double value into json.e="+e+" "+value,e);
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    @Override
    public JSONObject put(@NonNull String name, long value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
            Debug.E(getClass(),"Can't put long value into json.e="+e+" "+value,e);
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    @Override
    public JSONObject put(@NonNull String name, int value)  {
        try {
            super.put(name, value);
        } catch (JSONException e) {
            Debug.E(getClass(),"Can't put int value into json.e="+e+" "+value,e);
            e.printStackTrace();
        }
        return this;
    }

    @NonNull
    @Override
    public JsonObject put(@NonNull String name, @Nullable Object value) {
        try {
            super.put(name, value);
        } catch (JSONException e) {
            Debug.E(getClass(),"Can't put object value into json.e="+e+" "+value,e);
            e.printStackTrace();
        }
        return this;
    }
}
