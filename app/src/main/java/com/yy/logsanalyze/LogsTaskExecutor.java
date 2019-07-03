package com.yy.logsanalyze;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池封装
 * Created by leibing 2019/7/2
 */
public class LogsTaskExecutor {
    // 核心线程池数
    private static final int CORE_SIZE = RuntimeCompat.calculateBestThreadCount();
    // 线程池服务
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(CORE_SIZE);
    // main handler
    private static Handler mainHandler = new Handler(Looper.getMainLooper());

    public static void execute(final Runnable task) {
        mExecutorService.execute(task);
    }

    public static void postToMainThread(final Runnable task) {
        mainHandler.post(task);
    }
}
