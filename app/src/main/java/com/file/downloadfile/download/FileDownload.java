package com.file.downloadfile.download;

import com.file.downloadfile.Utils.FileUtils;
import com.file.downloadfile.bean.DownloadFileInfo;
import com.file.downloadfile.listener.FileDownloadListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 文件下载工具类
 */

public class FileDownload {
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
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bufferedInputStream;
        try {
            url = new URL(downloadurl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setUseCaches(false);  // 请求时不使用缓存
            httpURLConnection.setConnectTimeout(5 * 1000); // 设置连接超时时间
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");

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
