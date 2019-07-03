package com.yy.logsanalyze;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 日志文件选择适配器
 * Created by leibing 2019/7/3
 */
public class LogsSelectAdapter extends BaseAdapter<LogsSelectInfo,
        LogsSelectAdapter.VHolderSelect> implements View.OnClickListener {
    private OnItemClickListener onItemClickListener;
    private RecyclerView recyclerView;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public LogsSelectAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(VHolderSelect holder, int position, LogsSelectInfo item) {
        // 更新ui
        holder.updateUi(item);
        // set click
        holder.setClick(this);
    }

    @NonNull
    @Override
    public VHolderSelect onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new VHolderSelect(LayoutInflater.from(mContext)
                .inflate(R.layout.layout_logs_select_item, parent, false), parent);
    }

    @Override
    public void onClick(View view) {
        // 根据RecyclerView获得当前View的位置
        int position = recyclerView.getChildAdapterPosition(view);
        // 程序执行到此，会去执行具体实现的onItemClick()方法
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(position, mDataList.get(position).fileName);
        }
    }

    /**
     * 将RecycleView附加到Adapter上
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    /**
     * 将RecycleView从Adapter解除
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public static class VHolderSelect extends BaseHolder {
        // 文件名称
        TextView fileNameTv;
        // 容器
        LinearLayout ly_container;

        public VHolderSelect(View itemView, ViewGroup mParent) {
            super(itemView, mParent);
            fileNameTv = itemView.findViewById(R.id.tv_filename);
            ly_container = itemView.findViewById(R.id.ly_container);
        }

        public void updateUi(LogsSelectInfo mInfo) {
            if (mInfo == null) {
                return;
            }
            if (!TextUtils.isEmpty(mInfo.fileName)) {
                fileNameTv.setText(mInfo.fileName);
            }
        }

        public void setClick(View.OnClickListener mListener) {
            ly_container.setOnClickListener(mListener);
        }
    }

    public interface OnItemClickListener {
        //参数（父组件，当前单击的View,单击的View的位置，数据）
        void onItemClick(int position, String data);
    }
}
