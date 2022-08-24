package com.yingda.rxtools.http.func;


import com.yingda.rxtools.http.cache.model.CacheResult;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * author: chen
 * data: 2022/8/24
 * des: 缓存结果转换
*/
public class CacheResultFunc<T> implements Function<CacheResult<T>, T> {
    @Override
    public T apply(@NonNull CacheResult<T> tCacheResult) throws Exception {
        return tCacheResult.data;
    }
}
