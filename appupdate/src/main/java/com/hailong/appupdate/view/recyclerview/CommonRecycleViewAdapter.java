package com.hailong.appupdate.view.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * @author ZuoHailong
 * @date 2019/4/3.
 */
public abstract class CommonRecycleViewAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    private OnItemClickListener mItemClickListener;
    private onLongItemClickListener mLongItemClickListener;

    public CommonRecycleViewAdapter() {
    }

    public CommonRecycleViewAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = ViewHolder.get(mContext, parent, mLayoutId);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.updatePosition(position);
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        convert(holder, mDatas.get(position), position);
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(v, position);
                }
            });
        }
        if (mLongItemClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongItemClickListener.onLongItemClick(v, position);
                    return true;
                }
            });
        }
    }

    public abstract void convert(ViewHolder holder, T t, int position);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void update(List<T> datas) {
        mDatas = datas;
        notifyDataSetChanged();
    }

    public void updateIndex(int positon, T t) {
        mDatas.set(positon, t);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface onLongItemClickListener {
        void onLongItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public void setonLongItemClickListener(onLongItemClickListener listener) {
        this.mLongItemClickListener = listener;
    }

    public OnItemClickListener getmItemClickListener() {
        return mItemClickListener;
    }

    public onLongItemClickListener getmLongItemClickListener() {
        return mLongItemClickListener;
    }
}

