package com.file.downloadfile;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.file.downloadfile.Utils.LogUtils;
import com.file.downloadfile.Utils.WeakHandler;
import com.file.downloadfile.database.model.DownloadFileInfo;
import com.file.downloadfile.download.FileDownload;
import com.file.downloadfile.listener.FileDownloadListener;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity implements View.OnClickListener,FileDownloadListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView tvPercent;
    private ProgressBar pbPercent;
    private static final String DOWNLOAD_URL = "http://pa-package-object.oss-cn-beijing.aliyuncs.com/f101a283db8943478d6cdd3004725232";
    public static final int STATE_START_DOWNLOAD = 0; // 开始下载
    public static final int STATE_DOWNLOADING = 1;   //  下载中
    public static final int STATE_FINISH_DOWNLOAD = 2;  // 下载完成
    public static final int STATE_FAIL_DOWNLOAD = 3; // 下载失败
    public static final int STATE_STOP_DOWNLOAD = 4;// 停止下载
    private FileDownload fileDownload;
    private MyWeakHandler myWeakHandler;
    private MyBroadcastReceiver myBroadcastReceiver;
    private int mStatus;

    private static final class MyWeakHandler extends WeakHandler<MainActivity>{

        private MyWeakHandler(MainActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        public void handleMessage(Message msg, MainActivity mainActivity) {
            if(mainActivity == null){
                return;
            }
            int progress = msg.arg1;
            switch (msg.what) {
                case STATE_START_DOWNLOAD:
                    mainActivity.tvPercent.setText("下载进度：" + progress + "%");
                    mainActivity.pbPercent.setVisibility(View.VISIBLE);
                    mainActivity.pbPercent.setProgress(progress);
                    break;
                case STATE_DOWNLOADING:

                    mainActivity.tvPercent.setText("下载进度：" + progress + "%");
                    mainActivity. pbPercent.setProgress(progress);
                    break;
                case STATE_FINISH_DOWNLOAD:
                    mainActivity.tvPercent.setText("已完成");
                    mainActivity.pbPercent.setVisibility(View.GONE);
                    break;
                case STATE_FAIL_DOWNLOAD:
                    String failReson = (String) msg.obj;
                    Toast.makeText(mainActivity,failReson,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private static final class MyBroadcastReceiver extends BroadcastReceiver{

        private WeakReference<MainActivity> weakReference;
        private MyBroadcastReceiver(MainActivity mainActivity) {
            weakReference = new WeakReference<>(mainActivity);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            MainActivity mainActivity = weakReference == null ? null : weakReference.get();
            if (mainActivity == null) {
                return;
            }
            if (CONNECTIVITY_SERVICE.equals(intent.getAction())) {
                ConnectivityManager connMgr = (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connMgr == null) {
                    return;
                }
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                boolean isConnected = networkInfo != null && networkInfo.isConnected();
                if (isConnected && mainActivity.mStatus == STATE_STOP_DOWNLOAD) {
                    mainActivity.startDownloadFile();
                }else if (!isConnected && mainActivity.mStatus == STATE_DOWNLOADING){
                    mainActivity.stopDownloadFile();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWeakHandler = new MyWeakHandler(this);
        initView();
        initBroadcastReceiver();
    }

    private void initView() {
        tvPercent = (TextView) findViewById(R.id.tv_percent);
        pbPercent = (ProgressBar) findViewById(R.id.pb_percent);
        findViewById(R.id.btn_start_download).setOnClickListener(this);
        findViewById(R.id.btn_stop_download).setOnClickListener(this);
        FileDownload.setDebugModel(true);
    }

    private void initBroadcastReceiver(){
        myBroadcastReceiver = new MyBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_SERVICE);
        registerReceiver(myBroadcastReceiver,intentFilter);
    }

    private void unregisterBroadcastReceiver(){
        if (myBroadcastReceiver != null) {
            unregisterReceiver(myBroadcastReceiver);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_download:
                startDownloadFile();
                break;
            case R.id.btn_stop_download:
                stopDownloadFile();
                break;
        }
    }

    /**
     * 停止下载文件
     */
    public void stopDownloadFile() {
        fileDownload.stop();
    }

    /**
     * 开始下载文件
     */
    public void startDownloadFile() {
        fileDownload = new FileDownload();
        fileDownload.setFileDownloadListener(this);
        fileDownload.start(DOWNLOAD_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownloadFile();
        unregisterBroadcastReceiver();
    }

    @Override
    public void onFileDownloading(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloading="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_DOWNLOADING;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
        mStatus = STATE_DOWNLOADING;
    }

    @Override
    public void onFileDownloadFail(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloadFail="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_FAIL_DOWNLOAD;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        message.obj = downloadFileInfo.getFailMessage();
        myWeakHandler.sendMessage(message);
        mStatus = STATE_FAIL_DOWNLOAD;
    }

    @Override
    public void onFileDownloadCompleted(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloadCompleted="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_FINISH_DOWNLOAD;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
        mStatus = STATE_FINISH_DOWNLOAD;

    }

    @Override
    public void onFileDownloadPaused(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloadPaused="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_DOWNLOADING;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
        mStatus = STATE_STOP_DOWNLOAD;
    }

}
