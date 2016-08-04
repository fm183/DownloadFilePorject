package com.file.downloadfile.bean;

/**
 * Created by Administrator on 2016/7/27.
 */
public class DownloadBean {
    /**
     * 已经下载文件的大小
     */
    private long downloadSize = -1;
    /**
     * 原始文件大小
     */
    private long fileSize = -1;
    /**
     *原始文件名
     */
    private String fileName = "";
    /**
     * 文件下载路径
     */
    private String downloadUrl = "";

    /**
     * 文件下载进度
     */
    private int downloadProgress;

    /**
     * 是否停止文件下载
     */
    private boolean isStopDownloadFile;

    public boolean isStopDownloadFile() {
        return isStopDownloadFile;
    }

    public void setIsStopDownloadFile(boolean isStopDownloadFile) {
        this.isStopDownloadFile = isStopDownloadFile;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;

    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DownloadBean{");
        sb.append("downloadSize=").append(downloadSize);
        sb.append(", fileSize=").append(fileSize);
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", downloadUrl='").append(downloadUrl).append('\'');
        sb.append(", downloadProgress=").append(downloadProgress);
        sb.append('}');
        return sb.toString();
    }
}
