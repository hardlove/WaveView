package com.chenlu.waveview;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class WaveView extends View {
    private int mWaveLength = 400;//波长
    private int mWaveCenterLineY = 400;//波的中心线距离顶部的距离
    private Paint mPaint = new Paint();
    private Path mPath;
    private int mWaveColor = Color.BLUE;
    private int mWaveHight =200;//振幅
    private int mWith;
    private int mHeight;
    private static final String TAG = "WaveView";
    private Region mRegion;
    private Bitmap mBitmap;
    private Region clip;

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
        postInvalidate();
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
        postInvalidate();
    }

    private int dx;
    private int dy;


    public WaveView(Context context) {
        super(context);
        initAttrs(null, 0);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs, 0);
        init();
    }


    public WaveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs, defStyle);
        init();

    }

    private void initAttrs(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WaveView, defStyle, 0);


        a.recycle();


    }

    private void init() {
        mPath = new Path();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(mWaveColor);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);


        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(10);



        startAnimation();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWith = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        mRegion = new Region();
        int centerX = mWith / 2;
        clip = new Region(centerX-1, 0, centerX, mHeight);


    }

    private Paint linePaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWave(canvas);
        drawBitmap(canvas);
//        canvas.drawLine(0, mWaveCenterLineY, mWith, mWaveCenterLineY, linePaint);
    }

    private void drawWave(Canvas canvas) {
        setPathData();
        canvas.drawPath(mPath, mPaint);

    }

    private void drawBitmap(Canvas canvas) {
        mRegion.setPath(mPath, clip);
        Rect bounds = mRegion.getBounds();

        int left = bounds.centerX() - mBitmap.getWidth() / 2;
        int top = bounds.top - mBitmap.getHeight() / 2;

        canvas.drawBitmap(mBitmap, left, top, mPaint);
    }

    private void setPathData() {
        mPath.reset();
        mPath.moveTo(-mWaveLength+dx, mWaveCenterLineY+dy);
        if (mWaveHight > mWaveCenterLineY) {
            mWaveHight = mWaveCenterLineY;
        }
        if (mWaveCenterLineY + mWaveHight > mHeight) {
            mWaveHight = mHeight - mWaveCenterLineY;
        }

        for (int i = 0; i < mWith / mWaveLength + 2; i++) {
            mPath.rQuadTo(mWaveLength / 4, 2 * mWaveHight, mWaveLength / 2, 0);
            mPath.rQuadTo(mWaveLength / 4, -2 * mWaveHight, mWaveLength / 2, 0);
        }
        mPath.lineTo(mWith,mHeight);
        mPath.lineTo(-mWaveLength, mHeight);
        mPath.close();



    }

    public void startAnimation() {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofInt("dx", 0, mWaveLength);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofInt("dy", 0, 2 * 0);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(this, pvhX, pvhY);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        animator.setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int dx = (int) animation.getAnimatedValue("dx");
                int dy = (int) animation.getAnimatedValue("dy");
                Log.e(TAG, "dx:" + dx + "   dy:" + dy);


            }
        });
        animator.start();

    }
}
