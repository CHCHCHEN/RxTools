package com.yingda.rxtools.http.callback;

/**
 * author: chen
 * data: 2022/8/24
 * des: 下载进度回调（主线程，可以直接操作UI）
*/
public abstract class DownloadProgressCallBack<T> extends CallBack<T> {
    public DownloadProgressCallBack() {
    }

    @Override
    public void onSuccess(T response) {
        
    }

    public abstract void update(long bytesRead, long contentLength, boolean done);

    public abstract void onComplete(String path);

    @Override
    public void onCompleted() {
        
    }
}
