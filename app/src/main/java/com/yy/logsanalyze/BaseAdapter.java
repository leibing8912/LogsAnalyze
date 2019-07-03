package com.yy.logsanalyze;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @author eastern
 * @date 24/02/2018
 */

public abstract class BaseAdapter<T, VH extends BaseHolder> extends RecyclerView.Adapter<VH> {

    protected List<T> mDataList;
    protected Context mContext;

    private SparseArray<View.OnClickListener> clickListenerSparseArray = new SparseArray<>();
    private SparseArray<View.OnLongClickListener> longClickListenerSparseArray = new SparseArray<>();

    public BaseAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, position, mDataList.get(position));
    }

    public void setOnLongClickListener(int id, View.OnLongClickListener onLongClickListener) {
        longClickListenerSparseArray.put(id, onLongClickListener);
    }

    public void setOnClickListener(int id, View.OnClickListener onClickListener) {
        clickListenerSparseArray.put(id, onClickListener);
    }

    protected View.OnClickListener getClickListener(int id) {
        return clickListenerSparseArray.get(id);
    }

    public abstract void onBindViewHolder(VH holder, int position, T item);

    public void addItem(T item) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.add(item);
        notifyItemChanged(mDataList.size() - 1);
    }

    public void addItem(T item, int position) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mDataList.add(position, item);
        notifyItemChanged(position);
    }

    public void addAll(List<T> list) {
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        } else {
            mDataList.clear();
        }
        mDataList.addAll(list);
        notifyDataSetChanged();
    }

    public void removeItem(T item) {
        if (mDataList == null) {
            return;
        }
        int position = mDataList.indexOf(item);
        if (position == -1 || position > mDataList.size()) {
            return;
        }
        mDataList.remove(item);
        notifyItemRemoved(position);
    }

    public void removeItem(int position) {
        if (mDataList == null) {
            return;
        }
        if (position == -1 || position > mDataList.size()) {
            return;
        }
        mDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if (mDataList == null) {
            return;
        }
        mDataList.clear();
        notifyDataSetChanged();
    }

    public int getTotalCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public T getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }
}
