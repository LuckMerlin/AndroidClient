package com.luckmerlin.core.collection;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class ChainMap<K,V> implements Map<K,V>,PublishMethods {
    private Map<K,V> mMap;

    public ChainMap(Map<K,V> map){
        mMap=map;
    }

    public final ChainMap set(Map<K,V> map){
        mMap=map;
        return this;
    }

    @Override
    public int size() {
        Map<K,V> map=mMap;
        return null!=map?map.size():-1;
    }

    @Override
    public boolean isEmpty() {
        Map<K,V> map=mMap;
        return null==map||map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        Map<K,V> map=null!=key?mMap:null;
        return null!=map&&map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        Map<K,V> map=mMap;
        return null!=map&&map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        Map<K,V> map=mMap;
        return null!=map?map.get(key):null;
    }

    @Override
    public V put(K key, V value) {
        Map<K,V> map=null!=key?mMap:null;
        return null!=map?map.put(key,value):null;
    }

    ChainMap chainPut(K key, V value){
        put(key,value);
        return this;
    }

    @Override
    public V remove(Object key) {
        Map<K,V> map=null!=key?mMap:null;
        return null!=map?map.remove(key):null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Map<K,V> map=null!=m?mMap:null;
        if (null!=map){
            map.putAll(m);
        }
    }

    @Override
    public void clear() {
        Map<K,V> map=mMap;
        if (null!=map){
            map.clear();
        }
    }

    @Override
    public Set<K> keySet() {
        Map<K,V> map=mMap;
        return null!=map?map.keySet():null;
    }

    @Override
    public Collection<V> values() {
        Map<K,V> map=mMap;
        return null!=map?map.values():null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Map<K,V> map=mMap;
        return null!=map?map.entrySet():null;
    }


}
