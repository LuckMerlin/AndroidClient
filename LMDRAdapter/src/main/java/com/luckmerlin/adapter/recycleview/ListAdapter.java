package com.luckmerlin.adapter.recycleview;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.luckmerlin.core.debug.Debug;
import com.luckmerlin.core.proguard.PublishFields;
import com.luckmerlin.core.proguard.PublishMethods;
import com.luckmerlin.core.proguard.PublishProtectedMethod;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class ListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements LayoutManagerResolver, PublishMethods,PublishProtectedMethod, PublishFields {
    private List<T> mData;
    public final static int TYPE_NONE=0;
    public final static int TYPE_TAIL=-1;
    public final static int TYPE_EMPTY=-3;
    public final static int TYPE_DATA=-4;
    public final static int TYPE_HEAD=-5;
    private WeakReference<RecyclerView> mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private final SparseArray<RecyclerView.ViewHolder> mFixHolder=new SparseArray<>();

    public ListAdapter(T  ...values){
        this(null!=values&&values.length>0? Arrays.asList(values):null);
    }

    public ListAdapter(Collection<T>  list){
        add(list,"");
    }

    protected Integer onResolveViewTypeLayoutId(int viewType){
        return null;
    }

    protected RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater in,int viewType,ViewGroup parent){
        return null;
    }

    public final boolean setFixHolder(int type,View view){
        return null!=view&&null==view.getParent()&&setFixHolder(type,new RecyclerView.ViewHolder(view){});
    }

    public final boolean setFixHolder(int type,RecyclerView.ViewHolder viewHolder){
        SparseArray<RecyclerView.ViewHolder> array=mFixHolder;
        if (null!=array){
            RecyclerView.ViewHolder curr=array.get(type);
            if ((null==curr&&null==viewHolder)||(null!=curr&&null!=viewHolder&&curr==viewHolder)){
                return false;
            }
            array.put(type,viewHolder);
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    protected Integer onResolveDataLayoutId(ViewGroup parent){
        //Do nothing
        return null;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater in=LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder=onCreateViewHolder(in,viewType,parent);
        if (viewHolder ==null){
            Integer integer= onResolveViewTypeLayoutId(viewType);
            integer=null==integer&&viewType==TYPE_DATA?onResolveDataLayoutId(parent):integer;
            viewHolder=generateViewHolder(null,parent,integer,false);
        }
        if (null==viewHolder){
            switch (viewType){
                case TYPE_HEAD:
                    SparseArray<RecyclerView.ViewHolder> fixHolder=mFixHolder;
                    viewHolder=null!=fixHolder?fixHolder.get(TYPE_HEAD):null;
                    break;
                case TYPE_TAIL:
                    fixHolder=mFixHolder;
                    RecyclerView.ViewHolder holder=null!=fixHolder?fixHolder.get(TYPE_TAIL):null;
                    viewHolder=null!=holder?holder:new ViewHolder(new View(parent.getContext()));
                    break;
                case TYPE_EMPTY:
                    fixHolder=mFixHolder;
                    holder=null!=fixHolder?fixHolder.get(TYPE_EMPTY):null;
                    viewHolder=null!=holder?holder:new ViewHolder(new View(parent.getContext()));
                    break;
                default:
                    viewHolder= new ViewHolder(new View(parent.getContext()));
                    break;
            }
        }
        return viewHolder;
    }

    protected void onBindViewHolder(RecyclerView.ViewHolder holder,int viewType,ViewDataBinding binding,int position,T data, List<Object> payloads){
        //Do nothing
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        ViewDataBinding binding=null!=holder&&holder instanceof BindingViewHolder?((BindingViewHolder)holder).getBinding():null;
        T data=getItemData(position);
        onBindViewHolder(holder,null!=holder?holder.getItemViewType():TYPE_NONE,binding,position,data,payloads);
        if (null==payloads||payloads.isEmpty()){
            onBindViewHolder(holder, position);
        } else {
//           View itemView=holder.itemView;
//           if (null!=itemView){
//               itemView.setVisibility(((Item)payloads.get(0)).disabled ? View.VISIBLE : View.INVISIBLE);
//           }
        }
    }

    @Override
    public final void onBindViewHolder( RecyclerView.ViewHolder holder, int position) {
        //Do nothing
    }

    protected final boolean scrollToPosition(int position){
        RecyclerView recyclerView=getRecyclerView();
        if (null!=recyclerView){
            recyclerView.scrollToPosition(position);
            return true;
        }
        return false;
    }

    public final T getItemData(int position){
        List<T> data=mData;
        return position>=0&&null!=data&&position<data.size()?data.get(position):null;
    }

    @Override
    public final int getItemCount() {
        int dataCount=getDataCount();
        SparseArray<RecyclerView.ViewHolder> fixHolder=mFixHolder;
        int increase=0;
        if (dataCount>0&&(null!=fixHolder&&null!=fixHolder.get(TYPE_HEAD))){
            increase++;
        }
        return (dataCount<=0?0:dataCount)+1+increase;
    }

    protected int onResolveItemViewType(int position,int size,T data) {
        if (size<=0){
            return TYPE_EMPTY;
        }
        SparseArray<RecyclerView.ViewHolder> fixHolder=mFixHolder;
        if (position == 1&&(null!=fixHolder&&null!=fixHolder.get(TYPE_HEAD))){
            return TYPE_HEAD;
        }
        if (position == size){
            return TYPE_TAIL;
        }
        return TYPE_DATA;
    }

    @Override
    public final int getItemViewType(int position) {
        List<T> data=mData;
        int size=null!=data?data.size():0;
        return onResolveItemViewType(position,size,position>=0&&position<size?data.get(position):null);
    }

    public final int getDataCount(){
        List<T> data=mData;
        return null!=data?data.size():0;
    }

    public final boolean clean(){
        return clean(null);
    }

    public final boolean clean(String debug){
        List<T> data=mData;
        int size=null!=data?data.size():0;
        if (size>0){
            data.clear();
            notifyItemRangeRemoved(0,size);
            return true;
        }
        return false;
    }

    public final ArrayList<T> getData() {
        List<T> data=mData;
        int length=null!=data?data.size():0;
        ArrayList<T> result=length>0?new ArrayList<T>(length):null;
        return null!=result&&result.addAll(data)?result:null;
    }

    public final ArrayList<T> remove(T data,String debug){
        if (null!=data){
            List list=new ArrayList<>(1);
            list.add(data);
            return remove(list,debug);
        }
        return null;
    }

    public final ArrayList<T> remove(List<?> list,String debug){
        if (null!=list&&list.size()>0){
            ArrayList<T> removed=new ArrayList<>(list.size());
            T data=null;
            for (Object obj:list) {
                if (null!=(data=null!=obj?removeData(obj,debug):null)){
                    removed.add(data);
                }
            }
            return null!=removed&&removed.size()>0?removed:null;
        }
        return null;
    }

    public final T removeData(Object data,String debug){
        List<T> list=null!=data?mData:null;
        if (null!=list){
            synchronized (list){
                int index= null!=list?list.indexOf(data):-1;
                T indexData=index>=0?list.get(index):null;
                if (null!=indexData&&list.remove(indexData)){
                    notifyItemRemoved(index);
                    if (list.size()==0){
                        mData=null;
                    }
                    return indexData;
                }
            }
        }
        return null;
    }

    public final int index(Object data){
        List<T> list=null!=data?mData:null;
        return null!=list?list.indexOf(data):-1;
    }

    public final T get(Object data){
        List<T> list=null!=data?mData:null;
        if (null!=list){
            synchronized (list){
                int index= null!=list?list.indexOf(data):-1;
                return index>=0?list.get(index):null;
            }
        }
        return null;
    }

    public final boolean set(List<T> data,String debug){
        List<T> list=mData;
        int size=null!=data?data.size():0;
        if (size>0){
            list=null!=list?list:(mData=new ArrayList<>(size));
            int currentSize=list.size();
            if (currentSize > size){
                list.removeAll(list.subList(size,currentSize));
                notifyItemRangeRemoved(size, currentSize);
            }
            list.clear();
            list.addAll(data);
            notifyItemRangeChanged(0,size,"Set");
        }else if(null!=list){
            list.clear();
            mData=null;
            notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public final boolean add(T data){
        return add(data,null);
    }

    public final boolean add(T data,String debug){
        List<T> list=null!=data?new ArrayList<T>():null;
        return null!=list&&list.add(data)&&add(-1,list,debug);
    }

    public final boolean add(Collection<T> data,String debug) {
        return null!=data&&data.size()>0&&add(-1,data,debug);
    }

    public final boolean add(int index,T data,String debug) {
        if (null!=data){
            List<T> list=new ArrayList<T>(1);
            list.add(data);
            return add(index,list,debug);
        }
        return false;
    }

    public final boolean add(int index,Collection<T> data,String debug) {
        if (null!=data&&data.size()>0) {
            List<T> list = mData;
            list = null != list ? list : (mData = new ArrayList<>());
            synchronized (list) {
                int from=list.size();
                list.addAll(index>=0&&index<from?index:from,data);
                notifyItemRangeInserted(from,list.size()-from);
                return true;
            }
        }
        return false;
    }

    public final boolean replace(T data,String debug){
        List<T> list= null!=data?mData:null;
        int index=null!=list?list.indexOf(data):-1;
        return index>=0&&replace(index,data,debug);
    }

    public final boolean insert(int index,T data,String debug) {
        if (null!=data){
            List list=new ArrayList(1);
            list.add(data);
            return insert(index,list,true,debug);
        }
        return false;
    }

    public final boolean insert(int index,List<T> data,boolean exceptExist,String debug) {
        if (null!=data&&data.size()>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            synchronized (list){
                index=index<0||index>list.size()?list.size():index;
                for (T child:data) {
                    if (null==child||(list.contains(child)&&exceptExist)){
                        continue;
                    }
                    list.add(index,child);
                    notifyItemInserted(index++);
                }
            }
            return true;
        }
        return false;
    }

    public final boolean replace(int index,T data,String debug) {
        if (null!=data&&index>=0){
            ArrayList<T> list= new ArrayList<>(1);
            list.add(data);
            return replace(index,list,debug);
        }
        return false;
    }

    public final boolean replace(int from,ArrayList<T> data,String debug){
        int length=null!=data?data.size():-1;
        if (length>0){
            List<T> list=mData;
            list=null!=list?list:(mData=new ArrayList<>());
            if (null!=list){
                int size=list.size();
                from=from<0||from>size?size:from;
                synchronized (list){
                    for (int i = 0; i < length; i++) {
                        int index=i+from;
                        T child=data.get(i);
                        if (index<list.size()){
                            list.remove(list.get(index));
                            list.add(index,child);
                            notifyItemChanged(index,"Item replaced");
                        }else{
                            list.add(child);
                            notifyItemInserted(index);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final boolean remove(int from,int to,String debug){
        List<T> list=from>=0&&to>from?mData:null;
        if (null!=list){
            synchronized (list){
                int size=list.size();
                if (size>0&&from<size){
                    to=to>size?size:to;
                    list.removeAll(list.subList(from,to));
                    notifyItemRangeRemoved(from,to-from);
                    Debug.D("Remove items from "+from+" to "+to+" "+(null!=debug?debug:"."));
                    return true;
                }
            }
        }
        return false;
    }

    public final boolean isExist(Object data){
        return null!=data&&index(data)>=0;
    }

    protected void onViewAttachedToWindow(RecyclerView.ViewHolder holder, View view, ViewDataBinding binding){
        //Do nothing
    }

    @Override
    public final void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (null!=holder){
            View view=holder.itemView;
            ViewDataBinding binding=null!=view? DataBindingUtil.getBinding(view):null;
            onViewAttachedToWindow(holder,view,binding);
        }
    }

    protected void onViewDetachedFromWindow( RecyclerView.ViewHolder holder, View view, ViewDataBinding binding){
        //Do nothing
    }

    @Override
    public final void onViewDetachedFromWindow( RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (null!=holder){
            View view=holder.itemView;
            ViewDataBinding binding=null!=view? DataBindingUtil.getBinding(view):null;
            onViewDetachedFromWindow(holder,view,binding);
        }
    }

    protected void onAttachedRecyclerView(RecyclerView recyclerView) {
        //Do nothing
    }

    public final boolean updateVisibleItems(String debug){
        RecyclerView.LayoutManager manager=getLayoutManager();
        if (null!=manager){
            if (manager instanceof LinearLayoutManager){
                LinearLayoutManager lm=(LinearLayoutManager)manager;
                int first=lm.findFirstVisibleItemPosition();
                int last=lm.findLastVisibleItemPosition();
                Debug.D("Update visible linear items from "+first+" to "+last+" "+(null!=debug?debug:"."));
                return last>=first&&updateItems(first>1?--first:first,last+2);
            }else if (manager instanceof GridLayoutManager){
                GridLayoutManager glm=(GridLayoutManager)manager;
                int first=glm.findFirstVisibleItemPosition();
                int last=glm.findLastVisibleItemPosition();
                Debug.D("Update visible grid items from "+first+" to "+last+" "+(null!=debug?debug:"."));
                return last>=first&&updateItems(first>1?--first:first,last+2);
            }else if (manager instanceof StaggeredGridLayoutManager){
//                StaggeredGridLayoutManager glm=(StaggeredGridLayoutManager)manager;
//                int first=glm.fin();
//                int last=glm.findLastVisibleItemPosition();
//                return last>=first&&updateItems(first,last-first);
            }
        }
        return false;
    }

    public final boolean updateItems(int from,int to){
        if (to>from&&from>=0&&from<getDataCount()){
            notifyItemRangeChanged(from,to-from);
            return true;
        }
        return false;
    }

    public final  RecyclerView.LayoutManager getLayoutManager(){
        RecyclerView rv=getRecyclerView();
        return null!=rv?rv.getLayoutManager():null;
    }

    protected void onDetachedRecyclerView(RecyclerView recyclerView) {
        //Do nothing
    }

    public final Context getAdapterContext(){
        RecyclerView rv=getRecyclerView();
        return null!=rv?rv.getContext():null;
    }

    protected final boolean toast(Object text){
        Context context=null!=text?getAdapterContext():null;
        text=null!=context?text instanceof Integer?getText((Integer)text):text:null;
        if (null!=text&&text instanceof CharSequence){
            Toast.makeText(context,(CharSequence)text,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    protected final String getText(int textResId, Object ...args){
        Context context=getAdapterContext();
        return null!=context?context.getResources().getString(textResId,args):null;
    }

    public final RecyclerView getRecyclerView(){
        WeakReference<RecyclerView> reference = mRecyclerView;
        return null!=reference?reference.get():null;
    }

    @Override
    public RecyclerView.LayoutManager onResolveLayoutManager(RecyclerView rv) {
        return new LinearLayoutManager(rv.getContext(),RecyclerView.VERTICAL,false);
    }

    protected void onResolveFixedViewItem(RecyclerView recyclerView){
        //Do nothing
    }

    protected final LayoutInflater getLayoutInflater(Context context){
        context=null!=context?context:getAdapterContext();
        return null!=context?LayoutInflater.from(context):null;
    }

    protected final RecyclerView.ViewHolder generateViewHolder(Context context,Integer layoutId) {
        return generateViewHolder(context,null,layoutId,false);
    }

    protected final RecyclerView.ViewHolder generateViewHolder(Context context, ViewGroup parent,Integer layoutId,boolean attachToParent){
        if (null==layoutId||layoutId==0) {
            return null;
        }
        context=null==context&&null!=parent?parent.getContext():context;
        LayoutInflater inflater=null!=context?getLayoutInflater(context):null;
        try {
            if (null!=inflater) {
                ViewDataBinding binding=DataBindingUtil.inflate(inflater,layoutId,parent,attachToParent);
                if (null!=binding){
                    return new BindingViewHolder(binding);
                }else{//If not binding layout
                    View rootView=inflater.inflate(layoutId,parent,attachToParent);
                    return null!=rootView?new ViewHolder(rootView):null;
                }
            }
        }catch (Exception e){
            Debug.E("Exception create view holder."+layoutId+" "+e,e);
        }
        return null;
    }

    @Override
    public final void onAttachedToRecyclerView( RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        WeakReference<RecyclerView> view=mRecyclerView;
        if (null!=view){
            mRecyclerView=null;
            view.clear();
        }
        if (null!=recyclerView){
            mRecyclerView=new WeakReference<>(recyclerView);
            onResolveFixedViewItem(recyclerView);
        }
        RecyclerView.LayoutManager manager=null!=recyclerView?onResolveLayoutManager(recyclerView):null;
        if (null!=manager&&null!=recyclerView){
            recyclerView.setLayoutManager(manager);
        }
        Object object=this instanceof OnItemTouchResolver?((OnItemTouchResolver)this).onResolveItemTouch(recyclerView):null;
        if (null!=object&&object instanceof ItemTouchHelper){
            (mItemTouchHelper=((ItemTouchHelper)object)).attachToRecyclerView(recyclerView);
        }
        onAttachedRecyclerView(recyclerView);
    }

    @Override
    public final void onDetachedFromRecyclerView( RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        WeakReference<RecyclerView> view=mRecyclerView;
        if (null!=view){
            mRecyclerView=null;
            view.clear();
        }
        ItemTouchHelper itemTouchHelper=mItemTouchHelper;
        if (null!=itemTouchHelper){
            mItemTouchHelper=null;
            itemTouchHelper.attachToRecyclerView(null);
        }
        onDetachedRecyclerView(recyclerView);
        mFixHolder.clear();
    }
}
