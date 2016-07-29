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
        sb.append('}');
        return sb.toString();
    }
}
