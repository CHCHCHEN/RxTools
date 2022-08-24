package com.yingda.rxtools.http.func;

import com.yingda.rxtools.http.exception.ApiException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
/**
 * author: chen
 * data: 2022/8/24
 * des: 异常转换处理
*/
public class HttpResponseFunc<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        return Observable.error(ApiException.handleException(throwable));
    }
}
