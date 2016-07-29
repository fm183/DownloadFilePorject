package com.file.downloadfile.DownloadUtil;

import android.os.Environment;
import android.util.Log;

import com.file.downloadfile.bean.DownloadBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/7/27.
 */
public class MyDownloadUtil {
    private static final String FILE_DIR = "downloadFile";
    private static DownloadBean downloadBean;


    /**
     * 下载文件
     * @param downloadUrl
     * @param downloadFileStateListener
     */
    public static void downloadFile(String downloadUrl,DownloadFileStateListener downloadFileStateListener){
        System.out.println("downloadFiledownloadFiledownloadFile");
        downloadBean = new DownloadBean();
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            url = new URL(downloadUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(5 * 1000); // 设置连接超时时间
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.connect();
            downloadFileStateListener.onStartDownload();
            long fileLength = httpURLConnection.getContentLength(); // 获取文件的大小
            String fileName = httpURLConnection.getHeaderField("Content-Disposition");
            inputStream = httpURLConnection.getInputStream();
            downloadBean.setFileSize(fileLength);
            downloadBean.setDownloadUrl(downloadUrl);
            System.out.println("fileLength=======" + fileLength);
            System.out.println("downloadUrl=======" + downloadUrl);
            byte[] bs = new byte[1024]; //1KB的数据缓冲
            int len = 0; //读取到的数据长度
            String path = creatFilePath(fileName);
            System.out.println("path=======" + path);
            downloadBean.setFileName(path);
            //输出的文件流
            OutputStream outputStream = new FileOutputStream(path);
            //开始读取
            while((len = inputStream.read(bs)) != -1) {
                outputStream.write(bs, 0, len);
                long length = readDownloadFileSize(path);
                System.out.println("length=======" + length);
                int perent = conversionPercent(length);
                System.out.println("perent=======" + perent);
                downloadFileStateListener.onDownloadProgress(perent);
                if(perent == 100){
                    downloadFileStateListener.onDownloadFinish();
                }
                downloadBean.setDownloadSize(length);
            }
            outputStream.close();
            inputStream.close();
        }catch (MalformedURLException malformedURLException){
            malformedURLException.printStackTrace();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }finally {
            if(httpURLConnection!=null){
                httpURLConnection.disconnect();
                httpURLConnection = null;
            }
        }
    }

    static File file = null;
    static FileInputStream fileInputStream = null;
    static String fileName = "";
    /**
     * 读取下载文件的大小
     * @param fileName
     * @return
     */
    private static long readDownloadFileSize(String fileName){
        long fileLength = 0;
        try {
            if(file == null){
                file  = new File(creatFilePath(fileName));
            }
            fileInputStream = null;
            fileInputStream = new FileInputStream(file);
            fileLength = fileInputStream.available();
            fileInputStream.close();
        }catch (FileNotFoundException fileNotFoundException){
            fileNotFoundException.printStackTrace();
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
        return fileLength;
    }

    /**
     * 将大小转化为百分比
     * @param length
     * @return
     */
    private static int conversionPercent(long length){
        System.out.println("length=============="+length);
        System.out.println("getFileSize==========="+downloadBean.getFileSize());
        double fileSize = downloadBean.getFileSize();
        double len = length;
        double d = len / fileSize;
        System.out.println("d========="+d);
        DecimalFormat df = new DecimalFormat("0.00");
        String per =  df.format(d);
        int percent = (int)(Double.parseDouble(per) *100);
        System.out.println("percent========="+percent);
        return percent;
    }

    /**
     * 创建文件
     * @return 存储文件的路径
     * @param fileName
     */
    private static String creatFilePath(String fileName){
        String filePath = "";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filePath = Environment.getExternalStorageDirectory() + File.separator + FILE_DIR;
            System.out.println("filePath==========="+filePath);
            try {
                fileName = createFile(createFileDir(filePath)+fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    private static String createFileDir(String filePath){
        System.out.println("createFileDir  filePath===="+filePath);
        File file = new File(filePath);
        if(!file.exists()){
            file.mkdir();
        }
        return filePath;
    }
    private static String createFile(String fileName) throws IOException {
        System.out.println("createFile  fileName===="+fileName);
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        file.createNewFile();
        return fileName;
    }


    public interface DownloadFileStateListener{
        /**
         * 开始下载
         */
        void onStartDownload();

        /**
         * 下载进度
         * @param progress
         */
        void onDownloadProgress(int progress);

        /**
         * 下载完成
         */
        void onDownloadFinish();

    }
}
