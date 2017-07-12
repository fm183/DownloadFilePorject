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
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return "";
        }
        File file = new File(Environment.getExternalStorageDirectory(),dir);
        if(!file.exists()||!file.isDirectory()){
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }


    /**
     * 创建临时文件
     * @param fileName
     * @return 创建新文件   创建过程中出现异常返回null
     */
    public static File createTempFile(String dri,String fileName){
        File file = new File(dri + File.separator+ fileName + ".dl");
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


    /**
     * 下载完成后保存文件
     * @param filePath
     * @param isDeletedCreate // 是否删除并创建文件
     * @return 创建新文件   创建过程中出现异常返回null
     */
    public static  File deleteAndCreatFilePath(String filePath,boolean isDeletedCreate){
        File file = new File(filePath);
        if(file.exists()){ // 如果存在先删除
            file.delete();
        }
        if(isDeletedCreate){ //为true创建新文件
            try {
                file.createNewFile();
                System.out.println("fileName==========" + file.getName());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        System.out.println("deleteAndCreatFilePath====="+file.getName());
        return file;
    }



}
