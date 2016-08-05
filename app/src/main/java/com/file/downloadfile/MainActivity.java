package com.file.downloadfile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.file.downloadfile.DownloadUtil.MyDownloadUtil;
import com.file.downloadfile.bean.DownloadBean;


public class MainActivity extends Activity implements View.OnClickListener{

    private TextView tvPercent;
    private ProgressBar pbPercent;
    private static final String DOWNLOAD_URL = "http://pa-package-object.oss-cn-beijing.aliyuncs.com/f101a283db8943478d6cdd3004725232";
    public static final int STATE_START_DOWNLOAD = 0; // 开始下载
    public static final int STATE_DOWNLOADING = 1;   //  下载中
    public static final int STATE_FINISH_DOWNLOAD = 2;  // 下载完成
    private MyDownloadUtil myDownloadUtil;
    private  DownloadBean downloadBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDownloadUtil = new MyDownloadUtil(this);
        downloadBean = myDownloadUtil.getDownloadFileInfo();
        initView();
    }
    private void initView() {
        tvPercent = (TextView) findViewById(R.id.tv_percent);
        pbPercent = (ProgressBar) findViewById(R.id.pb_percent);
        findViewById(R.id.btn_start_download).setOnClickListener(this);
        findViewById(R.id.btn_stop_download).setOnClickListener(this);
        if(downloadBean == null){
            downloadBean = new DownloadBean();
        }
            tvPercent.setText("下载进度：" + downloadBean.getDownloadProgress()+"%");
            if(downloadBean.getDownloadProgress() > 0){
                pbPercent.setVisibility(View.VISIBLE);
            }else{
                pbPercent.setVisibility(View.GONE);
            }
    }



    private Runnable runnable = new Runnable() {
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_download:
                startDownloadFile();
                break;
            case R.id.btn_stop_download:
                stopDownloadFile();
                break;
        }
    }

    /**
     * 处理下载文件的消息
     */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int progress = msg.arg1;
            switch (msg.what){
                case STATE_START_DOWNLOAD:
                    tvPercent.setText("下载进度：" + progress + "%");
                    pbPercent.setVisibility(View.VISIBLE);
                    pbPercent.setProgress(progress);
                    break;
                case STATE_DOWNLOADING:

                    tvPercent.setText("下载进度："+progress + "%");
                    pbPercent.setProgress(progress);
                    break;
                case STATE_FINISH_DOWNLOAD:
                    tvPercent.setText("已完成");
                    pbPercent.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /**
     * 停止下载文件
     */
    public void stopDownloadFile(){
        downloadBean.setIsStopDownloadFile(true);
        handler.removeCallbacks(runnable);
    }

    /**
     * 开始下载文件
     */
    public void startDownloadFile(){
        downloadBean.setIsStopDownloadFile(false);
        new Thread(runnable).start();
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
}
