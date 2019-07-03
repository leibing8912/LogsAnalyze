package com.yy.logsanalyze;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * file工具
 * Created by leibing 2019/7/2
 */
public class FileUtils {
    public final static String TAG = "FileUtils";

    /**
     * 获取目录下txt文件列表
     */
    public static Vector<String> getTxtFileName(String fileAbsolutePath) {
        Log.v(TAG, "#getTxtFileName fileAbsolutePath " + fileAbsolutePath);
        if (TextUtils.isEmpty(fileAbsolutePath)) {
            return null;
        }
        Vector<String> vecFile = new Vector<>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();
        if (subFile == null) {
            Log.v(TAG, "#getTxtFileName subFile is null");
            return null;
        }
        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为Txt结尾
                if (filename.trim().toLowerCase().endsWith(".txt")) {
                    Log.v(TAG, "#getTxtFileName filename = " + filename);
                    vecFile.add(filename);
                }
            }
        }
        return vecFile;
    }

    /**
     * 获取tag列表(全部)
     */
    public static List<LogsStatisticsInfo> getTagList(Vector<String> mFileList) {
        HashMap<String, Integer> fileMap = new HashMap();
        Set<String> mTagList = new TreeSet<>();
        int size = mFileList.size();
        if (mFileList != null && size > 0) {
            for (int i = 0; i < size; i++) {
                String fileName = mFileList.get(i);
                Log.v(TAG, "#readLogs fileName：" + fileName);
                File file = new File("/sdcard/yymobile/logs/" + fileName);
                if (!file.isDirectory()) {  // 检查此路径名的文件是否是一个目录(文件夹)
                    if (file.getName().endsWith("txt")) { // 文件格式为""文件
                        try {
                            InputStream instream = new FileInputStream(file);
                            if (instream != null) {
                                InputStreamReader inputreader
                                        = new InputStreamReader(instream, "UTF-8");
                                BufferedReader buffreader = new BufferedReader(inputreader);
                                String line;
                                // 分行读取
                                while ((line = buffreader.readLine()) != null) {
                                    if (!TextUtils.isEmpty(line.trim())) {
                                        int startIndex = line.indexOf("/") - 1;
                                        if (startIndex > 0) {
                                            int endIndex = line.indexOf(" ", startIndex);
                                            if (endIndex > 0) {
                                                String tag = line.substring(startIndex, endIndex);
                                                Log.v(TAG, "#getTagList tag = " + tag + " fileName：" + fileName);
                                                if (!TextUtils.isEmpty(tag)) {
                                                    if (fileMap.get(tag) == null) {
                                                        fileMap.put(tag, 0);
                                                    } else {
                                                        mTagList.add(tag);
                                                    }
                                                    fileMap.put(tag, fileMap.get(tag) + 1);
                                                }
                                            }
                                        }
                                    }
                                }
                                // 关闭输入流
                                instream.close();
                            }
                        } catch (java.io.FileNotFoundException e) {
                            Log.d(TAG, "#getTagList The File doesn't not exist.");
                        } catch (IOException e) {
                            Log.d(TAG, "#getTagList " + e.getMessage());
                        }
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        List<LogsStatisticsInfo> mData = new ArrayList<>();
        int mTagSize = mTagList.size();
        for (int i = 0; i < mTagSize; i++) {
            LogsStatisticsInfo mInfo = new LogsStatisticsInfo();
            mInfo.tag = ((TreeSet<String>) mTagList).pollFirst();
            mInfo.count = fileMap.get(mInfo.tag);
            mData.add(mInfo);
        }
        // 排序
        class LogsStatisticsInfoComparetor implements Comparator<LogsStatisticsInfo> {
            @Override
            public int compare(LogsStatisticsInfo h1, LogsStatisticsInfo h2) {
                return h2.count - h1.count;
            }
        }
        Collections.sort(mData, new LogsStatisticsInfoComparetor());

        return mData;
    }

    /**
     * 获取tag列表（单个）
     */
    public static List<LogsStatisticsInfo> getTagList(String fileName) {
        HashMap<String, Integer> fileMap = new HashMap();
        Set<String> mTagList = new TreeSet<>();
        Log.v(TAG, "#readLogs fileName：" + fileName);
        File file = new File("/sdcard/yymobile/logs/" + fileName);
        if (!file.isDirectory()) {  // 检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) { // 文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line;
                        // 分行读取
                        while ((line = buffreader.readLine()) != null) {
                            if (!TextUtils.isEmpty(line.trim())) {
                                int startIndex = line.indexOf("/") - 1;
                                if (startIndex > 0) {
                                    int endIndex = line.indexOf(" ", startIndex);
                                    if (endIndex > 0) {
                                        String tag = line.substring(startIndex, endIndex);
                                        Log.v(TAG, "#getTagList tag = " + tag + " fileName：" + fileName);
                                        if (!TextUtils.isEmpty(tag)) {
                                            if (fileMap.get(tag) == null) {
                                                fileMap.put(tag, 0);
                                            } else {
                                                mTagList.add(tag);
                                            }
                                            fileMap.put(tag, fileMap.get(tag) + 1);
                                        }
                                    }
                                }
                            }
                        }
                        // 关闭输入流
                        instream.close();
                    }
                } catch (java.io.FileNotFoundException e) {
                    Log.d(TAG, "#getTagList The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d(TAG, "#getTagList " + e.getMessage());
                }
            }
        }
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<LogsStatisticsInfo> mData = new ArrayList<>();
        int mTagSize = mTagList.size();
        for (int i = 0; i < mTagSize; i++) {
            LogsStatisticsInfo mInfo = new LogsStatisticsInfo();
            mInfo.tag = ((TreeSet<String>) mTagList).pollFirst();
            mInfo.count = fileMap.get(mInfo.tag);
            mData.add(mInfo);
        }
        // 排序
        class LogsStatisticsInfoComparetor implements Comparator<LogsStatisticsInfo> {
            @Override
            public int compare(LogsStatisticsInfo h1, LogsStatisticsInfo h2) {
                return h2.count - h1.count;
            }
        }
        Collections.sort(mData, new LogsStatisticsInfoComparetor());

        return mData;
    }

    /**
     * 往sd卡写入文件
     */
    public static void writeToSDCardFile(String directory, String fileName,
                                         String content, String encoding, boolean isAppend) {
        delFile(directory, fileName);
        // mobile SD card path +path
        File file = null;
        OutputStream os = null;
        try {
            createFile(directory, fileName);
            file = new File(directory + fileName);
            os = new FileOutputStream(file, isAppend);
            if (encoding.equals("")) {
                os.write(content.getBytes());
            } else {
                os.write(content.getBytes(encoding));
            }
            os.flush();
        } catch (IOException e) {
            Log.e(TAG, "#writeToSDCardFile:" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建文件
     */
    public static void createFile(String directory, String fileName) {
        boolean isDirExist = isFileExist(directory);
        if (!isDirExist) {
            File dirFile = new File(directory);
            dirFile.mkdir();
        }
        boolean isFileExist = isFileExist(directory + fileName);
        Log.v(TAG, "#createFile isFileExist = " + isFileExist);
        if (!isFileExist) {
            File file = new File(directory + fileName);
            try {
                boolean createNewFile = file.createNewFile();
                Log.v(TAG, "#createFile createNewFile = " + createNewFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否文件存在
     */
    public static boolean isFileExist(String director) {
        File file = new File(director);
        return file.exists();
    }

    /**
     * 删除文件
     */
    public static void delFile(String directory, String fileName) {
        File file = new File(directory + fileName);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
