package com.file.downloadfile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.file.downloadfile.Utils.LogUtils;
import com.file.downloadfile.Utils.WeakHandler;
import com.file.downloadfile.database.model.DownloadFileInfo;
import com.file.downloadfile.download.FileDownload;
import com.file.downloadfile.listener.FileDownloadListener;


public class MainActivity extends Activity implements View.OnClickListener,FileDownloadListener {

    private static final String TAG = MainActivity.class.getSimpleName();

   /* private Button btnStopDownload;*/
    private TextView tvPercent;
    private ProgressBar pbPercent;
    private static final String DOWNLOAD_URL = "http://pa-package-object.oss-cn-beijing.aliyuncs.com/f101a283db8943478d6cdd3004725232";
    public static final int STATE_START_DOWNLOAD = 0; // 开始下载
    public static final int STATE_DOWNLOADING = 1;   //  下载中
    public static final int STATE_FINISH_DOWNLOAD = 2;  // 下载完成
    public static final int STATE_FAIL_DOWNLOAD = 3; // 下载失败
/*    private MyDownloadUtil myDownloadUtil;
    private DownloadBean downloadBean;*/
    private FileDownload fileDownload;
    private MyWeakHandler myWeakHandler;

    private static final class MyWeakHandler extends WeakHandler<MainActivity>{

        public MyWeakHandler(MainActivity mainActivity) {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWeakHandler = new MyWeakHandler(this);
        /*myDownloadUtil = new MyDownloadUtil(this);
        downloadBean = myDownloadUtil.getDownloadFileInfo();*/
        initView();
    }

    private void initView() {
        tvPercent = (TextView) findViewById(R.id.tv_percent);
        pbPercent = (ProgressBar) findViewById(R.id.pb_percent);
        findViewById(R.id.btn_start_download).setOnClickListener(this);
        findViewById(R.id.btn_stop_download).setOnClickListener(this);
        FileDownload.setDebugModel(true);
      /*  if (downloadBean == null) {
            downloadBean = new DownloadBean();
        }
        tvPercent.setText("下载进度：" + downloadBean.getDownloadProgress() + "%");
        if (downloadBean.getDownloadProgress() > 0) {
            pbPercent.setVisibility(View.VISIBLE);
        } else {
            pbPercent.setVisibility(View.GONE);
        }*/
    }


   /* private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            myDownloadUtil.startDownload(downloadBean, DOWNLOAD_URL, new MyDownloadUtil.DownloadFileStateListener() {
                @Override
                public void onStartDownload(int progress) {
                    Message message = handler.obtainMessage();
                    message.what = STATE_START_DOWNLOAD;
                    message.arg1 = progress;
                    handler.sendMessage(message);
                }

                @Override
                public void onDownloadProgress(int progress, String downloadSize) {
                    Message message = handler.obtainMessage();
                    message.what = STATE_DOWNLOADING;
                    message.arg1 = progress;
                    handler.sendMessage(message);
                }

                @Override
                public void onDownloadFinish() {
                    Message message = handler.obtainMessage();
                    message.what = STATE_FINISH_DOWNLOAD;
                    handler.sendMessage(message);
                }

                @Override
                public void onStopDownload() {

                }
            });
        }
    };
*/
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
       /* downloadBean.setIsStopDownloadFile(true);
        handler.removeCallbacks(runnable);*/
        fileDownload.stop();
    }

    /**
     * 开始下载文件
     */
    public void startDownloadFile() {
      /*  downloadBean.setIsStopDownloadFile(false);
        new Thread(runnable).start();*/
        fileDownload = new FileDownload();
        fileDownload.setFileDownloadListener(this);
        fileDownload.start(DOWNLOAD_URL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownloadFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFileDownloading(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloading="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_DOWNLOADING;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
    }

    @Override
    public void onFileDownloadFail(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloadFail="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_FAIL_DOWNLOAD;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
    }

    @Override
    public void onFileDownloadCompleted(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloadCompleted="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_FINISH_DOWNLOAD;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
    }

    @Override
    public void onFileDownloadPaused(DownloadFileInfo downloadFileInfo) {
        LogUtils.D(TAG, "onFileDownloadPaused="+downloadFileInfo.toString());
        Message message = myWeakHandler.obtainMessage();
        message.what = STATE_DOWNLOADING;
        message.arg1 = downloadFileInfo.getDownloadProgress();
        myWeakHandler.sendMessage(message);
    }

}
