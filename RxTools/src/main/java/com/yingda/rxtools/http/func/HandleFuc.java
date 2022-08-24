package com.yingda.rxtools.http.func;

import com.yingda.rxtools.http.exception.ApiException;
import com.yingda.rxtools.http.exception.ServerException;
import com.yingda.rxtools.http.model.ApiResult;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * author: chen
 * data: 2022/8/24
 * des: ApiResult<T>转换T
*/
public class HandleFuc<T> implements Function<ApiResult<T>, T> {
    @Override
    public T apply(@NonNull ApiResult<T> tApiResult) throws Exception {
        if (ApiException.isOk(tApiResult)) {
            return tApiResult.getData();// == null ? Optional.ofNullable(tApiResult.getData()).orElse(null) : tApiResult.getData();
        } else {
            throw new ServerException(tApiResult.getCode(), tApiResult.getMsg());
        }
    }
}
