package com.file.downloadfile.Utils;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * 文件操作工具类
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

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


    /**
     * 创建临时文件
     * @param fileName
     * @return 创建新文件   创建过程中出现异常返回null
     */
    public static File createTempFile(String dri,String fileName){
        File file = new File(dri + fileName + ".dl");
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        }
        if(file == null){
            LogUtils.E(TAG,"createTempFile is fail");
        }else{
            LogUtils.D(TAG,"createTempFile====="+file.getName());
        }
        return file;
    }



}
