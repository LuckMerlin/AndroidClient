package com.luckmerlin.databinding;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ColorStateListDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.luckmerlin.databinding.text.OnEditActionChange;
import com.luckmerlin.databinding.text.OnEditActionChangeListener;
import com.luckmerlin.databinding.text.OnEditTextChange;
import com.luckmerlin.databinding.text.OnEditTextChangeListener;
import com.luckmerlin.databinding.touch.OnViewClick;
import com.luckmerlin.databinding.touch.OnViewLongClick;
import com.luckmerlin.databinding.touch.OnViewTouch;
import com.luckmerlin.databinding.view.Image;
import com.luckmerlin.databinding.view.Text;
import com.luckmerlin.databinding.view.Touch;

import java.lang.ref.WeakReference;
import java.util.List;

final class ViewBinding {

    boolean bind(View view, BindingObject ...bindings){
        if (null==view||null==bindings||bindings.length<=0){
            return false;
        }
        for (BindingObject binding:bindings) {
            if (binding instanceof BindingList){
                BindingList list=(BindingList)binding;
                if (null!=list&&list.size()>0){
                    for (BindingObject child:list) {
                        bind(view,child);
                    }
                }
                continue;
            }
            if (view instanceof TextView){
                TextView textView=(TextView)view;
                if (binding instanceof Text){
                    applyViewText(textView,(Text)binding);
                }
                if (binding instanceof OnEditActionChange) {
                    textView.setOnEditorActionListener(new OnEditActionChangeListener((OnEditActionChange)binding));
                }
                if (binding instanceof OnEditTextChange){
                    textView.addTextChangedListener(new OnEditTextChangeListener((OnEditTextChange) binding));
                }
            }
            if (binding instanceof Image){
                applyViewImage(view,(Image)binding);
            }
            if (binding instanceof Touch){
                applyViewTouch(view,(Touch)binding);
            }
        }
        return true;
    }

    private boolean applyViewTouch(View view,Touch touch){
        if (null==view||null==touch){
            return false;
        }
        Object object=touch.getObject();
        if (null==object||(object instanceof Boolean&&!(Boolean)object)){
            return false;
        }
        Object tagObject=touch.getTag();
        if (null!=tagObject){
            if (tagObject instanceof Activity||tagObject instanceof Service|| tagObject instanceof BroadcastReceiver||tagObject instanceof ContentProvider){
                tagObject=new WeakReference<>(tagObject);
            }
        }
        final Object finalTagObject=tagObject;
        if (object instanceof OnViewTouch){
            view.setOnTouchListener((View v, MotionEvent event)->((OnViewTouch)object).onViewTouched(view,event,getTagObject(finalTagObject)));
        }
        if (object instanceof OnViewClick){
            int dither=touch.getClickDither();
            final int finalDither=dither<0?400:dither;
            final Object[] ditherObject=new Object[2];
            final View.OnClickListener clickListener=(View v)->{
                Object clickCount=ditherObject[1];
                ((OnViewClick)object).onViewClick(view, null!=clickCount&&clickCount instanceof Integer
                        &&((Integer)clickCount)>0?(Integer) clickCount:0, getTagObject(finalTagObject));
            };
            view.setOnClickListener((View v)-> {
                if (finalDither>0){
                   Object dithering=ditherObject[0];
                   Object count=ditherObject[1];
                   Runnable runnable=null;
                   if (null!=dithering&&dithering instanceof Runnable){
                       view.removeCallbacks(runnable=(Runnable)dithering);
                   }else{
                       ditherObject[0]=runnable=()->{
                           clickListener.onClick(v);
                           ditherObject[0]=ditherObject[1]=null;
                       };
                   }
                   ditherObject[1]=null!=count&&count instanceof Integer?(Integer)count+1:1;
                   v.postDelayed(runnable,finalDither);
                   return;
                }else{
                    clickListener.onClick(v);
                }
            });
        }
        if (object instanceof OnViewLongClick){
            view.setOnLongClickListener((View v)->((OnViewLongClick)object).onViewLongClick(v,getTagObject(finalTagObject)));
        }
        return true;
    }

    private Object getTagObject(Object object){
        while (null!=object&&object instanceof WeakReference){
            object=((WeakReference)object).get();
        }
        return object;
    }

    private boolean applyViewImage(View view,Image image){
        if (null==view||null==image){
            return false;
        }
        final Context context=view.getContext();
        Resources resources=new Resources();
        android.content.res.Resources res=view.getResources();
        //Apply background
        Object background=image.getBackground();
        Drawable backgroundDrawable=generateObjectDrawable(context,resources,res,background,null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(backgroundDrawable);
        }else{
            view.setBackgroundDrawable(backgroundDrawable);
        }
        //Apply image src for image view
        if (view instanceof ImageView){
            ImageView imageView=(ImageView)view;
            Object imageObject=image.getSrc();
            Drawable imageDrawable=generateObjectDrawable(context,resources,res,imageObject,null);
            imageView.setImageDrawable(imageDrawable);
        }
        return true;
    }

    private boolean applyViewText(TextView view,Text text){
        if (null==view||null==text){
            return false;
        }
        if (view instanceof TextView){
            TextView textView=(TextView)view;
            Resources resources=new Resources();
            Context context=textView.getContext();
            android.content.res.Resources res=null!=context?context.getResources():null;
            //Apply text color
            textView.setText(getText(resources,res,text.getObjects()));
            textView.setTextColor(getColor(context,resources,text.getColor(),textView.getTextColors()));
            float textSize=text.getSize(textView.getTextSize());
            Integer sizeUnit=text.getSizeUnit(null);
            if (null!=sizeUnit) {
                textView.setTextSize(sizeUnit, textSize);
            }else{
                textView.setTextSize(textSize);
            }
            //App hint text
            Text hintText=text.getHint();
            if (null!=hintText) {
                textView.setTextColor(getColor(context, resources, hintText.getColor(), textView.getHintTextColors()));
                textView.setHint(getText(resources,res,hintText.getObjects()));
            }
            return true;
        }
        return false;
    }

    private String getText(Resources resources, android.content.res.Resources res,List<Object> objects){
        if (null!=objects&&objects.size()>0){
            String textValue="";
            for (Object obj:objects) {
                if (null!=(obj=null!=obj&&obj instanceof Integer&&null!=res?(null!=resources?resources:
                        (resources=new Resources())).getText(res, (Integer)obj,Integer.toString((Integer) obj)):obj)){
                    textValue+=(obj instanceof CharSequence?(CharSequence)obj:obj.toString());
                }
            }
            return textValue;
        }
        return null;
    }

    private ColorStateList getColor(Context context,Resources resources,Object colorObj,ColorStateList def){
          if (null!=colorObj){
              Integer color=colorObj instanceof String?resources.getColorFromText((String)colorObj,null):null;
              if (null!=color){
                  return ColorStateList.valueOf(color);
              }
              ColorStateList colorStateList=colorObj instanceof String&&null!=context?resources.
                      getColor(context.getPackageName(),context.getResources(),(String)colorObj,null):null;
              if (null!=colorStateList){
                  return colorStateList;
              }
          }
        return def;
    }

    private Drawable generateObjectDrawable(Context context, Resources resources, android.content.res.Resources res, Object image, Drawable def){
        if (null!=res&&image instanceof String&&null!=context){
            Drawable drawable=resources.getDrawable(context.getPackageName(),res,(String)image,null);
            image=null!=drawable?drawable:image;
        }
        if (image instanceof String&&null!=resources){
            Integer color=resources.getColorFromText((String)image,null);
            image=null!=color?color:image;
        }
        image= image instanceof Integer&&null!=resources&&null!=res?resources.getDrawable(res,(Integer)image,
                resources.getColorDrawable(res,(Integer)image,new ColorDrawable((Integer)image))):image;
        image=null!=image&&image instanceof Bitmap ?new BitmapDrawable((Bitmap) image):image;
        return null!=image&&image instanceof Drawable?((Drawable)image):def;
    }
}
