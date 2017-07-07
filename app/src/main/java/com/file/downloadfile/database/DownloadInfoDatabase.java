package com.file.downloadfile.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Administrator on 2017/7/7.
 */
@Database(name= DownloadInfoDatabase.DATABASE_NAME,version = DownloadInfoDatabase.VERSION)
public class DownloadInfoDatabase {

    //数据库名称
    public static final String DATABASE_NAME = "download_info";
    //数据库版本号
    public static final int VERSION = 1;
}
