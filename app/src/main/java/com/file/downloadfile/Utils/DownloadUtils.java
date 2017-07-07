package com.file.downloadfile.Utils;

import java.text.DecimalFormat;

/**
 * 下载工具类
 */

public class DownloadUtils {

    /**
     * 设置进度
     * @param length
     * @param totalLength
     */
    public static int getProgress(long length, long totalLength){
        if(totalLength == 0){
            return 0;
        }
        return conversionPercent(length, totalLength);
    }

    /**
     * 将大小转化为百分比
     * @param length
     * @param totalLength
     * @return
     */
    public static int conversionPercent(long length,long totalLength){
        int percent = (int) (length * 100 / totalLength);
        return percent;
    }

    /**
     * 保留double的两位小数点
     * @param length
     * @param totalLength
     * @return
     */
    public static String formatDouble(long length,long totalLength){
        double totalSize = totalLength;
        double size = length;
        double d = size / totalSize;
        DecimalFormat df = new DecimalFormat("0.00");
        String str =  df.format(d);
        return str;
    }




}
