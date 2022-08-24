package com.yingda.rxtools.http.cache.stategy;

import com.yingda.rxtools.http.cache.RxCache;
import com.yingda.rxtools.http.cache.model.CacheResult;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * author: chen
 * data: 2022/8/24
 * des: 只读缓存
*/
public final class OnlyCacheStrategy extends BaseStrategy{
    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, long time, Observable<T> source, Type type) {
        return loadCache(rxCache,type,key,time,false);
    }
}
