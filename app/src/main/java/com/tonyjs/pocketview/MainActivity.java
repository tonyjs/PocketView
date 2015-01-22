package com.tonyjs.pocketview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private PocketAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PocketView pocketView = (PocketView) findViewById(R.id.pocket_view);
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
//                Toast.makeText(getApplicationContext(), newsFeedResponse.toString(), Toast.LENGTH_SHORT).show();
                ArrayList<Feed> items = newsFeedResponse.getData();
//                ArrayList<Feed> items2 = new ArrayList<Feed>();
//                for (int i = 0; i < 5; i++) {
//                    items2.add(items.get(i));
//                }
                mAdapter.setItems(getFeeds(items));
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

//    final int[] sColors = new int[]{
//            Color.argb(160, 160, 50, 50),
//            Color.argb(160, 50, 160, 50),
//            Color.argb(160, 160, 50, 160),
//            Color.argb(160, 160, 100, 50),
//            Color.argb(160, 160, 50, 100),
//            Color.argb(160, 50, 160, 100),
//            Color.argb(160, 50, 100, 160),
//            Color.argb(160, 100, 160, 50),
//            Color.argb(160, 100, 50, 160),
//            Color.argb(160, 120, 200, 60),
//            Color.argb(160, 200, 60, 120),
//            Color.argb(160, 60, 120, 200),
//            Color.argb(160, 120, 60, 200),
//            Color.argb(160, 60, 200, 120),
//            Color.argb(160, 200, 120, 60)
//    };

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
            Color.rgb(200, 120, 60)
    };

    private ArrayList<Feed> mFeeds;
    private ArrayList<Feed> getFeeds(List<Feed> items) {
        mFeeds = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Feed feed = items.get(i);
            int color = (int) (Math.random() * sColors.length);
            feed.setColor(sColors[color]);
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
        public View getView(int position, ViewGroup parent) {
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

            ImageView ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
//            if (!TextUtils.isEmpty(url)) {
//                loadImage(ivThumb, url);
//            } else {
//                ivThumb.setImageDrawable(null);
//            }
            return view;
        }
    }

    private void loadImage(ImageView ivThumb, String url) {
        Glide.with(this).load(url)
                .asBitmap()
                .transform(new CircleTransform(this))
                .into(ivThumb);
    }

    public static class CircleTransform extends BitmapTransformation {
        private Context mContext;
        public CircleTransform(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            RoundedDrawable roundedDrawable = new RoundedDrawable(mContext, toTransform);
            return roundedDrawable.getBitmap();
        }

        @Override
        public String getId() {
            return "com.orcpark.hashtagram.circletransform";
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
