package com.yy.logsanalyze;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 日志统计适配器
 * Created by leibing 2019/7/2
 */
public class LogsStatisticsAdapter extends BaseAdapter<LogsStatisticsInfo,
        LogsStatisticsAdapter.VHolderStatistics> {

    public LogsStatisticsAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(VHolderStatistics holder, int position, LogsStatisticsInfo item) {
        // 更新ui
        holder.updateUi(item);
    }

    @NonNull
    @Override
    public VHolderStatistics onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new VHolderStatistics(LayoutInflater.from(mContext)
                .inflate(R.layout.layout_logs_statistics_item, parent, false), parent);
    }

    public static class VHolderStatistics extends BaseHolder {
        // tag名称
        TextView tagTv;
        // 次数
        TextView countTv;

        public VHolderStatistics(View itemView, ViewGroup mParent) {
            super(itemView, mParent);
            tagTv = itemView.findViewById(R.id.tv_tag);
            countTv = itemView.findViewById(R.id.tv_count);
        }

        public void updateUi(LogsStatisticsInfo mInfo) {
            if (mInfo == null) {
                return;
            }
            if (!TextUtils.isEmpty(mInfo.tag)) {
                tagTv.setText("tag：" + mInfo.tag);
            }
            if (mInfo.count != 0) {
                countTv.setText("出现" + mInfo.count + "次");
            }
        }
    }
}
