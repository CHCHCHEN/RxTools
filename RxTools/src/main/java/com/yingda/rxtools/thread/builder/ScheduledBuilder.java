package com.yingda.rxtools.thread.builder;

import com.yingda.rxtools.thread.ThreadPoolType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:创建一个定长线程池，支持定时及周期性任务执行。
 *
 * @author CHEN
 * @date 2020-12-02
 */
public class ScheduledBuilder extends ThreadPoolBuilder<ExecutorService> {
    /**
     * 固定线程池大小
     */
    private int mSize = 1;

    protected ScheduledExecutorService mExecutorService = null;

    @Override
    protected ScheduledExecutorService create() {
        return Executors.newScheduledThreadPool(mSize);
    }

    @Override
    protected ThreadPoolType getType() {
        return ThreadPoolType.SCHEDULED;
    }

    @Override
    public ScheduledExecutorService builder() {
        if (mThreadPoolMap.get(getType() + "_" + mPoolName) != null) {
            mExecutorService = (ScheduledExecutorService) mThreadPoolMap.get(getType() + "_" + mPoolName);
        } else {
            mExecutorService = create();
            mThreadPoolMap.put(getType() + "_" + mPoolName, mExecutorService);
        }
        return mExecutorService;
    }

    public ScheduledExecutorService getExecutorService() {
        return mExecutorService;
    }

    public ScheduledBuilder size(int size) {
        this.mSize = size;
        return this;
    }
}
