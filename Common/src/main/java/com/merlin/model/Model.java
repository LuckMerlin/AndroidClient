package com.merlin.model;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.merlin.api.Res;
import com.merlin.debug.Debug;
import com.merlin.dialog.Dialog;
import com.merlin.dialog.PopupWindow;
import com.merlin.lib.R;
import com.merlin.retrofit.Retrofit;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

public class Model extends Retrofit {
    private WeakReference<View> mRootView=null;
    private final static String LABEL_ACTIVITY_DATA="activityData";
    private Dialog mLoadingDialog;
    private PopupWindow mPopWindow;

    public interface OnBindChange{
        boolean onBindChanged(Object obj, String debug);
    }

    public interface OnModelAttachedToWindow{
        void onModelAttachedToWindow(View v, Model model);
    }

    public interface OnModelDetachedFromWindow{
        void onModelDetachedFromWindow(View v, Model model);
    }

    @Override
    protected String onResolveUrl(Class<?> cls, Executor callbackExecutor) {
        return "http://192.168.0.3:5000";
    }

    public final View getRoot() {
        WeakReference<View> reference=mRootView;
        return null!=reference?reference.get():null;
    }

    public interface OnActivityResult{
        void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
    }

    public interface OnActivityIntentChange{
        void onActivityIntentChanged(Activity activity, Intent intent);
    }

    public interface OnActivityResume{
        void onActivityResume(Activity activity, Intent intent);
    }

    public interface OnActivityBackPress{
        boolean onActivityBackPressed(Activity activity);
    }

    protected void onRootAttached(View root){
        //Do nothing
    }

    protected final CharSequence getViewText(int viewId,CharSequence def){
        View view=findViewById(viewId);
        if (null!=view&&view instanceof TextView){
            return ((TextView)view).getText();
        }
        return null;
    }

    protected final boolean isViewChecked(int viewId,boolean def){
        View view=findViewById(viewId);
        if (null!=view&&view instanceof Checkable){
            return ((Checkable)view).isChecked();
        }
        return def;
    }

    private boolean initial(View view){
        if (null!=view){
            mRootView=new WeakReference<>(view);
//            ViewTreeObserver observer=view.getViewTreeObserver();
//            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//                @Override
//                public void onViewAttachedToWindow(View v) {
//                    if (Model.this instanceof OnModelAttachedToWindow){
//                        ((OnModelAttachedToWindow)Model.this).onModelAttachedToWindow(v,Model.this);
//                    }
//                }
//
//                @Override
//                public void onViewDetachedFromWindow(View v) {
//                    if (Model.this instanceof OnModelDetachedFromWindow){
//                        ((OnModelDetachedFromWindow)Model.this).onModelDetachedFromWindow(v,Model.this);
//                    }
//                    view.removeOnAttachStateChangeListener(this);
//                }
//            });
//            checkStatusBarTapBind();
            onRootAttached(view);
            return true;
        }
        return false;
    }

    public final boolean toast(int textResId){
        return toast(textResId,null);
    }

    public final String getText(int textResId,Object ...args){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        return null!=context?context.getResources().getString(textResId,args):null;
    }

    public final boolean toast(int textResId,String note){
        String text=getText(textResId);
        return toast((null!=text?text:"")+(null!=note?note:""));
    }

    protected final <T extends ViewDataBinding> T inflate(int layoutId, Res...res){
        return inflate(getViewContext(),layoutId,res);
    }

    protected final <T extends ViewDataBinding> T inflate(int layoutId,ViewGroup parent, Res ...res) {
        return inflate(getViewContext(),layoutId,parent,res);
    }

    protected final <T extends ViewDataBinding> T inflate(Context context,int layoutId, Res ...res){
        return inflate(context,layoutId,null,res);
    }

    protected final <T extends ViewDataBinding> T inflate(Context context,int layoutId,ViewGroup parent, Res ...res){
        T binding=null!=context?DataBindingUtil.inflate(LayoutInflater.from(context),layoutId,parent,null!=parent):null;
        if (null!=binding&&null!=res&&res.length>0){
            for (Res r:res) {
                Integer resourceId=null!=r?r.getResourceId():null;
                if (null!=resourceId){
                    binding.setVariable(resourceId,r.getArg());
                }
            }
        }
        return binding;
    }

    public final boolean toast(String msg){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        if (null!=context&&null!=msg){
            if (Looper.getMainLooper()==Looper.myLooper()) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }else{
                root.post(()->Toast.makeText(context, msg, Toast.LENGTH_LONG).show());
            }
            return true;
        }
        return false;
    }

    public final View findViewById(int id){
        return findViewById(id,View.class);
    }

    public final <T> T findViewById(int id,Class<T> cls){
        View root=getRoot();
        View child=null!=root?root.findViewById(id):null;
        return null!=child&&null!=cls?(T)child:null;
    }

    public final Context getViewContext(){
        WeakReference<View> reference=mRootView;
        View view= null!=reference?reference.get():null;
        return null!=view?view.getContext():null;
    }

    public final Context getContext(){
        Context context=getViewContext();
        return null!=context?context.getApplicationContext():null;
    }

    public final boolean dismiss(ViewDataBinding binding){
        return null!=binding&&dismiss(binding.getRoot());
    }

    public final boolean dismiss(View view){
        PopupWindow popupWindow=null!=view?mPopWindow:null;
        View content=null!=popupWindow?popupWindow.getContentView():null;
        if (null!=content&&content==view){
            popupWindow.dismiss();
            return true;
        }
        return false;
    }

    protected final boolean showAtLocationAsContext(View parent,ViewDataBinding binding) {
        return showAtLocation(parent,binding,PopupWindow.DISMISS_OUT_MASK|PopupWindow.DISMISS_INNER_MASK);
    }


    protected final boolean showAtLocation(View parent,ViewDataBinding binding,Integer dismissFlag){
        return null!=binding&&showAtLocation(parent,binding.getRoot(),dismissFlag);
    }

    protected final boolean showAtLocationAsContext(View parent,View root) {
        return showAtLocation(parent,root,PopupWindow.DISMISS_OUT_MASK|PopupWindow.DISMISS_INNER_MASK);
    }

    protected final boolean showAtLocation(View parent,View root,Integer dismissFlag) {
        return showAtLocation(parent,root,Gravity.CENTER,0,0,dismissFlag);
    }
    protected final boolean showAtLocation(View parent,ViewDataBinding binding,int gravity,int x,int y,Integer dismissFlag) {
        return null!=binding&&showAtLocation(parent,binding.getRoot(),gravity,x,y,dismissFlag);
    }

    private PopupWindow fetchPopWindow(){
        PopupWindow currentPopWindow=mPopWindow;
        final PopupWindow popupWindow=null!=currentPopWindow?currentPopWindow:(mPopWindow=new PopupWindow(true,(window)->{
            PopupWindow curr=mPopWindow;
            if (null!=curr&&null!=window&&curr==window){
                mPopWindow=null;
            }
        }));
        return popupWindow;
    }

    protected final boolean showAtLocation(View parent,View root,int gravity,int x,int y,Integer dismissFlag){
        if (null!=root&&(null==root.getParent())){
            PopupWindow popupWindow=fetchPopWindow();
            if (null!=popupWindow) {
                popupWindow.setContentView(root);
                dismissFlag = null == dismissFlag ? PopupWindow.DISMISS_OUT_MASK | PopupWindow.DISMISS_INNER_MASK : dismissFlag;
                popupWindow.showAtLocation(parent, gravity, x, y, getRoot(), dismissFlag);
                return true;
            }
            return false;
        }
        return false;
    }

    protected final boolean showAsDropDown(View anchor,ViewDataBinding binding, int x, int y,Integer dismissFlag) {
        return null!=binding&&showAsDropDown(anchor,binding.getRoot(),x,y,null,dismissFlag);
    }

    protected final boolean showAsDropDown(View anchor,View root, int x, int y,Object interrupter,Integer dismissFlag){
        if (null!=anchor&&null!=root&&null==root.getParent()&&(anchor!=root)){
            PopupWindow popupWindow=fetchPopWindow();
            if (null!=popupWindow){
                popupWindow.setContentView(root);
                dismissFlag = null == dismissFlag ? PopupWindow.DISMISS_OUT_MASK | PopupWindow.DISMISS_INNER_MASK : dismissFlag;
                return popupWindow.showAsDropDown(anchor, x, y, null!=interrupter?interrupter:getRoot(), dismissFlag);
            }
            return false;
        }
        return false;
    }

    protected final boolean runOnUiThread(Runnable runnable){
        return null!=runnable&&post(runnable,0);
    }

    protected final boolean post(Runnable runnable){
        return post(runnable,0);
    }

    protected final boolean post(Runnable runnable,int delay){
        if (null!=runnable){
            View root=getRoot();
            if (null==root){
                return new Handler(Looper.getMainLooper()).postDelayed(runnable,delay);
            }
            return root.postDelayed(runnable,delay);
        }
        return false;
    }

    protected final Application getApplication(){
        Context context=getContext();
        context=null!=context?context instanceof Application?context:context.getApplicationContext():null;
        return null!=context&&context instanceof Application?(Application)context:null;
    }

    public final boolean finishActivity(String debug){
        return finishActivity(null,null,debug);
    }

    public final boolean finishActivity(Integer resultCode, Intent data,String debug){
        Activity activity=getActivity(null);
        if (null!=activity){
            Debug.D(getClass(),"Finish activity "+activity+" "+resultCode+" "+(null!=debug?debug:"."));
            if (null!=resultCode){
                activity.setResult(resultCode,data);
            }
            activity.finish();
            return true;
        }
        return false;
    }

    public final <T extends Activity> Activity getActivity(Class<T> cls){
        View root=getRoot();
        Context context=null!=root?root.getContext():null;
        if (null!=context&&context instanceof Activity){
            return (Activity)context;
        }
        return null;
    }

    protected final Intent getIntent(){
       Activity activity= getActivity(null);
        return null!=activity?activity.getIntent():null;
    }

    public final Object getActivityDataFromIntent(Intent intent){
        Bundle bundle=null!=intent?intent.getExtras():null;
        return null!=bundle?bundle.get(LABEL_ACTIVITY_DATA):null;
    }

    protected final boolean startActivity(Class<? extends Activity> cls, String key,String value){
        Bundle bundle=null;
        if (null!=key&&null!=value){
            bundle=new Bundle();
            bundle.putString(key,value);
        }
        return startActivity(cls,bundle);
    }

    protected final boolean startActivity(Class<? extends Activity> cls, Object data){
        Context context=getContext();
        if (null!=context&&null!=cls){
            Intent intent=new Intent(context,cls);
            if (null!=data){
                if (data instanceof Bundle) {
                    intent.putExtras((Bundle) data);
                }else if (data instanceof Parcelable){
//                    intent.putExtras((Bundle) data);
                }else if (data instanceof Serializable){

                }
            }
            return startActivity(intent,null);
        }
        return false;
    }

    protected final boolean startActivity(Class<? extends Activity> cls,Integer forResultCode){
        Context context=getContext();
        return null!=context&&null!=cls&&startActivity(new Intent(context,cls),forResultCode);
    }

    protected final boolean startActivity(Intent intent,Integer forResultCode){
        Context context=null!=intent?null!=forResultCode?getViewContext():getContext():null;
        if (null==context){
            Debug.W(getClass(),"Can't start activity with NULL intent."+intent+" "+context);
            return false;
        }
        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(null!=forResultCode){
                if ((context instanceof Activity)){
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                     Debug.D(getClass(),"Start activity for result."+forResultCode);
                     ((Activity)context).startActivityForResult(intent,forResultCode);
                     return true;
                }else{
                    Debug.W(getClass(),"Will not receive activity result while context is not instanceof activity.");
                }
            }
            context.startActivity(intent);
            return true;
        }catch (Exception e){
            Debug.E(getClass(),"Fail start activity.e="+e+" "+intent,e);
        }
        return false;
    }

    protected final LifecycleProvider getLifecycleProvider(){
        View view=getRoot();
        Context context=null!=view?view.getContext():null;
        if (null!=context&&context instanceof LifecycleProvider){
            return (LifecycleProvider)context;
        }
        return null;
    }

    protected final boolean closeIO(Closeable closeable){
        if (null!=closeable){
            try {
                closeable.close();
                return true;
            } catch (IOException e) {
                //Do nothing
            }
        }
        return false;
    }

    protected final ViewDataBinding getBiniding(){
        View root=getRoot();
        return null!=root?DataBindingUtil.getBinding(root):null;
    }

    protected final String showLoading(Object messageText){
        return showLoading(getViewContext(),messageText);
    }

    protected final String showLoading(Context context,Object messageText){
        if (null!=context){
            Dialog dialog=mLoadingDialog;
            dialog=null!=dialog?dialog:(mLoadingDialog=new Dialog(context));
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
             dialog.setContentView(R.layout.dialog_loading,true).title(messageText).show();
            return Long.toString(System.currentTimeMillis());
        }
        return null;
    }

    protected final ContentResolver getContentResolver(){
        return getContentResolver(null);
    }

    protected final ContentResolver getContentResolver(Context context){
        context=null!=context?context:getContext();
        context=null!=context?context:getViewContext();
        return null!=context?context.getContentResolver():null;
    }

    protected final boolean dismissLoading(String key){
        Dialog dialog=mLoadingDialog;
        if (null!=dialog){
            mLoadingDialog=null;
            dialog.dismiss();
            return true;
        }
        return false;
    }

}
