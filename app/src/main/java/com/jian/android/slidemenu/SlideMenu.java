package com.jian.android.slidemenu;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by dell on 2017/6/6.
 */

public class SlideMenu extends FrameLayout{

    private View menuView;    //菜单view
    private View mainView;    //主界面view

    private ViewDragHelper viewDragHelper;
    private int width;
    private float dragRange;   //水平方向拖拽范围

    private FloatEvaluator floatEvaluator;//float的计算器
    private IntEvaluator intEvaluator;//int的计算器

    public static final int STATE_OPEN=0;       //定义打开状态
    public static final int STATE_CLOSE=1;      //定义关闭状态
    private int currentState=STATE_CLOSE;        //当前状态

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化操作
     */
    public void init(){
        viewDragHelper=ViewDragHelper.create(this,callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //简单的异常处理
        if (getChildCount()!=2){
            throw new IllegalArgumentException("SlideMenu only has two children");
        }

        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }

    /**
     * 该方法在onMeasure方法执行完毕后执行，可以在该方法中初始化自己和子view的宽高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width = getMeasuredWidth();
        dragRange=width*0.6f;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback=new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child==mainView || child==menuView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {

            if (child==mainView){
                if (left<0) left=0;     //限制mainView的左边界
                if (left>dragRange) left= (int) dragRange;     //限制mainView的右边界
            }
            return left;
        }

        /**
         *伴随移动
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {

            if (changedView==menuView){
                //固定住menuView
                menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());

                //让mainView移动起来
                int newLeft=mainView.getLeft()+dx;
                if (newLeft<0) newLeft=0;     //限制mainView的左边界
                if (newLeft>dragRange) newLeft= (int) dragRange;     //限制mainView的右边界
                mainView.layout(newLeft,mainView.getTop(),newLeft+mainView.getMeasuredWidth(),mainView.getBottom());
            }

            //1.计算滑动的百分比
            float fraction=mainView.getLeft()/dragRange;
            //2.执行伴随动画
            executeAnim(fraction);
            //3.更改状态，回调listener方法
            if (fraction==0 && currentState!=STATE_CLOSE){
                //更改状态为关闭，并回调关闭的方法
                currentState=STATE_CLOSE;
                if (mListener!=null) mListener.onClose();
            }else if (fraction==1 && currentState!=STATE_OPEN){
                //更改状态为打开，并回调打开的方法
                currentState=STATE_OPEN;
                if (mListener!=null) mListener.onOpen();
            }

            if (mListener!=null){
                mListener.onDraging(fraction);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            if (mainView.getLeft()<dragRange/2){
                //关闭菜单栏
                close();
            }else {
                //打开菜单栏
                open();
            }

            //处理用户的稍微滑动
            if(xvel>200 && currentState!=STATE_OPEN){
                open();
            }else if (xvel<-200 && currentState!=STATE_CLOSE) {
                close();
            }
        }

    };

    /**
     * 关闭菜单栏
     */
    public void close(){
        viewDragHelper.smoothSlideViewTo(mainView,0,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 打开菜单栏
     */
    public void open(){
        viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange,mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void executeAnim(float fraction){

        //fraction:0-1
        //缩小mainView
        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction,1f,0.8f));
        //移动menuView
        ViewHelper.setTranslationX(menuView,intEvaluator.evaluate(fraction,-menuView.getMeasuredWidth()/2,0));
        //放大menuView
        ViewHelper.setScaleX(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        ViewHelper.setScaleY(menuView,floatEvaluator.evaluate(fraction,0.5f,1f));
        //改变menuView的透明度
        ViewHelper.setAlpha(menuView,floatEvaluator.evaluate(fraction,0.3f,1f));

        //给SlideMenu的背景添加黑色的遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);

    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    public int getCurrentState(){
        return currentState;
    }

    private OnDragStateChangeListener mListener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
        this.mListener=listener;
    }

    public interface OnDragStateChangeListener{

        /**
         * 打开的回调
         */
        void onOpen();

        /**
         * 关闭的回调
         */
        void onClose();

        /**
         * 正在拖拽中的回调
         * @param fraction
         */
        void onDraging(float fraction);
    }
}
