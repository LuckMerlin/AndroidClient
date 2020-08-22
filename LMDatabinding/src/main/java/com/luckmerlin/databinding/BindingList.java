package com.luckmerlin.databinding;
import java.util.ArrayList;

public final class BindingList extends ArrayList<BindingObject>  implements BindingObject {

    public BindingList append(boolean skipExist,BindingObject ...objects){
        if (null!=objects&&objects.length>0){
            for (BindingObject child:objects) {
                if (null!=child&&(!contains(child)||!skipExist)){
                    add(child);
                }
            }
        }
        return this;
    }

}
