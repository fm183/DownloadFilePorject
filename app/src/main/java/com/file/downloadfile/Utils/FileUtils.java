package com.file.downloadfile.Utils;

import android.os.Environment;

import java.io.File;

/**
 * 文件操作工具类
 */
public class FileUtils {

    /**
     * 创建下载目录
     * @param dir
     * @return
     */
    public static String createBasePath(String dir){
        String basePath = "";
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return basePath;
        }
        File file = new File(Environment.getExternalStorageDirectory(),dir);
        if(!file.exists()||!file.isDirectory()){
            file.mkdirs();
        }
        basePath = file.getAbsolutePath();
        if(!basePath.toString().endsWith("/")){
            basePath += "/";
        }
        return basePath;
    }


}
