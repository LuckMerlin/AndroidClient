package com.luckmerlin.adapter.recycleview;

import android.view.MotionEvent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.luckmerlin.core.proguard.PublishMethods;


abstract class DragLoadMoreTouchListener implements RecyclerView.OnItemTouchListener, PublishMethods {
    private final int mInvalid=-1;
    private float mDownX=mInvalid,mDownY=mInvalid;
    private long mDownTime;

    protected abstract void onDragMore(RecyclerView recyclerView,boolean next,float shiftX,float shiftY);

    final void finishDrag(RecyclerView rv,float x, float y){
        float downX=mDownX;
        float downY=mDownY;
        long downTime=mDownTime;
        mDownX=mDownY=mInvalid;
        mDownTime=mInvalid;
        if ((downX>0||downY>0)){
            long shift=(System.currentTimeMillis()-downTime);
            if (shift>100&&shift<2000){
                checkLastFetch(rv,downX-x,downY-y);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        switch (null!=e?e.getAction():MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                finishDrag(rv,e.getX(),e.getY());
                mDownX=e.getX();
                mDownY=e.getY();
                mDownTime=System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                finishDrag(rv,e.getX(),e.getY());
                break;
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private void checkLastFetch(RecyclerView rv,float shiftX,float shiftY){
        RecyclerView.LayoutManager manager=null!=rv?rv.getLayoutManager():null;
        if (null!=manager){
            if (manager instanceof LinearLayoutManager){
                LinearLayoutManager lm=(LinearLayoutManager)manager;
                int first=lm.findFirstCompletelyVisibleItemPosition();
                int last=lm.findLastCompletelyVisibleItemPosition();
                int orientation=lm.getOrientation();
                if (first==last&&first==0){//Empty
                    onDragMore(rv,orientation==LinearLayoutManager.HORIZONTAL?shiftX>0:shiftY>0,shiftX,shiftY);
                }else if (first==0){//Top
                    switch (orientation){
                        case LinearLayoutManager.VERTICAL:
                            if (shiftY<0){
                                onDragMore(rv,false,shiftX,shiftY);
                            }
                            break;
                        case LinearLayoutManager.HORIZONTAL:
                            if (shiftX<0){
                                onDragMore(rv,false,shiftX,shiftY);
                            }
                            break;
                    }
                }else if (shiftY>0&&last==lm.getItemCount()-1){//Bottom
                    switch (lm.getOrientation()){
                        case LinearLayoutManager.VERTICAL:
                            if (shiftY>0){
                                onDragMore(rv,true,shiftX,shiftY);
                            }
                            break;
                        case LinearLayoutManager.HORIZONTAL:
                            if (shiftX>0){
                                onDragMore(rv,true,shiftX,shiftY);
                            }
                            break;
                    }
                }
            }else if (manager instanceof StaggeredGridLayoutManager){
//                ((StaggeredGridLayoutManager)manager).f
            }
        }
//        return resultXY;
    }
}
