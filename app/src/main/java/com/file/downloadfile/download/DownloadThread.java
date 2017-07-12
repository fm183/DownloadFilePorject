package com.file.downloadfile.download;

import com.file.downloadfile.Utils.DownloadUtils;
import com.file.downloadfile.Utils.FileUtils;
import com.file.downloadfile.Utils.LogUtils;
import com.file.downloadfile.Utils.StringUtils;
import com.file.downloadfile.database.model.DownloadFileInfo;
import com.file.downloadfile.database.model.DownloadFileInfo_Table;
import com.file.downloadfile.listener.FileDownloadListener;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static com.file.downloadfile.database.model.DownloadFileInfo_Table.url;

/**
 * 下载线程
 */

public class DownloadThread extends Thread {

    private static final String TAG = DownloadThread.class.getSimpleName();

    /**
     * 文件下载目录
     */
    private static final String FILE_DOWNLOAD_DIR = "FileDownload";

    private String mDownloadUrl;
    /**
     * 文件下载详情
     */
    private static DownloadFileInfo mDownloadFileInfo;

    /**
     * 文件下载监听
     */
    private FileDownloadListener mFileDownloadListener;

    private boolean isStopDownload;
    private OutputStream mOutputStream;
    private ByteArrayOutputStream mByteOutput;
    private File tmpFile;




    public DownloadThread(String downloadUrl, FileDownloadListener fileDownloadListener){
        this.mDownloadUrl = downloadUrl;
        this.mFileDownloadListener = fileDownloadListener;
    }

    @Override
    public void run() {
        super.run();
        isStopDownload = false;
        String basePath = FileUtils.createBasePath(FILE_DOWNLOAD_DIR);
        List<DownloadFileInfo> downloadFileInfos = SQLite.select().from(DownloadFileInfo.class).where(url.eq(this.mDownloadUrl)).orderBy(DownloadFileInfo_Table.id,false).queryList();
        if(downloadFileInfos.isEmpty()){
            mDownloadFileInfo = new DownloadFileInfo();
            mDownloadFileInfo.save();
        }else{
            mDownloadFileInfo = downloadFileInfos.get(0);
        }
        LogUtils.D(TAG,"run ="+mDownloadFileInfo.toString());
        URL url;
        HttpURLConnection httpURLConnection;
        BufferedInputStream bufferedInputStream;
        try {
            url = new URL(this.mDownloadUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);  // 请求时不使用缓存
            httpURLConnection.setConnectTimeout(5 * 1000); // 设置连接超时时间
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");


            long downloadSize = 0;
            String tmpFilePath = mDownloadFileInfo.getTmpFileName();
            if(!StringUtils.isBlank(tmpFilePath)){
                tmpFile = new File(tmpFilePath);
                if(!tmpFile.exists() || !tmpFile.isFile()){
                    mDownloadFileInfo.setUrl(null);
                }
            }
            if(StringUtils.isBlank(mDownloadFileInfo.getUrl())){
                long fileLength = httpURLConnection.getContentLength(); // 获取文件的大小
                String fileName = httpURLConnection.getHeaderField("Content-Disposition"); // 获取文件名
                tmpFile = FileUtils.createTempFile(basePath,fileName);
                LogUtils.D(TAG,"run fileLength="+fileLength+",fileName="+fileName+",absolute="+tmpFile.getAbsolutePath());
                mDownloadFileInfo.setUrl(this.mDownloadUrl);
                mDownloadFileInfo.setFileDir(FILE_DOWNLOAD_DIR);
                mDownloadFileInfo.setTmpFileName(tmpFile.getAbsolutePath());
                mDownloadFileInfo.setTotalDownloadSize(fileLength);
                mDownloadFileInfo.setFilePath(basePath+ File.separator+fileName);
            }else{
                downloadSize = mDownloadFileInfo.getDownloadedSize();
                if(downloadSize < mDownloadFileInfo.getTotalDownloadSize() && downloadSize > 0){
                    httpURLConnection.setRequestProperty("Range", "bytes=" + mDownloadFileInfo.getDownloadedSize() + "-"+mDownloadFileInfo.getTotalDownloadSize());
                }
            }

            long fileLength = mDownloadFileInfo.getTotalDownloadSize();
            int progress = DownloadUtils.getProgress(downloadSize,fileLength);
            mDownloadFileInfo.setDownloadedSize(downloadSize);
            mDownloadFileInfo.setDownloadProgress(progress);
            httpURLConnection.connect();
            if(mFileDownloadListener != null){
                mFileDownloadListener.onFileDownloading(mDownloadFileInfo);
            }

            int code = httpURLConnection.getResponseCode();
            LogUtils.E(TAG,"start  code="+code);
            if(code == HttpURLConnection.HTTP_OK){
                long currentTime = System.currentTimeMillis();
                int bufferSize = 1024;
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream(),bufferSize);
                int len; //读取到的数据长度
                byte[] buffer = new byte[bufferSize];
                //写入中间文件
                mOutputStream = new FileOutputStream(tmpFile,true);//true表示向打开的文件末尾追加数据
                mByteOutput = new ByteArrayOutputStream();
                // 开始读取
                while((len = bufferedInputStream.read(buffer)) != -1){
                    mByteOutput.write(buffer,0,len);
                    mDownloadFileInfo = writeCache(mDownloadFileInfo);
                    progress = DownloadUtils.getProgress(mDownloadFileInfo.getDownloadedSize(),fileLength);
                    LogUtils.D(TAG,"run === progress="+progress+",downloadSize="+downloadSize);
                    long nowTime = System.currentTimeMillis();
                    LogUtils.D(TAG,"run === currentTime="+currentTime+",nowTime="+nowTime);
                    if(currentTime < nowTime - 500){
                        currentTime = nowTime;
                        mDownloadFileInfo.setDownloadProgress(progress);
                        if(mFileDownloadListener != null){
                            if(downloadSize == fileLength){
                                mFileDownloadListener.onFileDownloadCompleted(mDownloadFileInfo);
                                break;
                            }else{
                                mFileDownloadListener.onFileDownloading(mDownloadFileInfo);
                            }
                        }
                    }
                    if(isStopDownload){
                        if(mFileDownloadListener != null){
                            mFileDownloadListener.onFileDownloadPaused(mDownloadFileInfo);
                        }
                        mDownloadFileInfo.update();
                        break;
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            mDownloadFileInfo.setFailMessage(e.getMessage());
            if(mFileDownloadListener != null){
                mFileDownloadListener.onFileDownloadFail(mDownloadFileInfo);
            }
        }finally {
            mDownloadFileInfo.update();
        }
    }

    public void stopDownload(){
        isStopDownload = true;
    }


    /**
     * 写缓存
     */
    private synchronized DownloadFileInfo writeCache(DownloadFileInfo downloadFileInfo){
        if(mByteOutput != null && mByteOutput.size() > 0 && mOutputStream!= null){
            try {
                mByteOutput.writeTo(mOutputStream);
                downloadFileInfo.setDownloadedSize(downloadFileInfo.getDownloadedSize()+mByteOutput.size());
                mByteOutput.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(downloadFileInfo.getDownloadedSize() >= downloadFileInfo.getTotalDownloadSize()){  // 下载完成后重命名
            boolean isSuccess =  tmpFile.renameTo(FileUtils.deleteAndCreatFilePath(downloadFileInfo.getFilePath(),true));
            LogUtils.D(TAG,"writeCache  renameto is"+isSuccess);
            mFileDownloadListener.onFileDownloadCompleted(downloadFileInfo);
        }
        return downloadFileInfo;
    }



}
