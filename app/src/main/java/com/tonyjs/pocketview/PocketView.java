package com.tonyjs.pocketview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjs on 15. 1. 16..
 */
public class PocketView extends ViewGroup {

    public static final int DEFAULT_GAP = 48;

    public PocketView(Context context) {
        super(context);
        init();
    }

    public PocketView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PocketView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private int mGap;
    private void init() {
        mGap = (int) (getContext().getResources().getDisplayMetrics().density * DEFAULT_GAP);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int max = getChildCount();
        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    float mInterceptLastX = 0;
    float mInterceptLastY = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = super.onInterceptTouchEvent(ev);
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mInterceptLastX = ev.getX();
                mInterceptLastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float x = ev.getX();
                float y = ev.getY();

                float mathX = Math.abs(mInterceptLastX - x);
                float mathY = Math.abs(mInterceptLastY - y);

                intercept = mathY > mathX;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mInterceptLastX = 0;
                mInterceptLastY = 0;
                break;
        }
        return intercept;
    }

    float mLastY = 0;
    boolean mFirstTouch = true;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                mFirstTouch = true;
                break;

            case MotionEvent.ACTION_MOVE:
                float y = ev.getY();
                if (mFirstTouch) {
                    mLastY = y;
                }
                mFirstTouch = false;

                float distanceY = y - mLastY;
                scroll(distanceY);
                mLastY = y;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastY = 0;
                mFirstTouch = true;
                break;
        }

        return true;
    }

    private void scroll(float distanceY) {
        if (distanceY >= 0) {
            scrollDown(distanceY);
        } else {
            scrollUp(distanceY);
        }
    }

    private void scrollUp(float distanceY) {
        int max = getChildCount();
        if (max <= 0) {
            return;
        }

        int parentBottom = getHeight() - getPaddingBottom();
        View lastView = getChildAt(max - 1);
        if (lastView.getBottom() <= parentBottom) {
            return;
        }

        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            int maxBottom = parentBottom - (mGap * ((max - 1) - i));
            int newBottom = child.getBottom() + (int) distanceY;

            int bottom = Math.max(maxBottom, newBottom);

            int top = bottom - child.getHeight();
            child.layout(child.getLeft(), top, child.getRight(), top + child.getHeight());
        }
    }

    private void scrollDown(float distanceY) {
        int max = getChildCount();
        if (max <= 0) {
            return;
        }

        int parentTop = getPaddingTop();
        View firstView = getChildAt(0);
        if (firstView.getTop() >= parentTop) {
            return;
        }

        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            int minTop = parentTop + (mGap * i);
            int newTop = child.getTop() + (int) distanceY;

            int top = Math.min(minTop, newTop);
            child.layout(child.getLeft(), top, child.getRight(), top + child.getHeight());
        }
    }

    private BaseAdapter mAdapter;

    public void setAdapter(BaseAdapter adapter) {
        mAdapter = adapter;
        adaptView();
    }

    private void adaptView() {
        int max = getSize();
        if (max <= 0) {
            return;
        }

        int height = (int) (184 * getContext().getResources().getDisplayMetrics().density);
        for (int i = 0; i < max; i++) {
            View view = mAdapter.getView(i, null, this);
            view.setId(i);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, height);
            view.setLayoutParams(params);
            addView(view);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        l = l + getPaddingLeft();
        r = r - getPaddingRight();

        int max = getChildCount();
        if (max <= 0) {
            return;
        }

        for (int i = 0; i < max; i++) {
            int gap = mGap * i;
            View child = getChildAt(i);
            LayoutParams params = child.getLayoutParams();
            int height = params.height;
            int top = getPaddingTop() + gap;
            child.layout(l, top, r, top + height);
        }
    }

    private int getSize() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }

}
