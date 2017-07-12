package com.file.downloadfile.Utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * 弱引用的handler，解决内存泄漏问题
 * @author fanming
 * @param <T> 弱引用的对象
 */
public abstract class WeakHandler<T> extends Handler {
	
	private final WeakReference<T> weakReference;

	public WeakHandler(T t) {
		this.weakReference = new WeakReference<T>(t);
	}
	
	@Override
	public final void handleMessage(Message msg) {
		super.handleMessage(msg);
		handleMessage(msg,getRef());
	}

	protected final T getRef() {
		return weakReference.get();
	}

	/**
	 * 处理消息
	 * @param msg 消息对象
	 * @param t 弱引用的对象有可能为空
	 */
	public abstract void handleMessage(Message msg, T t);

}
