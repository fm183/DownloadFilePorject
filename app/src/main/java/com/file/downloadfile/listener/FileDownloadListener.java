package com.file.downloadfile.listener;

import com.file.downloadfile.bean.DownloadBean;

/**
 *  文件下载监听
 */
public interface FileDownloadListener{

    /**
     *
     * @param downloadBean
     */
    public void onFileDownloading(DownloadBean downloadBean);

    /**
     *
     * @param downloadBean
     */
    public void onFileDownloadFail(DownloadBean downloadBean);
    /**
     *
     * @param downloadBean
     */
    public void onFileDownloadCompleted(DownloadBean downloadBean);
    /**
     *
     * @param downloadBean
     */
    public void onFileDownloadPaused(DownloadBean downloadBean);
}
