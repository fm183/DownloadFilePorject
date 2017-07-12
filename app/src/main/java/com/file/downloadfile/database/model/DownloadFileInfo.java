package com.file.downloadfile.database.model;

import com.file.downloadfile.database.DownloadInfoDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * 文件下载详情
 */
@Table(database = DownloadInfoDatabase.class)
public class DownloadFileInfo extends BaseModel{


    @PrimaryKey(autoincrement = true)
    private long id;

    @Column
    private String url;
    @Column
    private String fileDir;
    @Column
    private String filePath;
    @Column
    private String tmpFileName;
    @Column
    private long downloadedSize;
    @Column
    private int downloadProgress;
    @Column
    private long totalDownloadSize;
    @Column
    private String failMessage;

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }


    public String getTmpFileName() {
        return tmpFileName;
    }

    public void setTmpFileName(String tmpFileName) {
        this.tmpFileName = tmpFileName;
    }

    public long getTotalDownloadSize() {
        return totalDownloadSize;
    }

    public void setTotalDownloadSize(long totalDownloadSize) {
        this.totalDownloadSize = totalDownloadSize;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DownloadFileInfo{");
        sb.append("id=").append(id);
        sb.append(", url='").append(url).append('\'');
        sb.append(", fileDir='").append(fileDir).append('\'');
        sb.append(", filePath='").append(filePath).append('\'');
        sb.append(", tmpFileName='").append(tmpFileName).append('\'');
        sb.append(", downloadedSize=").append(downloadedSize);
        sb.append(", downloadProgress=").append(downloadProgress);
        sb.append(", totalDownloadSize=").append(totalDownloadSize);
        sb.append(", failMessage='").append(failMessage).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
