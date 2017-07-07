package com.file.downloadfile.download;

import com.file.downloadfile.Utils.DownloadUtils;
import com.file.downloadfile.Utils.FileUtils;
import com.file.downloadfile.Utils.LogUtils;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.file.downloadfile.database.model.DownloadFileInfo_Table.url;

/**
 * 文件下载类
 */

public class FileDownload {

    private static final String TAG = "FileDownload";

    /**
     * 文件下载目录
     */
    private static final String FILE_DOWNLOAD_DIR = "FileDownload";
    /**
     * 下载进度
     */
    private static long mDownloadLength;
    /**
     * 文件下载监听
     */
    private static FileDownloadListener mFileDownloadListener;
    /**
     * 文件下载详情
     */
    private static DownloadFileInfo mDownloadFileInfo;
    /**
     * 是否是debug模式
     */
    private static boolean isDebugModel;



    /**
     * 开始下载
     * @param downloadurl  文件url地址
     */
    public static void start(String downloadurl){
        String basePath = FileUtils.createBasePath(FILE_DOWNLOAD_DIR);
        List<DownloadFileInfo> downloadFileInfos = SQLite.select().from(DownloadFileInfo.class).where(url.eq(downloadurl)).orderBy(DownloadFileInfo_Table.id,false).queryList();
        if(downloadFileInfos.isEmpty()){
            mDownloadFileInfo = new DownloadFileInfo();
        }else{
            mDownloadFileInfo = downloadFileInfos.get(0);
        }

        URL url = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bufferedInputStream;
        OutputStream mOutputStream;
        ByteArrayOutputStream mByteOutput;
        try {
            url = new URL(downloadurl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);  // 请求时不使用缓存
            httpURLConnection.setConnectTimeout(5 * 1000); // 设置连接超时时间
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");

            long fileLength = httpURLConnection.getContentLength(); // 获取文件的大小
            String fileName = httpURLConnection.getHeaderField("Content-Disposition"); // 获取文件名
            File tmpFile = FileUtils.createTempFile(basePath,fileName);
            long downloadSize = 0;
            if(com.file.downloadfile.Utils.StringUtils.isBlank(mDownloadFileInfo.getUrl())){
                mDownloadFileInfo.setUrl(downloadurl);
                mDownloadFileInfo.setFileDir(FILE_DOWNLOAD_DIR);
                mDownloadFileInfo.setTmpFileName(tmpFile.getAbsolutePath());
                mDownloadFileInfo.setTotalDownloadSize(fileLength);
                mDownloadFileInfo.setFilePath(basePath+ File.separator+fileName);
            }else{
                downloadSize = mDownloadFileInfo.getDownloadedSize();
                if(downloadSize < fileLength && downloadSize > 0){
                    httpURLConnection.setRequestProperty("Range", "bytes=" + mDownloadFileInfo.getDownloadedSize() + "-"+downloadSize);
                }
            }

            int progress = DownloadUtils.getProgress(downloadSize,fileLength);
            mDownloadFileInfo.setDownloadedSize(downloadSize);
            mDownloadFileInfo.setDownloadProgress(progress);
            httpURLConnection.connect();
            if(mFileDownloadListener != null){
                mFileDownloadListener.onFileDownloading(mDownloadFileInfo);
            }

            int code = httpURLConnection.getResponseCode();
            LogUtils.E(TAG,"start  code="+code);
            if(code == HttpURLConnection.HTTP_PARTIAL){
                int bufferSize = 1024;
                bufferedInputStream = new BufferedInputStream(httpURLConnection.getInputStream(),bufferSize);
                int len = 0; //读取到的数据长度
                byte[] buffer = new byte[bufferSize];
                //写入中间文件
                mOutputStream = new FileOutputStream(tmpFile,true);//true表示向打开的文件末尾追加数据
                // 开始读取
                while((len = bufferedInputStream.read(buffer)) != -1){
                    mOutputStream.write(buffer,0,len);
                    downloadSize += len;
                    progress = DownloadUtils.getProgress(downloadSize,fileLength);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            if(mFileDownloadListener != null){

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public static void setFileDownloadListener(FileDownloadListener fileDownloadListener){
        mFileDownloadListener = fileDownloadListener;
    }

    public static void setDebugModel(){

    }

    public static boolean getDebugModel(){
        return isDebugModel;
    }
}
