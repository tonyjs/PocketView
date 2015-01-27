package com.tonyjs.pocketview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
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
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

public class WithNetworkActivity extends ActionBarActivity {

    private PocketAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PocketView pocketView = (PocketView) findViewById(R.id.pocket_view);
        pocketView.setGap((int) (getResources().getDisplayMetrics().density * 70));
        pocketView.addScrollCallback(PocketView.getScrollCallback(findViewById(R.id.slip_target)));
        pocketView.setOnItemClickListener(new PocketView.OnItemClickListener() {
            @Override
            public void onItemClick(PocketView parent, View child, int position) {
                Toast.makeText(getApplicationContext(), "onItemClick ! position = " + position, Toast.LENGTH_SHORT).show();
            }
        });

        pocketView.setOnItemLongClickListener(new PocketView.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(PocketView parent, View child, int position) {
                Toast.makeText(getApplicationContext(), "LongClick ! position = " + position, Toast.LENGTH_SHORT).show();
                mAdapter.setEditMode(true);
            }
        });

        mAdapter = new PocketAdapter();
        pocketView.setAdapter(mAdapter);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ApiInterface.END_POINT)
                .build();

        ApiInterface apiInterface = restAdapter.create(ApiInterface.class);
        apiInterface.getItems(new Callback<NewsFeedResponse>() {
            @Override
            public void success(NewsFeedResponse newsFeedResponse, Response response) {
                ArrayList<Feed> items = newsFeedResponse.getData();
                mAdapter.setItems(getFeeds(items));
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mAdapter.isEditMode()) {
            mAdapter.setEditMode(false);
            return;
        }
        super.onBackPressed();
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
    private ArrayList<Feed> getFeeds(List<Feed> items) {
        mFeeds = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Feed feed = items.get(i);
            feed.setColor(sColors[i]);
            mFeeds.add(feed);
        }
        return mFeeds;
    }

    private Feed getFeed() {
        int position = (int) (Math.random() * mFeeds.size());
        return mFeeds.get(position);
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
        public View getView(final int position, ViewGroup parent) {
            Feed item = getItem(position);
            int color = item != null ? item.getColor() : Color.TRANSPARENT;
            Images images = item != null ? item.getImages() : null;
            ImageResolution standard = images != null ? images.getStandard() : null;

            String url = standard != null ? standard.getUrl() : null;

            View view = getLayoutInflater().inflate(
                    R.layout.item_pocket_with_image, parent, false);
            CardView layoutBackground = (CardView) view.findViewById(R.id.layout_background);
            layoutBackground.setCardBackgroundColor(color);

            View vDelete = view.findViewById(R.id.v_delete);
            vDelete.setVisibility(mEditMode ? View.VISIBLE : View.GONE);

            vDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PocketAdapter.this.removeItem(position);
                }
            });

            ImageView ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            if (!TextUtils.isEmpty(url)) {
                loadImage(ivThumb, url);
            } else {
                ivThumb.setImageDrawable(null);
            }
            return view;
        }
    }

    private ImageLoader mImageLoader;
    private void loadImage(ImageView ivThumb, String url) {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this);
        }

        mImageLoader.load(ivThumb, url, ImageLoader.TransformationType.ROUNDED_CORNER);
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
