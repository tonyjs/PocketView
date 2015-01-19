package com.tonyjs.pocketview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

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

    private void returnToOriginPosition() {
        if (mInPullToUp) {
            int max = getChildCount();
            View firstView = getChildAt(0);
            int maxHeight = firstView.getHeight() + ((max - 1) * mGap);
            int minTop = (getHeight() - getPaddingBottom() - getPaddingTop()) - maxHeight;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.animate()
                        .translationY(minTop);
            }
            mInPullToUp = false;
        }
        if (mInPullToDown) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.animate()
                        .translationY(0);
            }
            mInPullToDown = false;
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
                    .translationY(y);
        }
    }

    private void scrollDown(float distanceY) {
        int max = getChildCount();
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
                    .translationYBy(y);
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
//        adapter.registerDataSetObserver(new DataSetObserver() {
//            @Override
//            public void onChanged() {
//                adaptView();
//            }
//        });
    }

    private void adaptView() {
        removeAllViews();

        int max = getSize();
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
            child.layout(left, top, right, top + height);
        }
    }

    private int getSize() {
        return mAdapter != null ? mAdapter.getCount() : 0;
    }

    @Override
    public void notifyDataSetChanged() {
        setDefaultAddLayoutAnimation();
        adaptView();
    }

    @Override
    public void notifyItemAdded() {
        int max = getSize();
        if (max <= 0) {
            return;
        }

        final int top = getPaddingTop() + (mGap * (getSize() - 1));

        int position = (getSize() - 1);
        final View view = mAdapter.getView(position, this);
        view.setId(mAdapter.getItemId(position));
        addView(view);
        view.layout(getPaddingLeft(), top, getRight(), top + view.getHeight());
//        view.setTranslationY(getBottom());
//        view.animate()
//                .setInterpolator(new DecelerateInterpolator())
//                .setDuration(250)
//                .translationYBy(0)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                    }
//                });
    }

    @Override
    public void notifyItemRemoved(int position) {
        Log.e("jsp", "position = " + position);
        int max = getSize() + 1;
        Log.e("jsp", "max = " + max);
        if (max > position) {
            final View target = getChildAt(position);
            target.animate()
                    .setInterpolator(new AccelerateInterpolator())
                    .translationX(getRight())
                    .setDuration(250)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            Log.e("jsp", "onAnimationEnd");
                            removeView(target);
                            postInvalidate();
                        }
                    });
        }
    }

    private void setDefaultAddLayoutAnimation() {
        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        animation.setDuration(250);

        LayoutAnimationController controller = new LayoutAnimationController(animation, 0.5f);
        setLayoutAnimation(controller);
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
            Log.d("jsp", "onSingleTapUp");
            returnToOriginPosition();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
//            Log.d("jsp", "onLongPress");
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

    private void fling(float distanceY) {
//        Log.e("jsp", "fling = " + distanceY);
        if (distanceY >= 0) {
            flingDown(distanceY * 2);
        } else {
            flingUp(distanceY * 2);
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
                    .setDuration((int) Math.abs(distanceY))
                    .translationY(top);
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
                    .setDuration((int) Math.abs(distanceY))
                    .translationY(top);
        }
    }

}
