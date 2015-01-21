package com.tonyjs.pocketview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjs on 15. 1. 19..
 */
public abstract class PocketViewAdapter<T> {
    public interface DataSetObserver {
        public void notifyDataSetChanged();

        public void notifyItemAdded();

        public void notifyItemRemoved(int position);
    }

    public PocketViewAdapter(){}

    private Context mContext;
    public PocketViewAdapter(Context context) {
        mContext = context;
    }

    public Context getConext() {
        return mContext;
    }

    private List<T> mItems = new ArrayList<>();
    public void setItems(ArrayList<T> items){
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public int getCount(){
        return mItems.size();
    }

    public int getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return mItems.size() > position ? mItems.get(position) : null;
    }

    // Basic - Add Item On Top
    public void addItem(T item) {
        mItems.add(0, item);
        notifyItemAdded();
    }

    public void addItems(ArrayList<T> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position < 0) {
            return;
        }

        notifyItemRemoved(position);
    }

    public List<T> getItems() {
        return mItems;
    }

    public abstract View getView(int position, ViewGroup parent);

    private DataSetObserver mObserver;
    public void registerDataSetObserver(DataSetObserver observer) {
        mObserver = observer;
    }

    public void notifyDataSetChanged() {
        if (mObserver == null) {
            return;
        }

        mObserver.notifyDataSetChanged();
    }

    public void notifyItemAdded() {
        if (mObserver == null) {
            return;
        }

        mObserver.notifyItemAdded();
    }

    public void notifyItemRemoved(int position) {
        if (mObserver == null) {
            return;
        }

        mObserver.notifyItemRemoved(position);
    }
}
