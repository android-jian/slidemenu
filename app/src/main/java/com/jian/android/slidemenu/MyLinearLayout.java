package com.jian.android.slidemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by dell on 2017/6/6.
 * 当slidemenu打开的时候，拦截并消费掉触摸事件
 */

public class MyLinearLayout extends LinearLayout{

    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlideMenu mSlideMenu;

    public void setSlideMenu(SlideMenu slideMenu){
        this.mSlideMenu=slideMenu;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mSlideMenu!=null && mSlideMenu.getCurrentState()==SlideMenu.STATE_OPEN){     //当slidemenu打开则应拦截并消费掉事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mSlideMenu!=null && mSlideMenu.getCurrentState()==SlideMenu.STATE_OPEN){     //当slidemenu打开则应拦截并消费掉事件

            if (event.getAction()==MotionEvent.ACTION_UP){
                mSlideMenu.close();
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
