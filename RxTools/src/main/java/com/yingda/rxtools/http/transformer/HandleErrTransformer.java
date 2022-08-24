package com.yingda.rxtools.http.transformer;

import com.yingda.rxtools.http.func.HttpResponseFunc;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;

/**
 * author: chen
 * data: 2022/8/24
 * des: 错误转换Transformer
 */
public class HandleErrTransformer<T> implements ObservableTransformer<T, T> {
    @Override
    public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
        return upstream.onErrorResumeNext(new HttpResponseFunc<T>());
    }
}
