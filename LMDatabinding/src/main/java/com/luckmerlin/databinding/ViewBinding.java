//package com.luckmerlin.databinding;
//
//import android.app.Activity;
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.ContentProvider;
//import android.content.Context;
//import android.content.res.ColorStateList;
//import android.graphics.Bitmap;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.ColorDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.widget.AbsListView;
//import android.widget.Adapter;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.databinding.ViewDataBinding;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.luckmerlin.databinding.text.OnEditActionChange;
//import com.luckmerlin.databinding.text.OnEditActionChangeListener;
//import com.luckmerlin.databinding.text.OnEditTextChange;
//import com.luckmerlin.databinding.text.OnEditTextChangeListener;
//import com.luckmerlin.databinding.touch.OnViewClick;
//import com.luckmerlin.databinding.touch.OnViewLongClick;
//import com.luckmerlin.databinding.touch.OnViewTouch;
//import com.luckmerlin.databinding.view.Image;
//import com.luckmerlin.databinding.view.Text;
//import com.luckmerlin.databinding.view.Touch;
//import com.luckmerlin.match.Matchable;
//
//import java.lang.ref.WeakReference;
//import java.lang.reflect.Method;
//import java.util.List;
//
///**
// * @deprecated
// */
//final class ViewBinding {
//
//    private interface OnIterate{
//        Boolean onIterated(View view,Object obj);
//    }
//
//    boolean bind(View view, BindingObject ...bindings){
//        if (null==view||null==bindings||bindings.length<=0){
//            return false;
//        }
//        for (BindingObject binding:bindings) {
//            if (binding instanceof BindingList){
//                BindingList list=(BindingList)binding;
//                if (null!=list&&list.size()>0){
//                    for (BindingObject child:list) {
//                        bind(view,child);
//                    }
//                }
//                continue;
//            }
//            if (binding instanceof CustomBinding){
//                ((CustomBinding)binding).onBind(view);
//            }
//            if (view instanceof TextView){
//                TextView textView=(TextView)view;
//                if (binding instanceof Text){
//                    applyViewText(textView,(Text)binding);
//                }
//                if (binding instanceof OnEditActionChange) {
//                    textView.setOnEditorActionListener(new OnEditActionChangeListener((OnEditActionChange)binding));
//                }
//                if (binding instanceof OnEditTextChange){
//                    textView.addTextChangedListener(new OnEditTextChangeListener((OnEditTextChange) binding));
//                }
//            }
//            if (binding instanceof Image){
//                applyViewImage(view,(Image)binding);
//            }
//            if (binding instanceof Touch){
//                applyViewTouch(view,(Touch)binding);
//            }
//            if (binding instanceof com.luckmerlin.databinding.view.View){
//                applyViewBinding(view,(com.luckmerlin.databinding.view.View)binding);
//            }
//        }
//        return true;
//    }
//
//    private boolean applyViewBinding(View view, com.luckmerlin.databinding.view.View binding){
//        if (null!=view&&null!=binding){
//            Object viewObject=binding.getView();
//            View viewChild=null!=viewObject?new ViewCreator().create(view.getContext(),viewObject):null;
//            if (null==viewChild||viewChild.getParent()!=null){
//                return false;
//            }
//            int type=binding.getType();
//            switch (type){
//                case com.luckmerlin.databinding.view.View.ADD:
//                    if (view instanceof ViewGroup){
//                        int index=binding.getPosition();
//                        ViewGroup viewGroup=((ViewGroup)view);
//                        ViewGroup.LayoutParams layoutParams=binding.getParms();
//                        int count=viewGroup.getChildCount();
//                        index=index>=0&&index<=count?index:count;
//                        ((ViewGroup)view).addView(viewChild,index,null!=layoutParams?layoutParams: new
//                                ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//                        return true;
//                    }
//                    return false;
//                case com.luckmerlin.databinding.view.View.SET:
//                    if (view instanceof ViewGroup){
//                        ViewGroup viewGroup=((ViewGroup)view);
//                        View firstChild=viewGroup.getChildCount()>0?viewGroup.getChildAt(0):null;
//                        ViewGroup.LayoutParams firstChildParms=null!=firstChild?firstChild.getLayoutParams():null;
//                        viewGroup.removeAllViews();
//                        firstChildParms=null!=firstChildParms?firstChildParms:binding.getParms();
//                        viewGroup.addView(viewChild,null!=firstChildParms?firstChildParms:
//                                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//                        return true;
//                    }
//                    return false;
//                case com.luckmerlin.databinding.view.View.REPLACE:
//                    ViewParent parent=view.getParent();
//                    if (null!=parent&&parent instanceof ViewGroup){
//                        ViewGroup vg=(ViewGroup)parent;
//                        int count=vg.getChildCount();
//                        int index=-1;
//                        View child=null;
//                        ViewGroup.LayoutParams params=null;
//                        for (int i = 0; i < count; i++) {
//                            if (null!=(child=vg.getChildAt(i))&&child==view){
//                                index=i;
//                                params=child.getLayoutParams();
//                                break;
//                            }
//                        }
//                        if (index>=0){
//                            vg.removeViewAt(index);
//                            params=null!=params?params:binding.getParms();
//                            vg.addView(viewChild,index,null!=params?params: new ViewGroup.LayoutParams
//                                    (ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//                            return true;
//                        }
//                        return false;
//                    }
//                    return false;
//            }
//        }
//        return false;
//    }
//
//    private boolean applyViewTouch(View view,Touch touch){
//        if (null==view||null==touch){
//            return false;
//        }
//        Object object=touch.getObject();
//        if (null!=object&&object instanceof Boolean&&!(Boolean)object){
//            return false;
//        }
//        Object tagObject=touch.getTag();
//        if (null!=tagObject){
//            if (tagObject instanceof Activity||tagObject instanceof Service|| tagObject instanceof BroadcastReceiver||tagObject instanceof ContentProvider){
//                tagObject=new WeakReference<>(tagObject);
//            }
//        }
//        Integer dispatch=touch.isDispatchEnable();
//        final int dispatchEvent=null!=dispatch?dispatch:Touch.NONE;
//        final Object finalTagObject=tagObject;
//        final boolean dispatchTouch=(dispatchEvent&Touch.TOUCH)!=0;
//        if (dispatchTouch||(null!=object&&object instanceof OnViewTouch)){
//            view.setOnTouchListener((View v, MotionEvent event)->{
//                final Object finalObject=getTagObject(finalTagObject);
//                return dispatchEvent(view, object,dispatchTouch,(View child,Object obj)-> null!=obj&&obj instanceof
//                        OnViewTouch&&((OnViewTouch)obj).onViewTouched(view, event,finalObject));
//                });
//        }
//        final boolean dispatchLongClick=(dispatchEvent&Touch.LONG_CLICK)!=0;
//        if (dispatchTouch||(null!=object&&object instanceof OnViewLongClick)){
//            view.setOnLongClickListener((View v)->{
//                final Object finalObject=getTagObject(finalTagObject);
//                return dispatchEvent(view, object,dispatchLongClick,(View child,Object obj)-> null!=obj&&obj instanceof
//                        OnViewLongClick&&((OnViewLongClick)obj).onViewLongClick(view,finalObject));
//            });
//        }
//        final boolean dispatchClick=(dispatchEvent&Touch.CLICK)!=0;
//        if (dispatchClick||(null!=object&&object instanceof OnViewClick)){
//            int dither=touch.getClickDither();
//            final int finalDither=dither<0?400:dither;
//            final Object[] ditherObject=new Object[2];
//            final View.OnClickListener clickListener=(View v)->{
//                Object clickCount=ditherObject[1];
//                final int finalClickCount=null!=clickCount&&clickCount instanceof Integer &&((Integer)clickCount)>0?(Integer) clickCount:0;
//                final Object finalObject=getTagObject(finalTagObject);
//                dispatchEvent(view, object,dispatchClick,(View child,Object obj)-> null!=obj&&obj instanceof
//                        OnViewClick&&((OnViewClick)obj).onViewClick(view, finalClickCount,finalObject));
//            };
//            view.setOnClickListener((View v)-> {
//                if (finalDither>0){
//                   Object dithering=ditherObject[0];
//                   Object count=ditherObject[1];
//                   Runnable runnable=null;
//                   if (null!=dithering&&dithering instanceof Runnable){
//                       view.removeCallbacks(runnable=(Runnable)dithering);
//                   }else{
//                       ditherObject[0]=runnable=()->{
//                           clickListener.onClick(v);
//                           ditherObject[0]=ditherObject[1]=null;
//                       };
//                   }
//                   ditherObject[1]=null!=count&&count instanceof Integer?(Integer)count+1:1;
//                   v.postDelayed(runnable,finalDither);
//                   return;
//                }else{
//                    clickListener.onClick(v);
//                }
//            });
//        }
//        return true;
//    }
//
//    private Object getTagObject(Object object){
//        while (null!=object&&object instanceof WeakReference){
//            object=((WeakReference)object).get();
//        }
//        return object;
//    }
//
//    private boolean applyViewImage(View view,Image image){
//        if (null==view||null==image){
//            return false;
//        }
//        final Context context=view.getContext();
//        Resources resources=new Resources();
//        android.content.res.Resources res=view.getResources();
//        //Apply background
//        Object background=image.getBackground();
//        Drawable backgroundDrawable=generateObjectDrawable(context,resources,res,background,null);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            view.setBackground(backgroundDrawable);
//        }else{
//            view.setBackgroundDrawable(backgroundDrawable);
//        }
//        //Apply image src for image view
//        if (view instanceof ImageView){
//            ImageView imageView=(ImageView)view;
//            Object imageObject=image.getSrc();
//            Drawable imageDrawable=generateObjectDrawable(context,resources,res,imageObject,null);
//            imageView.setImageDrawable(imageDrawable);
//        }
//        return true;
//    }
//
//    private boolean applyViewText(TextView view,Text text){
//        if (null==view||null==text){
//            return false;
//        }
//        if (view instanceof TextView){
//            TextView textView=(TextView)view;
//            Resources resources=new Resources();
//            Context context=textView.getContext();
//            android.content.res.Resources res=null!=context?context.getResources():null;
//            //Apply text color
//            textView.setText(getText(resources,res,text.getObjects()));
//            textView.setTextColor(getColor(context,resources,text.getColor(),textView.getTextColors()));
//            float textSize=text.getSize(textView.getTextSize());
//            Integer sizeUnit=text.getSizeUnit();
//            if (null!=sizeUnit) {
//                textView.setTextSize(sizeUnit, textSize);
//            }else{
//                textView.setTextSize(textSize);
//            }
//            //App hint text
//            Text hintText=text.getHint();
//            if (null!=hintText) {
//                textView.setTextColor(getColor(context, resources, hintText.getColor(), textView.getHintTextColors()));
//                textView.setHint(getText(resources,res,hintText.getObjects()));
//            }
//            return true;
//        }
//        return false;
//    }
//
//    private String getText(Resources resources, android.content.res.Resources res,List<Object> objects){
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
//    private ColorStateList getColor(Context context,Resources resources,Object colorObj,ColorStateList def){
//          if (null!=colorObj){
//              Integer color=colorObj instanceof String?resources.getColorFromText((String)colorObj,null):null;
//              if (null!=color){
//                  return ColorStateList.valueOf(color);
//              }
//              ColorStateList colorStateList=colorObj instanceof String&&null!=context?resources.
//                      getColor(context.getPackageName(),context.getResources(),(String)colorObj,null):null;
//              if (null!=colorStateList){
//                  return colorStateList;
//              }
//          }
//        return def;
//    }
//
//    private Drawable generateObjectDrawable(Context context, Resources resources, android.content.res.Resources res, Object image, Drawable def){
//        if (null!=res&&image instanceof String&&null!=context){
//            Drawable drawable=resources.getDrawable(context.getPackageName(),res,(String)image,null);
//            image=null!=drawable?drawable:image;
//        }
//        if (image instanceof String&&null!=resources){
//            Integer color=resources.getColorFromText((String)image,null);
//            image=null!=color?color:image;
//        }
//        image= image instanceof Integer&&null!=resources&&null!=res?resources.getDrawable(res,(Integer)image,
//                resources.getColorDrawable(res,(Integer)image,new ColorDrawable((Integer)image))):image;
//        image=null!=image&&image instanceof Bitmap ?new BitmapDrawable((Bitmap) image):image;
//        return null!=image&&image instanceof Drawable?((Drawable)image):def;
//    }
//
//    private boolean dispatchEvent(View view,Object object,boolean dispatch,OnIterate callback){
//        if (null!=callback&&null!=view){
//            if ((null!=object&&callback.onIterated(view,object))){
//                return true;
//            }
//            if (!dispatch){
//                return false;
//            }
//            if (dispatchEventToViewModel(view,callback)){
//                return true;
//            }
//            Adapter adapter=view instanceof AdapterView?((AdapterView)view).getAdapter():null;
//            if (null!=adapter&&callback.onIterated(view,adapter)){
//                return true;
//            }
//            Object reAdapter=view instanceof RecyclerView ?((RecyclerView)view).getAdapter():null;
//            if (null!=reAdapter&&callback.onIterated(view,reAdapter)){
//                return true;
//            }
//            Context context=view.getContext();
//            if (null!=context&&callback.onIterated(view,context)){
//                return true;
//            }
//            context=null!=context?context.getApplicationContext():null;
//            if (null!=context&&callback.onIterated(view,context)){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean dispatchEventToViewModel(View view,OnIterate callback){
//        if (null!=view&&null!=callback){
//            ViewDataBinding binding=DataBindingUtil.getBinding(view);
//            if (null!=(null!=binding?new BindingMethodIterator().iterate(binding, (o)-> {
//                    Object current=null!=o&&o instanceof BindingMethodIterator.MatchBinding ? ((BindingMethodIterator.MatchBinding)o).mCurrent:null;
//                    Boolean iterated=null!=current?callback.onIterated(view,current):null;
//                    if (null!=iterated&&iterated){
//                        return Matchable.MATCHED;
//                    }
//                    return null;
//            }):null)){
//                return true;
//            }
//            ViewParent parent=view.getParent();
//            return null!=parent&&parent instanceof View&&dispatchEventToViewModel((View)parent,callback);
//        }
//        return false;
//    }
//}
