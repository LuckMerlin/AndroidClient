//package com.luckmerlin.databinding.view;
//
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.luckmerlin.databinding.Resources;
//
//import java.util.List;
//import java.util.WeakHashMap;
//
///**
// * @deprecated
// */
//public final class ViewValuer {
//    private static WeakHashMap<View,Object> mTagMaps;
//
//    public boolean set(View view, Object value){
//        if (null!=view&&null!=value){
//           return setViewImage(view,new Image(value,view instanceof ImageView?null:value));
//        }
//        return false;
//    }
//
//    final boolean setViewImage(View view,Image image){
//        if(null!=view&&null!=image){
//            Resources resources=new Resources();
//            android.content.res.Resources res=view.getResources();
//            Object background=image.getBackground();
//            if (null!=background){
//                Drawable backgroundDrawable=generateObjectDrawable(resources,res,background,null);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    view.setBackground(backgroundDrawable);
//                }else{
//                    view.setBackgroundDrawable(backgroundDrawable);
//                }
//            }
//            Object imageSrc=view instanceof ImageView?image.getSrc():null;
//            if (null!=imageSrc){
//                ((ImageView)view).setImageDrawable(generateObjectDrawable(resources,res,imageSrc,null));
//            }
//            return true;
//        }
//        return false;
//    }
//
//    final boolean setViewText(View view,Text text){
//        if (null!=view&&view instanceof TextView &&null!=text){
//            android.content.res.Resources res=view.getResources();
//            final Resources resources=new Resources();
//            CharSequence textValue=generateTextString(resources,res,text);
//            Integer color=text.getColor();
//            color=null!=color?resources.getColor(res,color,color):color;
//            ((TextView)view).setText(null!=textValue?textValue:"");
//            if (null!=color){
//                ((TextView)view).setTextColor(color);
//            }
//            if (view instanceof EditText) {//Set editText hint
//                Text hintText=text.getHint();
//                if (null!=hintText){
//                    CharSequence hintTextValue=generateTextString(resources,view.getResources(),hintText);
//                    Integer hintColor=hintText.getColor();
//                    hintColor=null!=hintColor?resources.getColor(res,hintColor,hintColor):hintColor;
//                    if (null!=hintColor) {
//                        ((EditText) view).setHintTextColor(hintColor);
//                    }
//                    ((EditText)view).setHint(null!=hintTextValue?hintTextValue:"");
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//
//    static boolean setViewTag(View view,Object tag,String debug){
//        if (null!=view) {
//            WeakHashMap<View, Object> weakHashMap = mTagMaps;
//            if (null != tag) {
//                weakHashMap = null != weakHashMap ? weakHashMap : (mTagMaps = new WeakHashMap<>(1));
//                weakHashMap.put(view, tag);
//            } else if (null != weakHashMap) {
//                weakHashMap.remove(view);
//            }
//            checkTagMapClean(debug);
//            return true;
//        }
//        return false;
//    }
//
//    static Object getViewTag(View view) {
//        WeakHashMap<View,Object> tagMaps=null!=view?mTagMaps:null;
//        return null!=tagMaps?tagMaps.get(view):null;
//    }
//
//    static boolean checkTagMapClean(String debug){
//        WeakHashMap<View,Object> weakHashMap=mTagMaps;
//        if (null!=weakHashMap&&weakHashMap.size()<=0){
//            mTagMaps=null;
//            return true;
//        }
//        return false;
//    }
//
//    private Drawable generateObjectDrawable(Resources resources,android.content.res.Resources res,Object image,Drawable def){
//        image = image instanceof String&&null!=resources? resources.getTextColor((String)image,null):image;
//        image= image instanceof Integer&&null!=resources&&null!=res?resources.getDrawable(res,(Integer)image,
//                resources.getColorDrawable(res,(Integer)image,new ColorDrawable((Integer)image))):image;
//        image=null!=image&&image instanceof Bitmap?new BitmapDrawable((Bitmap) image):image;
//        return null!=image&&image instanceof Drawable?((Drawable)image):def;
//    }
//
//    private CharSequence generateTextString(Resources resources,android.content.res.Resources res,Text text){
//        List<Object> objects=null!=text?text.getObjects():null;
//        if (null!=objects&&objects.size()>0){
//            String textValue="";
//            for (Object obj:objects) {
//                if (null!=(obj=null!=obj&&obj instanceof Integer&&null!=res?(null!=resources?resources:
//                        (resources=new Resources())).getText(res, (Integer)obj,Integer.toString((Integer) obj)):obj)){
//                    textValue+=(obj instanceof CharSequence?(CharSequence)obj:obj.toString());
//                }
//            }
//            return textValue;
//        }
//        return null;
//    }
//
//}
