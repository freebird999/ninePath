package com.ustcinfo.nineunlockpathbyliaction.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.liaction.utils.LiactionCommonUtils;


/**
 * Created by CHEN.SI on 2015/4/8 0008.
 */
public class LiactionNinePathBySurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mSurfaceHolderLiaction;
    private Canvas mCanvasLiaction;

    public LiactionNinePathBySurfaceView(Context context) {
        this(context, null);
    }

    public LiactionNinePathBySurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiactionNinePathBySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LiactionCommonUtils.LiactionUtils.showTestLog("============");
        mSurfaceHolderLiaction = getHolder();
        mSurfaceHolderLiaction.addCallback(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mCanvasLiaction = mSurfaceHolderLiaction.lockCanvas(new Rect(0,0,230,230));
                mCanvasLiaction.drawColor(Color.BLUE);
                Paint _paintLiaction = new Paint();
                _paintLiaction.setColor(Color.YELLOW);
                mCanvasLiaction.drawLine(0,0,230,230,_paintLiaction);
                mSurfaceHolderLiaction.unlockCanvasAndPost(mCanvasLiaction);
            }
        }).start();

    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
