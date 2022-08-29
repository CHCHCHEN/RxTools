package com.yingda.rxtools.thread.builder;


import com.yingda.rxtools.thread.ThreadPoolType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Description:创建一个自定义线程池
 *
 * @author CHEN
 * @date 2020-12-02
 */
public class CustomBuilder extends ThreadPoolBuilder<ExecutorService> {
    /**
     * 核心线程池大小
     */
    private int mCorePoolSize = 1;
    /**
     * 最大线程池大小
     */
    private int mMaximumPoolSize = Integer.MAX_VALUE;
    /**
     * 线程任务空闲保留时间
     */
    private long mKeepAliveTime = 60;
    /**
     * 线程任务空闲保留时间单位
     */
    private TimeUnit mUnit = TimeUnit.SECONDS;
    /**
     * 任务等待策略
     */
    private BlockingQueue<Runnable> mWorkQueue = new SynchronousQueue<Runnable>();

    @Override
    protected ExecutorService create() {
        return new ThreadPoolExecutor(mCorePoolSize, mMaximumPoolSize, mKeepAliveTime, mUnit, mWorkQueue);
    }

    @Override
    protected ThreadPoolType getType() {
        return ThreadPoolType.CUSTOM;
    }

    public CustomBuilder corePoolSize(int corePoolSize) {
        mCorePoolSize = corePoolSize;
        return this;
    }

    public CustomBuilder maximumPoolSize(int maximumPoolSize) {
        mMaximumPoolSize = maximumPoolSize;
        return this;
    }

    public CustomBuilder keepAliveTime(long keepAliveTime) {
        mKeepAliveTime = keepAliveTime;
        return this;
    }

    public CustomBuilder unit(TimeUnit unit) {
        mUnit = unit;
        return this;
    }

    public CustomBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        mWorkQueue = workQueue;
        return this;
    }
}
