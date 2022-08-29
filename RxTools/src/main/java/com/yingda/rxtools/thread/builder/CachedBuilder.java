package com.yingda.rxtools.thread.builder;


import com.yingda.rxtools.thread.ThreadPoolType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 * 线程池为无限大，当执行第二个任务时第一个任务已经完成，会复用执行第一个任务的线程，而不用每次新建线程。
 *
 * @author CHEN
 * @date 2020-12-02
 */
public class CachedBuilder extends ThreadPoolBuilder<ExecutorService> {
    @Override
    protected ExecutorService create() {
        return Executors.newCachedThreadPool();
    }

    @Override
    protected ThreadPoolType getType() {
        return ThreadPoolType.CACHED;
    }
}
