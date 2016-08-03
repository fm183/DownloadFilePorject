package com.file.downloadfile.DownloadUtil;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/1.
 */
public class MainActivity extends Activity implements Runnable{


private static final String FOLDER_PATH = "/downLoadTool/";
private Button mBtnStart;
private Button mBtnPause;
private Button mBtnResume;
private String downloadUrl = "http://pic23.nipic.com/20120819/6787991_103140683159_2.jpg";
private boolean mIsDownloading = false;
private HttpURLConnection mConnection;
private long mLoadedByteLength;
private String mFileName;// 下载的文件名
private String mFilePath;// 下载的文件所在的路径
private String mTempFilePath;
private File mTargetFile;
private long mTotalByteLength;
private FileOutputStream mOutputStream;
private ByteArrayOutputStream mByteOutput;
private int mMemoryCacheSize;
private static final int DEFAULT_MEMORY_CACHE_SIZE = 100;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
        .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //开始下载
//        mBtnStart = (Button) findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View arg0) {
        mIsDownloading = true;
        new Thread(MainActivity.this).start();
        }
        });
        //暂停下载
//        mBtnPause = (Button) findViewById(R.id.btn_pause);
        mBtnPause.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
                mIsDownloading = false;

                }

            });
        //继续下载
//        mBtnResume = (Button) findViewById(R.id.btn_resume);
        mBtnResume.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View arg0) {
            mIsDownloading = true;
            //取得断点以前保存的已经下载好的部分数据，然后继续下载
            getData();
            new Thread(MainActivity.this).start();
            }

            });
        }

@Override
public void run() {
        BufferedInputStream inputStream = null;
        try {
        URL url = new URL(downloadUrl);
        mConnection = (HttpURLConnection) url.openConnection();
        mConnection.setUseCaches(false);
        if (mLoadedByteLength > 0) {//有过断点
        mConnection.setRequestProperty("Range", "bytes="
        + mLoadedByteLength + "-");
        } else {//第一次下载（没有断点过）
        guessFileName();
        String folderPath = getFilePath().substring(0,
        getFilePath().lastIndexOf("/"));
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
        folder.mkdirs();
        } else {//如果已经存在下载的文件，则先删除这些文件
//					deleteFile(getFilePath());
//					deleteFile(getTempFilePath());
        String path=getFilePath();
        String fileName=getFilePath().substring(path.lastIndexOf("/")+1);
        Log.e("file", "file:" + fileName);
        deleteFile(fileName);

        String path1=getTempFilePath();
        String fileName1=getTempFilePath().substring(path.lastIndexOf("/")+1);
        Log.e("file1", "file1:"+fileName1);
        deleteFile(fileName1);
        }
        }
        mTargetFile = new File(getTempFilePath());
        mConnection.connect();

        int bufferSize = 1024;
        inputStream = new BufferedInputStream(mConnection.getInputStream(),
        bufferSize);//为InputStream类增加缓冲区功能
        if (mTotalByteLength == 0) {
        mTotalByteLength = mConnection.getContentLength();
        }
        if (!mTargetFile.exists()) {
        mTargetFile.createNewFile();
        }
        //写入中间文件
        mOutputStream = new FileOutputStream(mTargetFile, true);//true表示向打开的文件末尾追加数据
        mByteOutput = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int length = -1;
        while ((length = inputStream.read(buffer)) != -1 && mIsDownloading) {
        mByteOutput.write(buffer, 0, length);
        writeCache();
        // 通知handler去更新视图组件
        handler.sendEmptyMessage(0);
        Thread.sleep(1000);
        //保存已经下载好的部分数据
        saveData();
        }
        inputStream.close();
        mOutputStream.close();
        mByteOutput.close();
        } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        }

// 取得要下载的文件名
private void guessFileName() {
        if (mFileName != null) {
        return;
        }
        int lastIndexOfPathComponent = downloadUrl.lastIndexOf("/");
        String last = downloadUrl
        .substring(lastIndexOfPathComponent >= 0 ? lastIndexOfPathComponent + 1
        : 0);// 截取url字符串中要下载的文件名
        int loc = last.indexOf("?");// 不存在则返回-1
        if (loc >= 0) {
        last = last.substring(0, loc);
        }
        mFileName = last;
        }

        public String getFilePath() {
                if (mFilePath == null) {
                setFilePath(makePath(this, FOLDER_PATH, getFileName()));
                }
                return mFilePath;
                }

public String getFileName() {
        if (mFileName == null) {
        guessFileName();
        }
        return mFileName;
        }

public static String makePath(Context context, String folderPath,
        String fileName) {
        File file = null;
        if (android.os.Environment.getExternalStorageState().equals(
        android.os.Environment.MEDIA_MOUNTED)) {
        file = new File(
        android.os.Environment.getExternalStorageDirectory(),
        folderPath);
        } else {
        file = context.getApplicationContext().getCacheDir();
        }
        if (!file.exists() || !file.isDirectory()) {
        file.mkdirs();
        }
        StringBuilder absoluteFolderPath = new StringBuilder(
        file.getAbsolutePath());
        if (!absoluteFolderPath.toString().endsWith("/")) {
        absoluteFolderPath.append("/");
        }
        return absoluteFolderPath.append(fileName).toString();
        }

public void setFilePath(String filePath) {
        mFilePath = filePath;
        }

public String getTempFilePath() {
        if (mTempFilePath == null) {
        setTempFilePath(getFilePath() + ".dl");
        }
        return mTempFilePath;
        }

public void setTempFilePath(String tempFilePath) {
        mTempFilePath = tempFilePath;
        }

private synchronized void writeCache() {
        if (mByteOutput != null && mByteOutput.size() > 0
        && mOutputStream != null) {
        try {
        mByteOutput.writeTo(mOutputStream);
        mLoadedByteLength += mByteOutput.size();
        mByteOutput.reset();
        } catch (IOException e) {
        e.printStackTrace();
        }
        }

        if (mLoadedByteLength >= mTotalByteLength) {
        mTargetFile.renameTo(new File(getFilePath()));
        }
        }

public int getMemoryCacheSize() {
        return mMemoryCacheSize == 0 ? DEFAULT_MEMORY_CACHE_SIZE
        : mMemoryCacheSize;
        }

        //更新主界面
        Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
                // 当收到更新视图消息时，计算已完成下载百分比，同时更新进度条信息
                int progress = (Double
                .valueOf((mLoadedByteLength * 1.0 / mTotalByteLength * 100)))
                .intValue();
                if (progress == 100) {
                // downloadBt.setClickable(true);
                // progressMessage.setText("下载完成！");
                } else {
                // progressMessage.setText("当前进度:" + progress + "%");
                Log.e("progress", "当前进度:" + progress + "%");
                }
                // downloadProgressBar.setDownloadProgress(progress);
                }

                };

//保存数据
private void saveData(){
        JSONObject jsonObject = getJsonObject();
        //获得SharedPreferences对象
        SharedPreferences settings = this.getSharedPreferences("downloadTool", 0);
        //获得可编辑对象
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("downloadTool", jsonObject.toString());
        editor.commit();
        }

private void getData(){
        //取出已保存的数据
        String json = this.getSharedPreferences("downloadTool",
        Context.MODE_PRIVATE).getString("downloadTool", null);
        if (json != null) {
        try {
        JSONObject jsonObject = new JSONObject(json);
        downloadUrl = jsonObject.optString("targetURL");
        mLoadedByteLength = jsonObject.optLong("loadedByteLength");
        mTotalByteLength = jsonObject.optLong("totalByteLength");
        mMemoryCacheSize = jsonObject.optInt("memoryCacheSize");
        mFilePath = jsonObject.optString("filePath");
        mFileName = jsonObject.optString("fileName");
        } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        }
        }

        }

public JSONObject getJsonObject() {
        JSONObject jsonObj = new JSONObject();
        try {
        jsonObj.put("targetURL", downloadUrl);
        jsonObj.put("loadedByteLength", mLoadedByteLength);
        jsonObj.put("totalByteLength", mTotalByteLength);
        jsonObj.put("memoryCacheSize", getMemoryCacheSize());
        jsonObj.put("filePath", getFilePath());
        jsonObj.put("fileName", getFileName());
        } catch (JSONException e) {
        e.printStackTrace();
        return null;
        }
        return jsonObj;
        }
        }