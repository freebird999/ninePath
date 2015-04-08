package com.ustcinfo.nineunlockpathbyliaction.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.liaction.utils.LiactionCommonUtils;

/**
 * Created by CHEN.SI LIACTION on 2015/4/5 0005.
 * <p/>
 * 图形解锁 View
 */

public class LiactionNinePath extends View {

    private static final int NINE = 9;
    private static int LIACTION_SCREEN_WIDTH;
    private static int LIACTION_SCREEN_HEIGHT;
    private static int LIACTION_SCREEN_SHORT;
    private static int LIACTION_CYCLE_R;
    private static Point[] mPointNine;
    private static NinePathCycle[] mNinePathCycleArray;
    private int mIntLiactionNextPoint = 23;//将要绘制的下一个点
    private int[] mPointHaveSave = new int[NINE];//最多允许画九个点作为手势密码,可以根据自己需要进行定制
    private Point mPointLiactionCurrent;//当前移动时的点的坐标
    private int[] mIntsLiactionClickPosition = new int[NINE];
    private StringBuilder mPwd  = new StringBuilder();
    private int mIntLiactionStart;
    /**
     * 画笔
     */
    private Paint mPaintLiaction;//nomal
    private Paint mPaintLiaction4Press;
    private Paint mPaintLiaction4Error;
    private Paint mPaintLiaction4Line;
    private Paint mPaintLiaction4LineError;


    public interface OnPwdChangeLister{
        public void pwdChange(String pwd);
    }

    private OnPwdChangeLister mOnpwdChangeLister;

    public void setOnpwdChangeLister(OnPwdChangeLister pOnpwdChangeLister) {
        mOnpwdChangeLister = pOnpwdChangeLister;
    }

    public LiactionNinePath(Context context) {
        this(context, null);
    }

    /**
     * @param context
     * @param attrs
     */
    public LiactionNinePath(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiactionNinePath(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LIACTION_SCREEN_WIDTH = LiactionCommonUtils.ScreenUtils.getScreenWidth(context);
        LIACTION_SCREEN_HEIGHT = LiactionCommonUtils.ScreenUtils.getScreenHeight(context);
        LIACTION_SCREEN_SHORT = LIACTION_SCREEN_HEIGHT >
                LIACTION_SCREEN_WIDTH ? LIACTION_SCREEN_WIDTH : LIACTION_SCREEN_HEIGHT;
        LIACTION_CYCLE_R = LIACTION_SCREEN_SHORT / 12;
        mPointNine = new Point[NINE];
        mPointNine[0] = new Point(LIACTION_CYCLE_R * 2, LIACTION_CYCLE_R * 2);
        mPointNine[1] = new Point(LIACTION_CYCLE_R * 6, LIACTION_CYCLE_R * 2);
        mPointNine[2] = new Point(LIACTION_CYCLE_R * 10, LIACTION_CYCLE_R * 2);

        mPointNine[3] = new Point(LIACTION_CYCLE_R * 2, LIACTION_CYCLE_R * 6);
        mPointNine[4] = new Point(LIACTION_CYCLE_R * 6, LIACTION_CYCLE_R * 6);
        mPointNine[5] = new Point(LIACTION_CYCLE_R * 10, LIACTION_CYCLE_R * 6);

        mPointNine[6] = new Point(LIACTION_CYCLE_R * 2, LIACTION_CYCLE_R * 10);
        mPointNine[7] = new Point(LIACTION_CYCLE_R * 6, LIACTION_CYCLE_R * 10);
        mPointNine[8] = new Point(LIACTION_CYCLE_R * 10, LIACTION_CYCLE_R * 10);

        mNinePathCycleArray = new NinePathCycle[9];


        mPointLiactionCurrent = new Point();

        resetNine();

        mPaintLiaction = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLiaction4Press = new Paint(Paint.ANTI_ALIAS_FLAG);//抗锯齿
        mPaintLiaction4Error = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLiaction4Line = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLiaction4LineError = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaintLiaction.setColor(Color.BLUE);
        mPaintLiaction4Press.setColor(Color.GREEN);
        mPaintLiaction4Error.setColor(Color.RED);
        mPaintLiaction4Line.setColor(Color.GREEN);
        mPaintLiaction4LineError.setColor(Color.BLUE);
        mPaintLiaction4Line.setStrokeWidth(23.0f);
        mPaintLiaction4LineError.setStrokeWidth(23.0f);

        LiactionCommonUtils.LiactionUtils.showTestLog("圆形半径为: " + LIACTION_CYCLE_R);
    }

    /**
     * 重置
     */
    private void resetNine() {
        for (int i = 0; i < mPointHaveSave.length; i++) {
            mIntsLiactionClickPosition[i] = mPointHaveSave[i] = 23;
            mNinePathCycleArray[i] = new NinePathCycle(mPointNine[i], NineState.NORMAL);
        }
        mPointLiactionCurrent.set(0, 0);
        mIntLiactionStart = 0;
    }

    /**
     * 图形绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mPointHaveSave.length; i++) {
            if (mNinePathCycleArray[i].getNineState() == NineState.NORMAL) {
                canvas.drawCircle(mPointNine[i].x, mPointNine[i].y, LIACTION_CYCLE_R, mPaintLiaction);
            } else if (mNinePathCycleArray[i].getNineState() == NineState.PRESSED) {
                canvas.drawCircle(mPointNine[i].x, mPointNine[i].y, LIACTION_CYCLE_R, mPaintLiaction4Press);
            } else if (mNinePathCycleArray[i].getNineState() == NineState.ERROR) {
                canvas.drawCircle(mPointNine[i].x, mPointNine[i].y, LIACTION_CYCLE_R, mPaintLiaction4Error);
            }
        }

        //获取手势密码
        mPwd.delete(0, mPwd.length());
        for (int i = 0; i < NINE; i++) {
            if (mIntsLiactionClickPosition[i] != 23) {
                mPwd.append(mIntsLiactionClickPosition[i]);
            }
        }

        //todo 记录点的顺序
        for (int i = 0; i < NINE - 1; i++) {


            if (mIntsLiactionClickPosition[i] != 23 && mIntsLiactionClickPosition[i + 1] != 23) {

                if (mNinePathCycleArray[i].getNineState() == NineState.ERROR) {
                    canvas.drawLine(
                            mPointNine[mIntsLiactionClickPosition[i]].x,
                            mPointNine[mIntsLiactionClickPosition[i]].y,
                            mPointNine[mIntsLiactionClickPosition[i + 1]].x,
                            mPointNine[mIntsLiactionClickPosition[i + 1]].y,
                            mPaintLiaction4LineError
                    );
                } else {
                    canvas.drawLine(
                            mPointNine[mIntsLiactionClickPosition[i]].x,
                            mPointNine[mIntsLiactionClickPosition[i]].y,
                            mPointNine[mIntsLiactionClickPosition[i + 1]].x,
                            mPointNine[mIntsLiactionClickPosition[i + 1]].y,
                            mPaintLiaction4Line
                    );
                }

            }
        }

        //todo 最后开始画线的点
        int tep = 28;
        for (int i = 0; i < NINE - 1; i++) {
            if (mIntsLiactionClickPosition[i] != 23 && mIntsLiactionClickPosition[i + 1] == 23) {
                tep = i;

                break;
            }
        }
        LiactionCommonUtils.LiactionUtils.showTestLog("tep = " + tep);

        if (tep != 28 && mPointLiactionCurrent.x != 0) {
            for (int i = 0; i < mPointHaveSave.length; i++) {
                if (mNinePathCycleArray[i].getNineState() == NineState.NORMAL ||
                        mNinePathCycleArray[i].getNineState() == NineState.PRESSED) {
                    canvas.drawLine(
                            mPointNine[mIntsLiactionClickPosition[tep]].x,
                            mPointNine[mIntsLiactionClickPosition[tep]].y,
                            mPointLiactionCurrent.x,
                            mPointLiactionCurrent.y,
                            mPaintLiaction4Line
                    );
                } else if (mNinePathCycleArray[i].getNineState() == NineState.ERROR) {
                    canvas.drawLine(
                            mPointNine[mIntsLiactionClickPosition[tep]].x,
                            mPointNine[mIntsLiactionClickPosition[tep]].y,
                            mPointLiactionCurrent.x,
                            mPointLiactionCurrent.y,
                            mPaintLiaction4LineError
                    );
                }
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthMeasureSpec = heightMeasureSpec = LIACTION_SCREEN_SHORT;
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mIntLiactionNextPoint = isOkMove(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (-1 != mIntLiactionNextPoint) {
                    mPointHaveSave[mIntLiactionNextPoint] = mIntLiactionNextPoint;
                    boolean _haveExist = false;
                    for (int i = 0; i < NINE; i++) {
                        if (mIntsLiactionClickPosition[i] == mIntLiactionNextPoint) {
                            _haveExist = true;
                            break;
                        }
                    }
                    if (!_haveExist) {
                        mIntsLiactionClickPosition[(mIntLiactionStart++) % NINE] = mIntLiactionNextPoint;
                    }
                    mNinePathCycleArray[mIntLiactionNextPoint].setNineState(NineState.PRESSED);
                }
                mPointLiactionCurrent.set((int) event.getX(), (int) event.getY());
                invalidate();//重绘
                break;
            case MotionEvent.ACTION_UP:
                LiactionCommonUtils.LiactionUtils.showTestLog("密码为 : " + mPwd.toString());
                mOnpwdChangeLister.pwdChange(mPwd.toString());
                resetNine();
                break;
        }


        return true;
    }

    /**
     * 判断点是否在有效范围内
     * <p/>
     * 并返还有效的圆的位置
     *
     * @param x
     * @param y
     * @return
     */
    private int isOkMove(float x, float y) {
        for (int i = 0; i < mPointNine.length; i++) {
            if (get2(x - mPointNine[i].x) + get2(y - mPointNine[i].y)
                    <= get2(LIACTION_CYCLE_R + 2)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 求平方
     *
     * @param a
     * @return
     */
    private double get2(double a) {
        return a * a;
    }

    enum NineState {
        NORMAL, PRESSED, ERROR;
    }

    private class NinePathCycle {
        private Point mPointLiaction;
        private NineState mNineStateLiaction;

        public NinePathCycle(Point pPointLiaction, NineState pNineStateLiaction) {
            mPointLiaction = pPointLiaction;
            mNineStateLiaction = pNineStateLiaction;
        }

        public Point getPoint() {
            return mPointLiaction;
        }

        public void setPoint(Point pPointLiaction) {
            mPointLiaction = pPointLiaction;
        }

        public NineState getNineState() {
            return mNineStateLiaction;
        }

        public void setNineState(NineState pNineStateLiaction) {
            mNineStateLiaction = pNineStateLiaction;
        }
    }
}
