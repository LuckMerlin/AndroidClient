package com.merlin.transport;


import java.util.List;

public final class Conveyor {
    public final static int MODE_REMOVE=123145;
    public final static int MODE_ADDED=123146;
    private List<Convey> mConveying;

    public interface Callback{

    }

    public boolean convey(int mode, Convey convey,Callback ...callbacks){
        return null!=convey&&convey(mode,new Convey[]{convey},callbacks);
    }

    public boolean convey(int mode, Convey[] conveys,Callback ...callbacks){
        if (null!=conveys&&conveys.length>0){
            switch (mode){
                case MODE_ADDED:
                    return add(conveys,callbacks);
                case MODE_REMOVE:
                    return remove(conveys);
            }
        }
        return false;
    }

    public boolean add(Convey[] conveys,Callback ...callbacks){

        return false;
    }

    public boolean remove(Convey[] conveys){

        return false;
    }

    private static class Classs{

    }

}
