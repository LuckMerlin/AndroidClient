package com.luckmerlin.core.collection;

import android.os.Build;

import com.luckmerlin.core.proguard.PublishMethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class ChainCollection<T> implements Collection<T>, PublishMethods {
    private final Collection<T> mCollections;

    public ChainCollection(){
        this(1);
    }

    public ChainCollection(int initialCapacity){
        this(new ArrayList<T>(initialCapacity<=0?0:initialCapacity));
    }

    public ChainCollection(Collection<T> collections){
        mCollections=collections;
    }

    public Collection<T> getCollections() {
        return mCollections;
    }

    @Override
    public int size() {
        Collection<T> collection=mCollections;
        return null!=collection?collection.size():-1;
    }

    @Override
    public boolean isEmpty() {
        Collection<T> collection=mCollections;
        return null==collection||collection.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        Collection<T> collection=null!=o?mCollections:null;
        return null!=collection&&collection.contains(o);
    }

    @Override
    public Iterator iterator() {
        Collection<T> collection=mCollections;
        return null!=collection?collection.iterator():null;
    }

    @Override
    public void forEach(Consumer action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collection<T> collection=mCollections;
            if (null!=collection){
                collection.forEach(action);
            }
        }
    }

    @Override
    public Object[] toArray() {
        Collection<T> collection=mCollections;
        return null!=collection?collection.toArray():null;
    }

    @Override
    public boolean add(T o) {
        Collection<T> collection=null!=o?mCollections:null;
        return null!=collection&&null!=o&&collection.add(o);
    }

    public ChainCollection<T> addInChain(T o){
        add(o);
        return this;
    }

    @Override
    public boolean remove(Object o) {
        Collection<T> collection=null!=o?mCollections:null;
        return null!=collection&&collection.remove(o);
    }

    public T index(Object object){
        Collection<T> collection=null!=object?mCollections:null;
        if (null!=collection){
            for (T child:collection){
                if (null!=child&&child.equals(object)){
                    return child;
                }
            }
        }
        return null;
    }

    public ChainCollection<T> removeInChain(Object o, boolean equals) {
        if (null!=o&&!remove(o)&&equals){
            T child=index(o);
            if (null!=child){
                remove(child);
            }
        }
        return this;
    }

    @Override
    public boolean addAll(Collection c) {
        Collection<T> collection=null!=c?mCollections:null;
        return null!=collection&&collection.addAll(c);
    }

    public ChainCollection<T> addAllInChain(Collection c) {
        if (null!=c){
            addAll(c);
        }
        return this;
    }

    @Override
    public boolean removeIf(Predicate filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collection<T> collection=null!=filter?mCollections:null;
            return collection.removeIf(filter);
        }
        return false;
    }

    public ChainCollection<T> removeIfInChain(Predicate filter) {
        if (null!=filter){
            removeIf(filter);
        }
        return this;
    }

    @Override
    public void clear() {
        Collection<T> collection=mCollections;
        if (null!=collection){
            collection.clear();
        }
    }

    public ChainCollection<T> clearInChain() {
        clear();
        return this;
    }


    @Override
    public boolean equals(Object o) {
        Collection<T> collection=mCollections;
        return null!=o&&(o==this||(null!=collection&&collection.equals(o)));
    }

    @Override
    public int hashCode() {
        Collection<T> collection=mCollections;
        return null!=collection?collection.hashCode():0;
    }

    @Override
    public Spliterator spliterator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collection<T> collection=mCollections;
            return null!=collection?collection.spliterator():null;
        }
        return null;
    }

    @Override
    public Stream stream() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collection<T> collection=mCollections;
            return null!=collection?collection.stream():null;
        }
        return null;
    }

    @Override
    public Stream parallelStream() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collection<T> collection=mCollections;
            return null!=collection?collection.parallelStream():null;
        }
        return null;
    }

    @Override
    public boolean retainAll(Collection c) {
        Collection<T> collection=mCollections;
        return null!=collection&&collection.retainAll(c);
    }

    public ChainCollection<T> retainAllInChain(Collection c) {
        retainAll(c);
        return this;
    }

    @Override
    public boolean removeAll(Collection c) {
        Collection<T> collection=mCollections;
        return null!=collection&&collection.removeAll(c);
    }

    public ChainCollection<T> removeAllInChain(Collection c) {
        removeAll(c);
        return this;
    }

    @Override
    public boolean containsAll(Collection c) {
        Collection<T> collection=mCollections;
        return null!=collection&&collection.containsAll(c);
    }

    @Override
    public Object[] toArray(Object[] a) {
        Collection<T> collection=mCollections;
        return null!=collection?collection.toArray(a):null;
    }
}
