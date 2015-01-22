package com.tonyjs.pocketview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by tonyjs on 15. 1. 22..
 */
public class FuckingActivity extends Activity {
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

        TextView tv = new TextView(this);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setText("Fuck You !");
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.gravity = Gravity.CENTER;
        tv.setLayoutParams(textParams);

        parent.addView(tv);

        setContentView(parent);
    }
}
