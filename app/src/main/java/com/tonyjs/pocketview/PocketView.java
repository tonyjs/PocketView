package com.tonyjs.pocketview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.Scroller;

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

    private PocketGestureDetector mGestureDetector;
    private int mGap;
    private void init() {
        mGap = (int) (getContext().getResources().getDisplayMetrics().density * DEFAULT_GAP);
        mGestureDetector = new PocketGestureDetector(getContext(), new PocketGestureListener());
        mScroller = new Scroller(getContext());
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
    boolean mDragging = false;
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getY();
                mFirstTouch = true;
                mDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                mDragging = true;
                float y = ev.getY();
                if (mFirstTouch) {
                    mLastY = y;
                }
                mFirstTouch = false;

                float distanceY = y - mLastY;
//                scroll(distanceY);
                mLastY = y;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mLastY = 0;
                mFirstTouch = true;
                mDragging = false;
                break;
        }
        mGestureDetector.onTouchEvent(ev);
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

        for (int i = 0; i < max; i++) {
            View view = mAdapter.getView(i, null, this);
            view.setId(i);
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

    class PocketGestureDetector extends GestureDetectorCompat{
        private PocketGestureListener mListener;
        public PocketGestureDetector(Context context, PocketGestureListener listener) {
            super(context, listener);
            mListener = listener;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean handled = super.onTouchEvent(event);
            int action = event.getAction() & MotionEventCompat.ACTION_MASK;
            if (action == MotionEvent.ACTION_UP) {
                mListener.dispatchSingleTapUpIfNeed(event);
            }
            return handled;
        }

    }

    class PocketGestureListener extends GestureDetector.SimpleOnGestureListener {

        public void dispatchSingleTapUpIfNeed(MotionEvent e) {
            if (getSize() > 0) {
                onSingleTapUp(e);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.d("jsp", "onSingleTapUp");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
//            Log.d("jsp", "onLongPress");
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scroll(distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float e1Y = e1.getY();
            float e2Y = e2.getY();
            float distanceY = e2Y - e1Y;
            Log.e("jsp", "mScroller.getCurrY() - " + mScroller.getCurrY());
//            mScroller.startScroll(0, mScroller.getCurrY(), 0, -(int) distanceY, 250);
//            mScroller.computeScrollOffset();
//            distanceY = (distanceY / 2);
//            Log.d("jsp", "onFling - " + distanceY);
//            scroll(-distanceY);
//            smoothScroll(distanceY);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
//            Log.d("jsp", "onShowPress");
            super.onShowPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
//            Log.d("jsp", "onDown");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//            Log.d("jsp", "onSingleTapConfirmed");
            return true;
        }
    }

    private Scroller mScroller;

    private void smoothScroll(float distanceY) {
        if (distanceY >= 0) {

            smoothScrollDown(distanceY);
        } else {
            smoothScrollUp(distanceY);
        }
    }

    private void smoothScrollDown(float distanceY) {
        float y = distanceY;
        while (y > 0) {
            scrollDown(y);
            y /= 2;
        }
    }

    private void smoothScrollUp(float distanceY) {
        float y = distanceY;
        while (y < 0) {
            scrollUp(y);
            y /= 2;
        }
    }

}
