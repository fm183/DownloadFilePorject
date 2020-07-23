package com.file.downloadfile.listener;

import com.file.downloadfile.database.model.DownloadFileInfo;

/**
 *  文件下载监听
 */
public interface FileDownloadListener{

    /**
     *
     */
    void onFileDownloading(DownloadFileInfo downloadFileInfo);

    /**
     *
     */
    void onFileDownloadFail(DownloadFileInfo downloadFileInfo);
    /**
     *
     */
    void onFileDownloadCompleted(DownloadFileInfo downloadFileInfo);
    /**
     *
     */
    void onFileDownloadPaused(DownloadFileInfo downloadFileInfo);
}
