package com.yy.logsanalyze;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * 读取、统计、保存日志
 * Created by leibing 2019/7/2
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // 列表视图
    private RecyclerView rcv_logs_statistics;
    // 加载转圈
    private ProgressBar pb_loading;
    // 提示
    private TextView tv_logs_tips;
    // 统计保存路径
    private TextView tv_path_prompt;
    // 适配器
    private LogsStatisticsAdapter mAdapter;
    // 数据源
    private List<LogsStatisticsInfo> mData = new ArrayList<>();
    // 文件名
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取文件名
        fileName = getIntent().getStringExtra("fileName");
        // findView
        rcv_logs_statistics = findViewById(R.id.rcv_logs_statistics);
        tv_logs_tips = findViewById(R.id.tv_logs_tips);
        pb_loading = findViewById(R.id.pb_loading);
        tv_path_prompt = findViewById(R.id.tv_path_prompt);
        // set click
        findViewById(R.id.btn_save_statistics).setOnClickListener(this);
        // init adapter
        initAdapter();
        // 获取日志
        requestLogs();
    }

    private void requestLogs() {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        fileName = fileName.trim();
        if (fileName.equals("全部")) {
            pb_loading.setVisibility(View.VISIBLE);
            LogsTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mData.clear();
                    // 读取目录下的满足条件的文件
                    Vector<String> mFileList = FileUtils.getTxtFileName("/sdcard/yymobile/logs");
                    if (mFileList != null && mFileList.size() > 0) {
                        mData.addAll(FileUtils.getTagList(mFileList));
                        LogsTaskExecutor.postToMainThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mData != null && mData.size() >= 0) {
                                    pb_loading.setVisibility(View.GONE);
                                    tv_logs_tips.setVisibility(View.VISIBLE);
                                    if (mAdapter != null) {
                                        mAdapter.addAll(mData);
                                    }
                                } else {
                                    tv_logs_tips.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        } else {
            pb_loading.setVisibility(View.VISIBLE);
            LogsTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    mData.clear();
                    mData.addAll(FileUtils.getTagList(fileName));
                    LogsTaskExecutor.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mData != null && mData.size() >= 0) {
                                pb_loading.setVisibility(View.GONE);
                                tv_logs_tips.setVisibility(View.VISIBLE);
                                if (mAdapter != null) {
                                    mAdapter.addAll(mData);
                                }
                            } else {
                                tv_logs_tips.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            });
        }
    }

    private void initAdapter() {
        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_logs_statistics.setLayoutManager(ms);
        mAdapter = new LogsStatisticsAdapter(this);
        rcv_logs_statistics.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_statistics:
                savaStatistics();
                break;
            default:
                break;
        }
    }

    private void savaStatistics() {
        LogsTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mData == null || mData.size() == 0) {
                    LogsTaskExecutor.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_path_prompt.setVisibility(View.GONE);
                            toast("无统计数据！");
                        }
                    });
                    return;
                }
                Iterator<LogsStatisticsInfo> iterator = mData.iterator();
                StringBuilder statSb = new StringBuilder();
                while (iterator.hasNext()) {
                    LogsStatisticsInfo mInfo = iterator.next();
                    statSb.append("tag：");
                    statSb.append(mInfo.tag);
                    statSb.append("  出现");
                    statSb.append(mInfo.count);
                    statSb.append("次");
                    statSb.append("\n");
                }
                String prefix = fileName;
                if (!fileName.equals("全部")) {
                    prefix = fileName.substring(0, fileName.lastIndexOf(".txt"));
                }
                FileUtils.writeToSDCardFile("/sdcard/yymobile/stat/",
                        prefix + "_statistics.txt",
                        statSb.toString(), "UTF-8", false);
                LogsTaskExecutor.postToMainThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_path_prompt.setVisibility(View.VISIBLE);
                        toast("保存统计数据成功！");
                    }
                });
            }
        });
    }

    private void toast(String msg) {
        Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
