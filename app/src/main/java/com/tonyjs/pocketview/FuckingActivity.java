package com.tonyjs.pocketview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BaseTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

/**
 * Created by tonyjs on 15. 1. 22..
 */
public class FuckingActivity extends Activity {
    private ImageLoader mImageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout parent = new FrameLayout(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parent.setLayoutParams(params);
        Intent intent = getIntent();
        if (intent != null) {
            parent.setBackgroundColor(intent.getIntExtra("color", Color.BLACK));
        }
        addImageView(parent);
        setContentView(parent);
    }

    private void addImageView(ViewGroup parent) {
//        parent.setPadding(24, 0, 24, 0);
        mImageLoader = new ImageLoader(this);
        String url = "https://fbcdn-sphotos-a-a.akamaihd.net/hphotos-ak-xfa1/t31.0-8/10608282_1517889285154690_696429095883032512_o.jpg";
        final ImageView imageView = new ImageView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.leftMargin = 24;
        params.rightMargin = 24;
        params.topMargin = 24;
        params.bottomMargin = 24;
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);

//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        parent.addView(imageView);

//        mImageLoader.loadResizeBitmap(imageView, url, 500, 500);
//        mImageLoader.load(imageView, url);
        mImageLoader.load(imageView, url, ImageLoader.TransformationType.ROUNDED_CORNER);
    }

    private void addTextView(ViewGroup parent) {
        TextView tv = new TextView(this);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setText("Fuck You !");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        tv.setLayoutParams(params);
        parent.addView(tv);
    }
}
