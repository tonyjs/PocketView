package com.tonyjs.pocketview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by tonyjs on 15. 1. 16..
 */
public class PocketView extends ViewGroup
        implements PocketViewAdapter.DataSetObserver{

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
        setWillNotDraw(false);
        mGap = (int) (getContext().getResources().getDisplayMetrics().density * DEFAULT_GAP);
        mGestureDetector = new PocketGestureDetector(getContext(), new PocketGestureListener());
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getAdapter() == null || getAdapter().getCount() <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mGestureDetector.onTouchEvent(ev);
    }

    private void returnToOriginFromPullToDown() {
        if (mInPullToDown) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.animate()
                        .setInterpolator(null)
                        .setDuration(250)
                        .setStartDelay(0)
                        .translationY(0)
                        .setListener(null);
            }
            mInPullToDown = false;
        }
    }

    private void returnToOriginFromPullToUp() {
        if (mInPullToUp) {
            int max = getChildCount();
            View firstView = getChildAt(0);
            int maxHeight = firstView.getHeight() + ((max - 1) * mGap);
            int minTop = (getHeight() - getPaddingBottom() - getPaddingTop()) - maxHeight;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.animate()
                        .setInterpolator(null)
                        .setDuration(250)
                        .setStartDelay(0)
                        .translationY(minTop)
                        .setListener(null);
            }
            mInPullToUp = false;
        }
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

        returnToOriginFromPullToDown();

        View firstView = getChildAt(0);
        int maxHeight = firstView.getHeight() + ((max - 1) * mGap);
        int parentHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        if (maxHeight <= parentHeight) {
            return;
        }

        int minTop = (getHeight() - getPaddingBottom() - getPaddingTop()) - maxHeight;
        if (firstView.getTranslationY() <= minTop) {
            pullToUp(distanceY);
            return;
        }

        for (int i = 0; i < max; i++) {
            final View child = getChildAt(i);
            int childY = (int) child.getTranslationY();
            int newTop = childY + (int) distanceY;
            final int top = Math.max(minTop, newTop);
            child.setTranslationY(top);
        }
    }

    boolean mInPullToUp = false;
    private void pullToUp(float distanceY) {
        if (mInPullToUp) {
            return;
        }
        final int max = getChildCount();
        if (max <= 0) {
            return;
        }

        mInPullToUp = true;
        for (int i = max - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            int childY = (int) child.getTranslationY();
            int y = childY + ((int) distanceY * ((max) - i));
            child.animate()
                    .setDuration(100)
                    .setInterpolator(null)
                    .setStartDelay(0)
                    .translationY(y)
                    .setListener(null);
        }
    }

    private void scrollDown(float distanceY) {
        int max = getChildCount();
        if (max <= 0) {
            return;
        }

        returnToOriginFromPullToUp();

        View firstView = getChildAt(0);
        if (firstView.getTranslationY() >= 0) {
            pullToDown(distanceY);
            return;
        }

        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            int maxTop = 0;
            int childY = (int) child.getTranslationY();
            int newTop = childY + (int) distanceY;
            int top = Math.min(maxTop, newTop);
            child.setTranslationY(top);
        }
    }

    boolean mInPullToDown = false;
    private void pullToDown(float distanceY) {
        if (mInPullToDown) {
            return;
        }
        final int max = getChildCount();
        if (max <= 0) {
            return;
        }

        mInPullToDown = true;
        for (int i = 0; i < max; i++) {
            final View child = getChildAt(i);
            int y = (int) distanceY * (i + 1);

            child.animate()
                    .setDuration(100)
                    .setInterpolator(null)
                    .setStartDelay(0)
                    .translationYBy(y)
                    .setListener(null);
        }
    }

    private PocketViewAdapter mAdapter;
    public PocketViewAdapter getAdapter() {
        return mAdapter;
    }

    public void setAdapter(PocketViewAdapter adapter) {
        mAdapter = adapter;
        adapter.registerDataSetObserver(this);
        adaptView();
    }

    private void adaptView() {
        removeAllViews();

        int max = getItemCount();
        if (max <= 0) {
            return;
        }

        for (int i = 0; i < max; i++) {
            View view = mAdapter.getView(i, this);
            view.setId(i);
            addView(view);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int left = l + getPaddingLeft();
        final int right = r - getPaddingRight();

        int max = getChildCount();
        if (max <= 0) {
            return;
        }

        for (int i = 0; i < max; i++) {
            int gap = mGap * i;
            final View child = getChildAt(i);
            LayoutParams params = child.getLayoutParams();
            final int height = params.height;
            final int top = getPaddingTop() + gap;
            final int originTop = child.getTop();
            if (originTop <= 0) {
                child.setVisibility(View.INVISIBLE);
            }
            child.layout(left, top, right, top + height);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int max = getChildCount();
        if (max <= 0) {
            return;
        }

        if (mFirstLayout) {
            layoutWithAnimateAtFirst();
            return;
        }

        if (mAddedLayout) {
            layoutWithAnimation();
            return;
        }

        for (int i = 0; i < max; i++) {
            final View child = getChildAt(i);
            child.setVisibility(View.VISIBLE);
        }
    }

    private boolean mFirstLayout = true;
    private void layoutWithAnimateAtFirst() {
        final int max = getChildCount();
        for (int i = 0; i < max; i++) {
            final View child = getChildAt(i);
            final int index = i;
            int delay = 100 * i;
            if (child.getVisibility() != View.VISIBLE) {
                child.setTranslationY(getBottom());
                child.animate()
                        .setInterpolator(new DecelerateInterpolator())
                        .setStartDelay(delay)
                        .setDuration(250)
                        .translationY(0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                if (child == null) {
                                    return;
                                }
                                child.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (index == max - 1) {
                                    mFirstLayout = false;
                                }
                            }
                        });
            }
        }
    }

    private boolean mAddedLayout = false;
    private void layoutWithAnimation() {
        final int max = getChildCount();
        for (int i = 0; i < max; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == View.VISIBLE) {
                continue;
            }

            final int index = i;
            child.setTranslationX(getRight());
            child.animate()
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setStartDelay(0)
                    .setDuration(250)
                    .translationX(0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (child == null) {
                                return;
                            }
                            child.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            if (child == null) {
                                return;
                            }
                            child.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
//                            if (index == max - 1) {
                                mAddedLayout = false;
//                            }
                        }
                    });
        }
    }

    private int getItemCount() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }

    @Override
    public void notifyDataSetChanged() {
        adaptView();
    }

    @Override
    public void notifyItemAdded() {
        mAddedLayout = true;
        int position = 0;
        final View view = mAdapter.getView(position, this);
        view.setId(mAdapter.getItemId(position));
        addView(view, 0);
    }

    private boolean mInItemRemoved = false;
    @Override
    public void notifyItemRemoved(final int position) {
        int max = getItemCount();
        if (position >= max) {
            return;
        }

        if (mInItemRemoved) {
            return;
        }

        mInItemRemoved = true;
        final View target = getChildAt(position);
        target.animate()
                .setInterpolator(new AccelerateInterpolator())
                .setStartDelay(0)
                .setDuration(250)
                .translationX(getRight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        removeItem(position, target);
                    }

                    @Override
                    public void onAnimationPause(Animator animation) {
                        removeItem(position, target);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        removeItem(position, target);
                    }
                });
    }

    private void removeItem(int position, View target) {
        if (target == null) {
            return;
        }

        if (getChildCount() <= 0) {
            mAdapter.getItems().clear();
            mInItemRemoved = false;
            return;
        }

        removeView(target);
        mAdapter.getItems().remove(position);
        mInItemRemoved = false;
    }

    private class PocketGestureDetector extends GestureDetectorCompat{
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

    private class PocketGestureListener extends GestureDetector.SimpleOnGestureListener {

        public void dispatchSingleTapUpIfNeed(MotionEvent e) {
            if (getItemCount() > 0) {
                onSingleTapUp(e);
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            Log.d("jsp", "onSingleTapUp");
            returnToOriginFromPullToUp();
            returnToOriginFromPullToDown();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) >= Math.abs(distanceY)) {
                return false;
            }
            scroll(-distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) >= Math.abs(velocityY)) {
                return false;
            }
            float e1Y = e1.getY();
            float e2Y = e2.getY();
            float distanceY = e2Y - e1Y;

            fling(distanceY);
            return true;
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

    private void fling(float distanceY) {
        if (distanceY >= 0) {
            flingDown(distanceY * 1.5f);
        } else {
            flingUp(distanceY * 1.5f);
        }
    }

    private void flingUp(float distanceY) {
        final int max = getChildCount();
        if (max <= 0) {
            return;
        }

        View firstView = getChildAt(0);
        int maxHeight = firstView.getHeight() + ((max - 1) * mGap);
        int parentHeight = getHeight() - getPaddingBottom() - getPaddingTop();
        if (maxHeight <= parentHeight) {
            return;
        }

        int minTop = (getHeight() - getPaddingBottom() - getPaddingTop()) - maxHeight;
        if (firstView.getTranslationY() <= minTop) {
            pullToUp(distanceY);
            return;
        }

        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            int childY = (int) child.getTranslationY();
            int newTop = childY + (int) distanceY;
            final int top = Math.max(minTop, newTop);
            child.animate()
                    .setInterpolator(new DecelerateInterpolator())
                    .setStartDelay(0)
//                .setDuration((int) Math.abs(distanceY))
                    .setDuration(250)
                    .translationY(top)
                    .setListener(null);
        }
    }

    private void flingDown(float distanceY) {
        final int max = getChildCount();
        if (max <= 0) {
            return;
        }

        View firstView = getChildAt(0);
        if (firstView.getTranslationY() >= 0) {
            pullToDown(distanceY);
            return;
        }

        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);
            int maxTop = 0;
            int childY = (int) child.getTranslationY();
            int newTop = childY + (int) distanceY;
            int top = Math.min(maxTop, newTop);
            child.animate()
                    .setInterpolator(new DecelerateInterpolator())
                    .setStartDelay(0)
//                .setDuration((int) Math.abs(distanceY))
                    .setDuration(250)
                    .translationY(top)
                    .setListener(null);
        }
    }

}
