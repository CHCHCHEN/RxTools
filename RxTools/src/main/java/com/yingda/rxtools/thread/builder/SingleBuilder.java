package com.yingda.rxtools.thread.builder;

import com.yingda.rxtools.thread.ThreadPoolType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Description:创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 *
 * @author CHEN
 * @date 2020-12-02
 */
public class SingleBuilder extends ThreadPoolBuilder<ExecutorService> {
    @Override
    protected ExecutorService create() {
        return Executors.newSingleThreadExecutor();
    }

    @Override
    protected ThreadPoolType getType() {
        return ThreadPoolType.SINGLE;
    }
}
