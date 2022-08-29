package com.yingda.rxtools.thread.builder;

import com.yingda.rxtools.thread.ThreadPoolType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
 *
 * @author CHEN
 * @date 2020-12-02
 */
public class FixedBuilder extends ThreadPoolBuilder<ExecutorService> {
    /**
     * 固定线程池
     */
    private int mSize = 1;

    @Override
    protected ExecutorService create() {
        return Executors.newFixedThreadPool(mSize);
    }

    @Override
    protected ThreadPoolType getType() {
        return ThreadPoolType.FIXED;
    }

    public FixedBuilder setSize(int size) {
        mSize = size;
        return this;
    }
}
