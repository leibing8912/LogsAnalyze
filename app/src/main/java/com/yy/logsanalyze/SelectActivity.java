package com.yy.logsanalyze;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.List;
import java.util.Vector;

/**
 * 选择目录
 * Created by leibing 2019/7/3
 */
public class SelectActivity extends AppCompatActivity implements View.OnClickListener {
    // 要申请的权限
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,};
    // 列表视图
    private RecyclerView rcv_logs_select;
    // 加载转圈
    private ProgressBar pb_loading_select;
    // 提示
    private TextView tv_logs_tips;
    // 适配器
    private LogsSelectAdapter mAdapter;
    // 数据源
    private List<LogsSelectInfo> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        // 申请权限
        requestSomePermissions();
        // findView
        rcv_logs_select = findViewById(R.id.rcv_logs_select);
        pb_loading_select = findViewById(R.id.pb_loading_select);
        tv_logs_tips = findViewById(R.id.tv_logs_tips);
        // set click
        findViewById(R.id.btn_read_logs).setOnClickListener(this);
        // init adapter
        initAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read_logs:
                readLogs();
                break;
            default:
                break;
        }
    }

    private void requestSomePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[0]);
            int l = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[1]);
            int m = ContextCompat.checkSelfPermission(getApplicationContext(), permissions[2]);
            if (i != PackageManager.PERMISSION_GRANTED
                    || l != PackageManager.PERMISSION_GRANTED
                    || m != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    private void initAdapter() {
        LinearLayoutManager ms = new LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.VERTICAL);
        rcv_logs_select.setLayoutManager(ms);
        mAdapter = new LogsSelectAdapter(this);
        rcv_logs_select.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new LogsSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String data) {
                if (!TextUtils.isEmpty(data)) {
                    Intent mIntent = new Intent();
                    mIntent.setClass(SelectActivity.this, MainActivity.class);
                    mIntent.putExtra("fileName", data);
                    startActivity(mIntent);
                }
            }
        });
    }

    /**
     * 用户权限 申请 的回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //如果没有获取权限，那么可以提示用户去设置界面--->应用权限开启权限
                } else {
                    //获取权限成功提示，可以不要
                    toast("获取权限成功");
                }
            }
        }
    }

    private void toast(String msg) {
        Toast toast = Toast.makeText(SelectActivity.this, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void readLogs() {
        pb_loading_select.setVisibility(View.VISIBLE);
        LogsTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mData.clear();
                // 读取目录下的满足条件的文件
                Vector<String> mFileList = FileUtils.getTxtFileName("/sdcard/yymobile/logs");
                if (mFileList != null && mFileList.size() > 0) {
                    int size = mFileList.size();
                    LogsSelectInfo info = new LogsSelectInfo();
                    info.fileName = "全部";
                    mData.add(info);
                    for (int i = 0; i < size; i++) {
                        info = new LogsSelectInfo();
                        info.fileName = mFileList.get(i);
                        mData.add(info);
                    }
                    LogsTaskExecutor.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_logs_tips.setVisibility(View.VISIBLE);
                            pb_loading_select.setVisibility(View.GONE);
                            if (mAdapter != null) {
                                mAdapter.addAll(mData);
                            }
                        }
                    });
                } else {
                    LogsTaskExecutor.postToMainThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_logs_tips.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }
}
