package com.tonyjs.pocketview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.tonyjs.pocketview.model.Feed;
import com.tonyjs.pocketview.model.ImageResolution;
import com.tonyjs.pocketview.model.Images;
import com.tonyjs.pocketview.request.ApiInterface;
import com.tonyjs.pocketview.response.NewsFeedResponse;
import com.tonyjs.pocketview.widget.RoundedDrawable;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity
    implements PocketView.OnItemClickListener, PocketView.OnItemLongClickListener{

    private PocketView mPocketView;
    private PocketAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPocketView = (PocketView) findViewById(R.id.pocket_view);
        mPocketView.setOnItemClickListener(this);

        mPocketView.setOnItemLongClickListener(this);

        mAdapter = new PocketAdapter();
        mPocketView.setAdapter(mAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mAdapter.setItems(getFeeds());
    }

    final int[] sColors = new int[]{
            Color.rgb(160, 50, 50),
            Color.rgb(50, 160, 50),
            Color.rgb(160, 50, 160),
            Color.rgb(160, 100, 50),
            Color.rgb(160, 50, 100),
            Color.rgb(50, 160, 100),
            Color.rgb(50, 100, 160),
            Color.rgb(100, 160, 50),
            Color.rgb(100, 50, 160),
            Color.rgb(120, 200, 60),
            Color.rgb(200, 60, 120),
            Color.rgb(60, 120, 200),
            Color.rgb(120, 60, 200),
            Color.rgb(60, 200, 120),
            Color.rgb(200, 120, 60),
            Color.rgb(125, 10, 255),
            Color.rgb(125, 255, 10),
            Color.rgb(255, 10, 125),
            Color.rgb(70, 120, 100),
            Color.rgb(10, 60, 150)
    };

    private ArrayList<Feed> mFeeds;
    private ArrayList<Feed> getFeeds() {
        mFeeds = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Feed feed = new Feed();
            feed.setColor(sColors[i]);
            mFeeds.add(feed);
        }
        return mFeeds;
    }

    private Feed getFeed() {
        int position = (int) (Math.random() * mFeeds.size());
        return mFeeds.get(position);
    }

    @Override
    public void onItemClick(PocketView parent, View child, int position) {
        Feed item = mAdapter.getItem(position);
        Intent intent = new Intent(this, FuckingActivity.class);
        intent.putExtra("color", item.getColor());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(PocketView parent, View child, int position) {
        Toast.makeText(this, "LongClick ! position = " + position, Toast.LENGTH_SHORT).show();
        mAdapter.setEditMode(true);
    }

    private class PocketAdapter extends PocketViewAdapter<Feed>{

        private boolean mEditMode = false;
        public void setEditMode(boolean editMode) {
            mEditMode = editMode;
            notifyDataSetChanged();
        }

        public boolean isEditMode() {
            return mEditMode;
        }

        @Override
        public View getView(int position, ViewGroup parent) {
            View view = getLayoutInflater().inflate(
                    R.layout.item_pocket_with_image, parent, false);

            Feed item = getItem(position);
            int color = item != null ? item.getColor() : Color.TRANSPARENT;

            CardView cardView = (CardView) view.findViewById(R.id.layout_background);
            cardView.setCardBackgroundColor(color);

            View vDelete = view.findViewById(R.id.v_delete);
            vDelete.setVisibility(mEditMode ? View.VISIBLE : View.GONE);

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            mAdapter.addItem(getFeed());
            return true;
        }

        if (id == R.id.action_edit) {
            mAdapter.setEditMode(!mAdapter.isEditMode());
            return true;
        }

        if (id == R.id.action_remove) {
            mAdapter.removeItem(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
