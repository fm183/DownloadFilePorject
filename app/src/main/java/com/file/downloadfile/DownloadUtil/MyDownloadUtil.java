package com.file.downloadfile.DownloadUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.file.downloadfile.bean.DownloadBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private DownloadBean downloadBean;
    private long mLoadedByteLength = 0; // 断点位置
    private long mTotalByteLength = 0; // 文件总长度
    private OutputStream mOutputStream;
    private ByteArrayOutputStream mByteOutput;
    private Context mContext;

    public MyDownloadUtil(Context context){
        this.mContext = context;
    }

    /**
     * 开始下载
     * @param downloadBean
     * @param downloadUrl
     * @param downloadFileStateListener
     */
    public void startDownload(DownloadBean downloadBean,String downloadUrl,DownloadFileStateListener downloadFileStateListener){
        this.downloadBean = downloadBean;
        downloadFile(downloadUrl,downloadFileStateListener);
    }


    /**
     * 临时文件对象
      */
    private  File mTempFile = null;
    /**
     * 下载文件
     * @param downloadUrl
     * @param downloadFileStateListener
     */
    private void downloadFile(String downloadUrl,DownloadFileStateListener downloadFileStateListener){
        System.out.println("downloadFiledownloadFiledownloadFile");
        System.out.println("downloadBean===="+downloadBean.toString());
        URL url = null;
        HttpURLConnection httpURLConnection = null;
        BufferedInputStream bufferedReader;
        if(downloadBean.isStopDownloadFile()){
            return;
        }
        try {
            url = new URL(downloadUrl);
            httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setUseCaches(false);  // 请求时不使用缓存
            httpURLConnection.setConnectTimeout(5 * 1000); // 设置连接超时时间
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            createBasePath(mContext);
            if(TextUtils.isEmpty(downloadBean.getFileName())){ //  判断是否获取到文件名，没有就去获取下载文件信息
                getDownloadFileInfo(httpURLConnection, downloadUrl);
            }
            mLoadedByteLength = downloadBean.getDownloadSize();
            mTotalByteLength = downloadBean.getFileSize();
            System.out.println("mLoadedByteLength===="+mLoadedByteLength+",mTotalByteLength==="+mTotalByteLength);
            if(mLoadedByteLength > 0 && mLoadedByteLength < mTotalByteLength){   // 如果已经在下载了，而且没下载完成就暂停，则继续接着上次的进度下载
                httpURLConnection.setRequestProperty("Range", "bytes=" + mLoadedByteLength + "-");
            }else{
                getDownloadFileInfo(httpURLConnection, downloadUrl);
                if(downloadBean.getDownloadProgress() == 100 || mLoadedByteLength == mTotalByteLength){
                    deleDownloadInfo();
                    deleteAndCreatFilePath(downloadBean.getFileName(),false);
                    downloadBean = null;
                    downloadBean = new DownloadBean();
                }
                System.out.println("downloadBean==========="+downloadBean.toString());
                mTempFile = createTempFile(downloadBean.getFileName());
                if(mTempFile == null){
                    Toast.makeText(mContext,"创建临时文件失败，请重新点击下载！",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(mTempFile == null){
                mTempFile = createTempFile(downloadBean.getFileName());
            }
            httpURLConnection.connect();
            int bufferSize = 1024;
            bufferedReader = new BufferedInputStream(httpURLConnection.getInputStream(),bufferSize); //为InputStream类增加缓冲区功能
            if(mTotalByteLength == 0){
                mTotalByteLength = downloadBean.getFileSize();
            }
            readProgress(mLoadedByteLength,mTotalByteLength,downloadFileStateListener);
            int len = 0; //读取到的数据长度
            byte[] buffer = new byte[bufferSize];
            //写入中间文件
            mOutputStream = new FileOutputStream(mTempFile,true);//true表示向打开的文件末尾追加数据
            mByteOutput = new ByteArrayOutputStream();
            //开始读取
            while((len = bufferedReader.read(buffer)) != -1) {
                mByteOutput.write(buffer,0,len);
                writeCache();
                readProgress(mLoadedByteLength, mTotalByteLength, downloadFileStateListener);
                saveDownloadInfo(mContext);
                if(downloadBean.isStopDownloadFile()){
                    downloadFileStateListener.onStopDownload();
                    break;
                }
            }
            mByteOutput.close();
            bufferedReader.close();
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

    /**
     * 取得文件下载信息
     * @return
     */
    public DownloadBean getDownloadFileInfo(){
        createSharedPreference();
        String downloadInfo = sharedPreferences.getString("downloadInfo","");
        System.out.println("getDownloadFileInfo     downloadInfo=====" + downloadInfo);
        if(!TextUtils.isEmpty(downloadInfo)){
            System.out.println("TextUtils.isEmpty(downloadInfo)======"+TextUtils.isEmpty(downloadInfo));
            try {
                JSONObject jsonObject = new JSONObject(downloadInfo);
                DownloadBean  myDownloadBean = new DownloadBean();
                String fileName = "";
                if(jsonObject.getLong("fileSize") == jsonObject.getLong("downloadSize")){
                    fileName = jsonObject.getString("fileName");
                }else {
                    fileName = jsonObject.getString("fileName") + ".dl";
                }
                if(readFileInfo(fileName)){
                    myDownloadBean.setDownloadSize(jsonObject.getLong("downloadSize"));
                    myDownloadBean.setFileSize(jsonObject.getLong("fileSize"));
                    myDownloadBean.setFileName(jsonObject.getString("fileName"));
                    myDownloadBean.setDownloadUrl(jsonObject.getString("downloadUrl"));
                    myDownloadBean.setDownloadProgress(jsonObject.getInt("downloadProgress"));
                    System.out.println("myDownloadBean======" + myDownloadBean.toString());
                    return myDownloadBean;
                }else {
                    deleDownloadInfo();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new DownloadBean();
    }

    /**
     * 删除文件下载信息
     */
    public void deleDownloadInfo(){
        createSharedPreference();
        String downloadInfo = sharedPreferences.getString("downloadInfo","");
        if(TextUtils.isEmpty(downloadInfo)){
            editor.putString("downloadInfo","");
            editor.commit();
        }
    }

    /**
     * 保存文件下载信息
     * @param context
     */
    public void saveDownloadInfo(Context context){
        if(downloadBean == null){
            return;
        }else{
            createSharedPreference();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("downloadSize",downloadBean.getDownloadSize());
                jsonObject.put("downloadUrl",downloadBean.getDownloadUrl());
                jsonObject.put("fileName",downloadBean.getFileName());
                jsonObject.put("fileSize",downloadBean.getFileSize());
                jsonObject.put("downloadProgress",downloadBean.getDownloadProgress());
            }catch (JSONException exception){
                exception.printStackTrace();
            }
            editor.putString("downloadInfo", jsonObject.toString());
            editor.commit();
        }
    }

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private void createSharedPreference(){
        if(sharedPreferences == null){
            sharedPreferences = mContext.getSharedPreferences("downloadInfo",0);
            editor = sharedPreferences.edit();
        }
    }


    /**
     * 设置进度
     * @param downloadLength,totalLength
     */
    private void readProgress(long downloadLength,long totalLength,DownloadFileStateListener downloadFileStateListener){
        System.out.println("downloadLength=-====="+downloadLength+",totalLength======="+totalLength);
        if(totalLength == 0){

            return;
        }
        int progress = conversionPercent(downloadLength, totalLength);
        String totalSize = formatDouble(downloadLength, totalLength);
        downloadFileStateListener.onDownloadProgress(progress, totalSize);
        downloadBean.setDownloadSize(mLoadedByteLength);
        downloadBean.setDownloadProgress(progress);
        downloadFileStateListener.onStartDownload(progress);
        if(progress == 100){
            downloadFileStateListener.onDownloadFinish();
            mTotalByteLength = 0;
            mLoadedByteLength = 0;
        }
    }

    /**
     * 将大小转化为百分比
     * @param length
     * @return
     */
    private int conversionPercent(long length,long totalLength){
        System.out.println("length==============" + length);
        System.out.println("getFileSize===========" + totalLength);
        int percent = (int) (length * 100 / totalLength);
        System.out.println("percent======"+percent);
        return percent;
    }

    /**
     * 保留double的两位小数点
     * @param length totalLength
     * @return
     */
    private String formatDouble(long length,long totalLength){
        double totalSize = totalLength;
        double size = length;
        double d = size / totalSize;
        DecimalFormat df = new DecimalFormat("0.00");
        String str =  df.format(d);
        return str;
    }

    /**
     * 写缓存
     */
    private synchronized void writeCache(){
        if(mByteOutput != null && mByteOutput.size() > 0 && mOutputStream!= null){
            try {
                mByteOutput.writeTo(mOutputStream);
                mLoadedByteLength += mByteOutput.size();
                mByteOutput.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("mLoadedByteLength====="+mLoadedByteLength+",mTotalByteLength===="+mTotalByteLength);
        if(mLoadedByteLength >= mTotalByteLength){  // 下载完成后重命名
            mTempFile.renameTo(deleteAndCreatFilePath(downloadBean.getFileName(),true));
        }
    }


    /**
     * 获取下载信息
     * @param connection
     */
    private void getDownloadFileInfo(HttpURLConnection connection, String downloadUrl){
        long fileLength = connection.getContentLength(); // 获取文件的大小
        String fileName = connection.getHeaderField("Content-Disposition"); // 获取文件名
        downloadBean.setFileSize(fileLength);
        downloadBean.setDownloadUrl(downloadUrl);
        downloadBean.setFileName(fileName);
        System.out.println("fileLength=======" + fileLength);
        System.out.println("downloadUrl=======" + downloadUrl);
        System.out.println("fileName=========="+fileName);
    }


    /**
     * 创建临时文件
     * @param fileName
     * @return 创建新文件   创建过程中出现异常返回null
     */
    private File createTempFile(String fileName){
        System.out.println("createTempFile  fileName======"+fileName);
        File file = new File(basePath + fileName + ".dl");
        if(file.exists()){
            System.out.println("existsexistsexists");
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        }
        if(file == null){
            System.out.println("nullnullnull");
        }else{
            System.out.println("createTempFile====="+file.getName());
        }
        return file;
    }

    /**
     * 下载完成后保存文件
     * @param fileName
     * @param isDeletedCreate // 是否删除并创建文件
     * @return 创建新文件   创建过程中出现异常返回null
     */
    private File deleteAndCreatFilePath(String fileName,boolean isDeletedCreate){
        File file = new File(basePath + fileName);
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

    /**
     * 根目录
     */
    private String basePath;
    /**
     * 创建根目录
     * @param contex
     * @return
     */
    private void createBasePath(Context contex){
        if(TextUtils.isEmpty(basePath)){
            File file = null;
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                file = new File(Environment.getExternalStorageDirectory(),FILE_DIR);
            }else{
                file = contex.getApplicationContext().getCacheDir();
            }
            if(!file.exists()||!file.isDirectory()){
                file.mkdirs();
            }
            basePath = file.getAbsolutePath();
            if(!basePath.toString().endsWith("/")){
                basePath += "/";
            }
        }
    }

    /**
     * 读取下载至本地的文件信息
     * @param fileName
     * @return  文件是否存在
     */
    private boolean readFileInfo(String fileName){
        createBasePath(mContext);
        File file = new File(basePath +fileName);
        System.out.println(basePath+fileName);
        long fileSize = 0;
        if(file.exists()){
            System.out.println("readFileInfoexistsexistsexists");
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                fileSize = fileInputStream.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(""+fileSize);
        return fileSize > 0;
    }


    /**
     * 文件下载监听
     */
    public interface DownloadFileStateListener{
        /**
         * 开始下载
         */
        void onStartDownload(int progress);

        /**
         * 下载进度
         * @param progress,downloadSize
         */
        void onDownloadProgress(int progress,String downloadSize);

        /**
         * 下载完成
         */
        void onDownloadFinish();

        /**
         * 停止下载
         */
        void onStopDownload();

    }
}
