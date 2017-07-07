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
}
