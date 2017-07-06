package com.file.downloadfile.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  下载文件数据库操作辅助类
 */

public class DownloadDatabaseHelper extends SQLiteOpenHelper {

    private static final String dbname = "download.db";
    private static final int VERSION = 1;

    public DownloadDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists download_info (id INTEGER PRIMARY KEY AUTOINCREMENT,url VARCHAR, fileDir VARCHAR,filePath VARCHAR,downloadedSize INTEGER,downloadProgress INTEGER,totalDownloadSize INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
