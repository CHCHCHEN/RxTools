package com.yingda.rxtools.thread.builder;

import com.yingda.rxtools.thread.ThreadPoolType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @param <T>
 * @author CHEN
 * @date 2020-12-02
 */
public abstract class ThreadPoolBuilder<T extends ExecutorService> {
	protected static Map<String, ExecutorService> mThreadPoolMap = new ConcurrentHashMap<String, ExecutorService>();
	protected ExecutorService mExecutorService = null;
	
	protected String mPoolName = "default";
	protected abstract T create();
	protected abstract ThreadPoolType getType();

	public ExecutorService builder() {
		if (mThreadPoolMap.get(getType() + "_" + mPoolName) != null) {
			mExecutorService = mThreadPoolMap.get(getType() + "_" + mPoolName);
		} else {
			mExecutorService = create();
			mThreadPoolMap.put(getType() + "_" + mPoolName, mExecutorService);
		}
		return mExecutorService;
	}
	
	public ThreadPoolBuilder<T> poolName(String poolName) {
		if (poolName != null && poolName.length() > 0) {
			mPoolName = poolName;
		}
		return this;
	}
}
