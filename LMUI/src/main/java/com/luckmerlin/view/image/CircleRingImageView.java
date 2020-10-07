package com.luckmerlin.view.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.luckmerlin.databinding.BindingList;
import com.luckmerlin.databinding.BindingObject;
import com.luckmerlin.databinding.CustomBinding;

public class CircleRingImageView extends ImageView {
    private final Path mPath=new Path();
    private Paint mPaint;
    private float mRingWidth=0;
    private  int mRingAlpha=0;
    private int mRingDirection=5;
    private int mRingColor=Color.parseColor("#ffff0000");
    private boolean mEnableHeart=true;

    public CircleRingImageView(Context context) {
        super(context);
    }

    public CircleRingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleRingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final boolean setHeartRingColor(int color){
        mRingColor=color;
        postInvalidate();
        return true;
    }

    public final boolean setRingWidth(float ringWidth){
        if (mRingWidth!=ringWidth){
            mRingWidth=ringWidth;
            postInvalidate();
            return true;
        }
        return false;
    }

    public final boolean enableHeart(boolean enableHeart) {
        if (mEnableHeart!=enableHeart) {
            this.mEnableHeart = enableHeart;
            postInvalidate();
            return true;
        }
        return false;
    }

    public final static CustomBinding heartRingColorBinding(Object arg){
        return (view)-> null!=view&&view instanceof CircleRingImageView&&((CircleRingImageView)view).setHeartRingColor(null!=arg&&arg instanceof Integer?((Integer)arg):Color.TRANSPARENT);
    }

    public final static CustomBinding heartEnableBinding(boolean enable){
        return (view)-> null!=view&&view instanceof CircleRingImageView&&((CircleRingImageView)view).enableHeart(enable);
    }

    public final static BindingObject heartRingBinding(boolean enable,Object color){
        return new BindingList().append(true,heartEnableBinding(enable)).append(true,heartRingColorBinding(color));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        int circle=Math.min(height,width);
        setMeasuredDimension(circle,circle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path=null!=canvas?mPath:null;
        if (null!=path){
            float ringWidth=mRingWidth;
            ringWidth=ringWidth<=0?0:ringWidth;
            float widthHalf=canvas.getWidth()/2.f;
            float heightHalf=canvas.getHeight()/2.f;
            float radius=widthHalf>=heightHalf?heightHalf:widthHalf;
            if (radius>0){
                Paint paint=mPaint;
                paint=null!=paint?paint:(mPaint=new Paint(Paint.ANTI_ALIAS_FLAG));
                paint.setColor(Color.parseColor("#65000000"));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(widthHalf,heightHalf,radius-ringWidth/2,paint);
                if (ringWidth>0){
                    paint.setStrokeWidth(ringWidth);
                    paint.setStyle(Paint.Style.STROKE);
                    int ringColor=mRingColor;
                    path.reset();
                    path.addCircle(widthHalf,heightHalf,radius, Path.Direction.CCW);
                    int ringDirection=mRingDirection;
                    int ringAlpha=mRingAlpha;
                    int absRingDirection=Math.abs(ringDirection);
                    paint.setAlpha(1);//Reset
                    if (absRingDirection>0&&mEnableHeart&&getWindowAttachCount()>0&&getVisibility()==VISIBLE){
                        ringAlpha+=mRingDirection;
                        if (ringAlpha<55){
                            ringAlpha=55;
                            mRingDirection=absRingDirection;
                        }else if (ringAlpha>240){
                            ringAlpha=240;
                            mRingDirection=-absRingDirection;
                        }
                        paint.setAlpha(mRingAlpha=ringAlpha);
                        postInvalidateDelayed(90);
                    }
                    Shader mShader = new RadialGradient(widthHalf,heightHalf,radius,
                            new int[]{ringColor,Color.TRANSPARENT},new float[]{1-(ringWidth/radius),1f}, Shader.TileMode.CLAMP);
                    paint.setShader(mShader);
                    canvas.drawPath(path,paint);
                    paint.setAlpha(1);//Reset
                    paint.setShader(null);//Reset
                }
                path.reset();
                path.addCircle(widthHalf,heightHalf,radius, Path.Direction.CCW);
                canvas.clipPath(path);
            }
        }
        super.onDraw(canvas);
    }

}
