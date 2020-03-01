//package com.merlin.model;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.databinding.ViewDataBinding;
//
//import com.merlin.bean.ClientMeta;
//import com.merlin.client.R;
//import com.merlin.client.databinding.ItemClientBinding;
//import com.merlin.client.databinding.ServerChooseLayoutBinding;
//import com.merlin.debug.Debug;
//
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//
//public class ServerChooseModel extends Model{
//
//    @Override
//    protected void onRootAttached(View root) {
//        super.onRootAttached(root);
//        ViewDataBinding vdb=getBiniding();
//        final ServerChooseLayoutBinding binding=null!=vdb&&vdb instanceof ServerChooseLayoutBinding?((ServerChooseLayoutBinding)vdb):null;
//        if (null==binding){
//            return;
//        }
//        Map<String,Object> map=binding.getClients();
//        Collection<Object> values=null!=map&&map.size()>0?map.values():null;
//        if (null!=values&&values.size()>0){
//            final Map<String,ViewDataBinding> added=new HashMap();
//            for (Object obj:values) {
//                if (null!=(obj=null!=obj&&obj instanceof BrowserModel?((BrowserModel)obj).getClientMeta():obj)&&obj instanceof ClientMeta){
//                    ClientMeta client=(ClientMeta)obj;
//                    String url=null!=client?client.getUrl():null;
//                    if (null==url||url.length()<=0){
//                        Debug.W(getClass(),"Skip add client into choose list.url="+url+" "+client);
//                        continue;
//                    }
//                    ItemClientBinding clientBinding= !added.containsKey(url)?inflate(R.layout.item_client):null;
//                    if(null!=clientBinding){
//                        added.put(url,clientBinding);
//                    }
//                }
//            }
//            if (null==added||added.size()<=0){
//                toast(R.string.noneServerExist);
//                return;
//            }else{
//                binding.setChilds(added.values());
//            }
//        }
//
//    }
//
//}
