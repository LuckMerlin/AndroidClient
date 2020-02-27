package com.merlin.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import com.merlin.adapter.BrowserAdapter;
import com.merlin.adapter.NasBrowserAdapter;
import com.merlin.api.Address;
import com.merlin.api.Label;
import com.merlin.api.OnApiFinish;
import com.merlin.api.Reply;
import com.merlin.bean.ClientMeta;
import com.merlin.bean.FolderData;
import com.merlin.bean.NasFile;
import com.merlin.bean.NasFolder;
import com.merlin.client.Client;
import com.merlin.client.R;
import com.merlin.client.databinding.ClientDetailBinding;
import com.merlin.client.databinding.DeviceTextBinding;
import com.merlin.client.databinding.FileBrowserMenuBinding;
import com.merlin.debug.Debug;
import com.merlin.protocol.Tag;
import com.merlin.view.OnLongClick;
import com.merlin.view.OnTapClick;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import retrofit2.http.POST;

import static com.merlin.api.What.WHAT_SUCCEED;

public class FileBrowserModel extends Model implements Label, Tag, OnTapClick, OnLongClick, Model.OnActivityResume,Model.OnActivityBackPress {
    private Map<String,Object> mAllClientMetas=new HashMap<>();
    private final ObservableField<BrowserModel> mCurrent=new ObservableField<>();
    private final ObservableField<FolderData> mCurrentFolder=new ObservableField<>();
    private final BrowserModel.OnPageDataLoad mPageDataLoad=(model,folder)-> {
        BrowserModel curr=mCurrent.get();
        if (null!=curr&&null!=model&&model==curr){
            mCurrentFolder.set(folder);
        }
    };

    public interface OnBrowserModelChange{
        void onBrowserModelChanged(BrowserModel last,BrowserModel current);
    }

    private interface Api{
        @POST(Address.PREFIX_FILE_CLIENT_META)
        Observable<Reply<ClientMeta>> queryClientMeta();
    }

    public FileBrowserModel(){

    }

    @Override
    protected void onRootAttached(View root) {
        super.onRootAttached(root);
//        putClientMeta(ClientMeta.buildLocalClient(getContext()), "After mode create.");
        refreshClientMeta("After mode create.");
    }

    private boolean putClientMeta(ClientMeta meta,String debug){
        String url=null!=meta?meta.getUrl():null;
        if (null!=url&&url.length()>0){
            Map<String,Object> list=mAllClientMetas;
            Object object=(list=null!=list?list:(mAllClientMetas=new HashMap<>())).get(url);
            if (null!=object&&object instanceof BrowserModel){
                ((BrowserModel)object).setMeta(meta,"After meta put.");
            }else{
                object=meta;
            }
            Debug.D(getClass(),"Put client "+url+" "+(null!=debug?debug:"."));
            list.put(url,object);
            changeDevice(meta,false,"After client put "+(null!=debug?debug:"."));
            return true;
        }
        Debug.W(getClass(),"Can't put client meta "+(null!=debug?debug:"."));
        return false;
    }

    private BrowserModel createModel(ClientMeta meta){
        if (null!=meta){
            Context context=getViewContext();
            BrowserModel model=meta.isLocalClient()?new LocalBrowserModel(context,meta): new NasBrowserModel(context,meta,meta.getUrl(),mPageDataLoad);
            if (null!=model){
                mCurrent.set(model);
                return model;
            }
            return null;
        }
        return null;
    }

    private boolean changeDevice(ClientMeta client,boolean force,String debug){
        final String url=null!=client?client.getUrl():null;
        if (null!=url&&url.length()>0){
            if (force||null==mCurrent.get()){
                Debug.D(getClass(),"Change browser device "+client.getName()+" "+(null!=debug?debug:"."));
                Map<String,Object> map=mAllClientMetas;
                BrowserModel model;
                if (null!=map){
                    Object object=map.get(url);
                    model=null!=object&&object instanceof BrowserModel?(BrowserModel)object: createModel(client);
                    if (null!=model){
                        BrowserModel curr=mCurrent.get();
                         mCurrent.set(model);
                         if (null!=model&&model instanceof BrowserModel){
                             ((BrowserModel)model).onBrowserModelChanged(curr,model);
                         }
                         return true;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private boolean refreshClientMeta(String debug){
        Debug.D(getClass(),"Refresh client meta "+(null!=debug?debug:"."));
        return null!=call(Api.class,(OnApiFinish<Reply<ClientMeta>>)(what, note, data, arg)->{
            ClientMeta meta=what==WHAT_SUCCEED&&null!=data?data.getData():null;
            if(null!=meta){
                putClientMeta(meta,"After client meta response.");
            }
        }).queryClientMeta();
    }

    @Override
    public boolean onTapClick(View view, int clickCount, int resId, Object data)  {
        switch (clickCount){
            case 1:
                switch (resId){
                    case R.id.fileBrowser_deviceNameTV:
                        return (null!=view&&view instanceof TextView&&showClientMenu((TextView)view,"After tap click."))||true;
                    case R.drawable.selector_menu:
                        return showBrowserMenu(view,"After tap click.");
                    case R.drawable.selector_back:
                        return onBackIconPressed(view,"After back pressed.");
                    case R.drawable.cancel_selector:
                        BrowserModel model=getCurrentModel();
                        return null!=model&&(!model.isMode(BrowserModel.MODE_NORMAL)&&model.entryMode(BrowserModel.MODE_NORMAL,"After cancel tap click."));
                    case R.drawable.choose_all_selector:
                        model=getCurrentModel();
                        BrowserAdapter adapter=null!=model&&model.isMode(BrowserModel.MODE_MULTI_CHOOSE)?model.getBrowserAdapter():null;
                        return null!=adapter&&adapter.chooseAll(true);
                    case R.drawable.ic_menu_alls:
                        model=getCurrentModel();
                        adapter=null!=model&&model.isMode(BrowserModel.MODE_MULTI_CHOOSE)?model.getBrowserAdapter():null;
                        return null!=adapter&&adapter.chooseAll(false);
                    default:
                        break;
                }
                break;
            case 2:
                switch (resId){
                    case R.id.fileBrowser_deviceNameTV:
                        return (null!=view&&null!=data&&data instanceof ClientMeta&&showClientDetail(view,(ClientMeta)data,"After tap click."))||true;
                }
        }
        BrowserModel model=getCurrentModel();
        return null!=model&&model.onTapClick(view,clickCount,resId,data);
    }

    private boolean showClientDetail(View view,ClientMeta meta,String debug){
        ClientDetailBinding binding=null!=view&&null!=meta?inflate(R.layout.client_detail):null;
        if (null!=binding){
            binding.setClient(meta);
            return showAtLocation(view,binding,Gravity.CENTER,0,0,null);
        }
        return false;
    }

    private boolean showClientMenu(TextView tv,String debug){
        Map<String,Object> map=mAllClientMetas;
        Context context=null!=tv?tv.getContext():null;
        Set<String> set=null!=map?map.keySet():null;
        final int size=null!=context&&null!=set?set.size():0;
        if (size>0){
            LinearLayout ll=new LinearLayout(context);
            ll.setOrientation(LinearLayout.VERTICAL);
            final OnTapClick click=( view, clickCount, resId, data)-> {
                return (null!=data&&data instanceof ClientMeta&&changeDevice((ClientMeta)data,true,"After device choose."))||true;
            };
            BrowserModel currentModel=mCurrent.get();
            ClientMeta current=null!=currentModel?currentModel.getClientMeta():null;
            for (String child:set) {
                Object object= null!=child?map.get(child):null;
                object=null!=object?object instanceof BrowserModel?((BrowserModel)object).getClientMeta():object:null;
                ClientMeta meta=null!=object&&object instanceof ClientMeta?(ClientMeta)object:null;
                if (null!=meta&&(null==current||!current.equals(meta))){
                    DeviceTextBinding binding=inflate(R.layout.device_text);
                    View root=null!=binding?binding.getRoot():null;
                    if (null!=root){
                        binding.setDevice(meta);
                        ll.addView(root);
                    }
                    continue;
                }
            }
            return showAsDropDown(tv,ll,0,0,click,null);
        }
        return false;
    }

    private boolean showBrowserMenu(View view,String debug){
        FileBrowserMenuBinding binding=null!=view?inflate(R.layout.file_browser_menu):null;
        if (null!=binding){
            binding.setFolder(mCurrentFolder.get());
            binding.setClient(getCurrentModelMeta());
            return showAtLocationAsContext(view,binding);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view, int clickCount, int resId, Object data) {
        BrowserModel model=getCurrentModel();
        return null!=model&&model instanceof OnLongClick&&((OnLongClick)model).onLongClick(view,clickCount,resId,data);
    }

    @Override
    public boolean onActivityBackPressed(Activity activity) {
        BrowserModel model=getCurrentModel();
        return null!=model&&model instanceof OnActivityBackPress&&((OnActivityBackPress)model).onActivityBackPressed(activity);
    }

    @Override
    public void onActivityResume(Activity activity, Intent intent) {
          BrowserModel model=getCurrentModel();
          if (null!=model&&model instanceof OnActivityResume){
              ((OnActivityResume)model).onActivityResume(activity,intent);
          }
    }

    private boolean onBackIconPressed(View view,String debug){
        BrowserModel model=getCurrentModel();
        return null!=model&&model.onBackIconPressed(view,debug);
    }

    private ClientMeta getCurrentModelMeta(){
        BrowserModel model=getCurrentModel();
        return null!=model?model.getClientMeta():null;
    }

    private BrowserModel getCurrentModel(){
       return mCurrent.get();
    }

    public ObservableField<BrowserModel> getCurrent() {
        return mCurrent;
    }

    public ObservableField<FolderData> getCurrentFolder(){
        return mCurrentFolder;
    }

}
