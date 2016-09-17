package com.wanghaisheng.view.largeimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.InputStream;

/**
 * Author: sheng on 2016/9/16 20:03
 * Email: 1392100700@qq.com
 * 思路：利用BitmapRegionDecoder只显示巨图的一块指定的区域
 */
public class LargeImageView extends View {

    private BitmapRegionDecoder mRegionDecoder;
    //图片的宽和高
    private int mImageWidth;
    private int mImageHeight;

    //图片显示区域所在的矩形
    private volatile Rect mImageRect = new Rect();

    //多点触控
    private MoveGestureDetector mDetector;

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    static {
        options.inPreferredConfig = Bitmap.Config.RGB_565;
    }

    public LargeImageView(Context context) {
        super(context,null);
    }

    public LargeImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LargeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化多点触控检测
        mDetector = new MoveGestureDetector(getContext(),new MoveGestureDetector.SimpleMoveGestureDetector(){
            @Override
            public boolean onMove(MoveGestureDetector detector) {
                int moveX = (int) detector.getMoveX();
                int moveY = (int) detector.getMoveY();

                if(mImageWidth > getWidth()) {
                    mImageRect.offset(-moveX,0);
                    //检测横向的边界，防止出现白边
                    checkWidth();
                    invalidate();
                }

                if(mImageHeight > getHeight()) {
                    mImageRect.offset(0,-moveY);
                    //检测竖向的边界，防止出现白边
                    checkHeight();
                    invalidate();
                }

                return true;
            }
        });

        //设置触摸事件处理对象
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetector.onToucEvent(motionEvent);

                return true;
            }
        });
    }

    /**
     * 检测横向的边界
     */
    private void checkWidth() {
        Rect rect = mImageRect;
        int imageWidth = mImageWidth;

        if(rect.left < 0) {
            rect.left = 0;
            rect.right = getWidth();
        }

        if(rect.right > imageWidth) {
            rect.right = imageWidth;
            rect.left = imageWidth - getWidth();
        }
    }

    /**
     * 检测竖向的边界
     */
    private void checkHeight() {
        Rect rect = mImageRect;
        int imageHeight = mImageHeight;

        if(rect.top < 0) {
            rect.top = 0;
            rect.bottom = getHeight();
        }

        if(rect.bottom > imageHeight) {
            rect.bottom = imageHeight;
            rect.top = imageHeight - getHeight();
        }
    }

    /**
     *
     * @param in 图片的输入流
     */
    public void setImageInputStream(InputStream in) {

        try {
            mRegionDecoder = BitmapRegionDecoder.newInstance(in,false);

            //获取图片的宽和高
            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            tmpOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in,null,tmpOptions);
            mImageWidth = tmpOptions.outWidth;
            mImageHeight = tmpOptions.outHeight;

            requestLayout();
            invalidate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        int tempWidth = mImageWidth;
        int tempHeight = mImageHeight;

        //确定图片显示区域所在的矩形的位置,默认直接显示图片的中心区域
        mImageRect.left = tempWidth/2 - width/2;
        mImageRect.right = mImageRect.left + width;
        mImageRect.top = tempHeight/2 - height/2;
        mImageRect.bottom = mImageRect.top + height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //只画mImageRect所在的区域
        Bitmap bitmap = mRegionDecoder.decodeRegion(mImageRect,options);
        canvas.drawBitmap(bitmap,0,0,null);
    }
}
