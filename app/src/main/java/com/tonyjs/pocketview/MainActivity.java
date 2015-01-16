package com.tonyjs.pocketview;

import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PocketView pocketView = (PocketView) findViewById(R.id.pocket_view);
        pocketView.setAdapter(new PocketAdapter());

    }

    private class PocketAdapter extends BaseAdapter{
        int[] colors = new int[]{
                Color.BLACK, Color.BLUE, Color.GRAY, Color.RED, Color.YELLOW,
                Color.rgb(03, 59, 21), Color.MAGENTA, Color.CYAN, Color.GREEN, Color.DKGRAY,
                Color.rgb(66, 254, 35), Color.rgb(99, 33, 44), Color.rgb(00, 69, 200),
                Color.rgb(87, 169, 70), Color.rgb(125, 125, 70), Color.rgb(12, 200, 243),
                Color.rgb(200, 185, 49), Color.rgb(34, 58, 254), Color.rgb(21, 194, 70),
                Color.rgb(168, 187, 250)};

        @Override
        public int getCount() {
            return colors.length;
        }

        @Override
        public Integer getItem(int position) {
            return colors[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int color = colors[position];

            String colorStr = "Hello Tony !";
            switch (position % getCount()) {
                case 0:
                    colorStr = "BLACK";
                    break;
                case 1:
                    colorStr = "BLUE";
                    break;
                case 2:
                    colorStr = "GRAY";
                    break;
                case 3:
                    colorStr = "RED";
                    break;
                case 4:
                    colorStr = "YELLOW";
                    break;
            }

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(
                        R.layout.item_pocket, parent, false);
                convertView.setBackgroundColor(color);
                Log.e("jsp", colorStr);

                TextView tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                tvTitle.setText(colorStr);
            }
            return convertView;
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
