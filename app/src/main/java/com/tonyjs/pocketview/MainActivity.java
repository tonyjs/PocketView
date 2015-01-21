package com.tonyjs.pocketview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
                ArrayList<Feed> items2 = new ArrayList<Feed>();
                for (int i = 0; i < 5; i++) {
                    items2.add(items.get(i));
                }
                mAdapter.setItems(items2);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
            int color = Color.BLACK;
            switch (position % 4) {
                case 1:
                    color = Color.BLUE;
                    break;
                case 2:
                    color = Color.GRAY;
                    break;
                case 3:
                    color = Color.RED;
                    break;
            }

            Feed item = getItem(position);
            Images images = item != null ? item.getImages() : null;
            ImageResolution standard = images != null ? images.getStandard() : null;

            String url = standard != null ? standard.getUrl() : null;

            View view = getLayoutInflater().inflate(
                    R.layout.item_pocket_with_image, parent, false);
            CardView layoutBackground = (CardView) view.findViewById(R.id.layout_background);
//            layoutBackground.setCardBackgroundColor(color);

            View vDelete = view.findViewById(R.id.v_delete);
            vDelete.setVisibility(mEditMode ? View.VISIBLE : View.GONE);
//                Log.d("jsp", "layoutBackground.getCardElevation() - " + layoutBackground.getCardElevation());
//                layoutBackground.setCardElevation(15);
//                layoutBackground.setBackgroundColor(color);
                ImageView ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
                if (!TextUtils.isEmpty(url)) {
                    loadImage(ivThumb, url);
                } else {
                    ivThumb.setImageDrawable(null);
                }
            return view;
        }

//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            int color = Color.BLACK;
//            switch (position % 4) {
//                case 1:
//                    color = Color.BLUE;
//                    break;
//                case 2:
//                    color = Color.GRAY;
//                    break;
//                case 3:
//                    color = Color.RED;
//                    break;
//            }
//
//            Feed item = getItem(position);
//            Images images = item.getImages();
//            ImageResolution standard = images != null ?
//                    images.getStandard() : null;
//
//            String url = standard != null ? standard.getUrl() : null;
//
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(
//                        R.layout.item_pocket_with_image, parent, false);
//                CardView layoutBackground = (CardView) convertView.findViewById(R.id.layout_background);
//                layoutBackground.setCardBackgroundColor(color);
////                Log.d("jsp", "layoutBackground.getCardElevation() - " + layoutBackground.getCardElevation());
////                layoutBackground.setCardElevation(15);
////                layoutBackground.setBackgroundColor(color);
////                ImageView ivThumb = (ImageView) convertView.findViewById(R.id.iv_thumb);
////                if (!TextUtils.isEmpty(url)) {
////                    loadImage(ivThumb, url);
////                } else {
////                    ivThumb.setImageDrawable(null);
////                }
//            }
//            return convertView;
//        }
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
//    private class PocketAdapter extends BaseAdapter{
//        int[] colors = new int[]{
//                Color.BLACK, Color.BLUE, Color.GRAY, Color.RED, Color.YELLOW,
//                Color.rgb(3, 59, 21), Color.MAGENTA, Color.CYAN, Color.GREEN, Color.DKGRAY,
//                Color.rgb(135, 138, 9), Color.rgb(99, 33, 44), Color.rgb(0, 69, 200),
//                Color.rgb(87, 169, 70), Color.rgb(125, 125, 70), Color.rgb(12, 200, 243),
//                Color.rgb(200, 185, 49), Color.rgb(34, 58, 254), Color.rgb(21, 194, 70),
//                Color.rgb(168, 187, 250)};
//
//        @Override
//        public int getCount() {
//            return colors.length;
//        }
//
//        @Override
//        public Integer getItem(int position) {
//            return colors[position];
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            int color = colors[position];
//
//            String colorStr = "Hello Tony !";
//            switch (position % getCount()) {
//                case 0:
//                    colorStr = "BLACK";
//                    break;
//                case 1:
//                    colorStr = "BLUE";
//                    break;
//                case 2:
//                    colorStr = "GRAY";
//                    break;
//                case 3:
//                    colorStr = "RED";
//                    break;
//                case 4:
//                    colorStr = "YELLOW";
//                    break;
//            }
//
//            if (convertView == null) {
//                convertView = getLayoutInflater().inflate(
//                        R.layout.item_pocket, parent, false);
//                convertView.setBackgroundColor(color);
//                Log.e("jsp", colorStr);
//
//                TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
//                tvTitle.setText(colorStr);
//            }
//            return convertView;
//        }
//    }

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
            mAdapter.addItem(mAdapter.getItem(0));
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
