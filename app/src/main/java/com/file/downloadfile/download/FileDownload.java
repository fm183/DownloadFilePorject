package com.file.downloadfile.download;

import com.file.downloadfile.listener.FileDownloadListener;

/**
 * 文件下载类
 */

public class FileDownload {

    /**
     * 文件下载监听
     */
    private FileDownloadListener mFileDownloadListener;

    private DownloadThread downloadThread;

    /**
     * 是否是debug模式
     */
    private static boolean mIsDebugModel;

    /**
     * 开始下载
     * @param downloadurl  文件url地址
     */
    public void start(String downloadurl){
        downloadThread  = new DownloadThread(downloadurl,mFileDownloadListener);
        downloadThread.start();
    }

    public void stop(){
        downloadThread.stopDownload();
    }

    public void setFileDownloadListener(FileDownloadListener fileDownloadListener){
        mFileDownloadListener = fileDownloadListener;
    }

    public static void setDebugModel(boolean isDebugModel){
        mIsDebugModel = isDebugModel;
    }

    public static boolean getDebugModel(){
        return mIsDebugModel;
    }
}
