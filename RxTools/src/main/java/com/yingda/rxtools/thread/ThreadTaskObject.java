package com.yingda.rxtools.thread;

import java.util.concurrent.ExecutorService;

/**
 * @author CHEN
 * @date 2020-12-02
 */
public abstract class ThreadTaskObject implements Runnable {
    private ExecutorService mExecutorService = null;

    private String mPoolName = null;

    public ThreadTaskObject() {
        init();
    }

    public ThreadTaskObject(String poolName) {
        mPoolName = poolName;
        init();
    }

    private void init() {
        mExecutorService = ThreadPoolHelp.Builder.cached().name(mPoolName).builder();
    }

    public void start() {
        mExecutorService.execute(this);
    }
}
