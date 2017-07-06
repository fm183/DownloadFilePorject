package com.file.downloadfile.Utils;


import android.util.Log;

import com.file.downloadfile.download.FileDownload;

public class LogUtils {

	private final static int I = 1, D = 2, E = 3;

	private static void print(int mod, String tag, String msg) {
		switch (mod) {
		case I:
			Log.i(tag, msg);
			break;
		case D:
			Log.d(tag, msg);
			break;
		case E:
			Log.e(tag, msg);
			break;
		}
	}

	public static void E(String tag, String message) {
		if (!FileDownload.getDebugModel())
			return;
		if (message != null) {
			print(E, tag, message);
		}
	}


	public static void D(String tag, String message) {
		if (!FileDownload.getDebugModel())
			return;
		if (message != null) {
			print(D, tag, message);
		}
	}

	/*public static void E(String tag, String message) {
		if (!Constant.LOG_PRINT)
			return;
		if (message != null) {
			print(E, tag, message);
		}
	}*/

	public static void I(String tag, String message) {
		if (!FileDownload.getDebugModel())
			return;
		if (message != null) {
			print(I, tag, message);
		}
	}

	/**
	 * 打印日志
	 * @param tag
	 * @param message
	 */
	public static void println(String tag,String message){
		if(!FileDownload.getDebugModel()){
			return;
		}
		System.out.println(tag+"====="+message);
	}
	
	
}