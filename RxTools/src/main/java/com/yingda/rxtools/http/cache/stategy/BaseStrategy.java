package com.yingda.rxtools.http.cache.stategy;

import com.yingda.rxtools.http.cache.RxCache;
import com.yingda.rxtools.http.cache.model.CacheResult;
import com.yingda.rxtools.http.utils.HttpLog;

import java.lang.reflect.Type;
import java.util.ConcurrentModificationException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * author: chen
 * data: 2022/8/24
 * des: 实现缓存策略的基类
*/
public abstract class BaseStrategy implements IStrategy {
    <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache, Type type, final String key, final long time, final boolean needEmpty) {
        Observable<CacheResult<T>> observable = rxCache.<T>load(type, key, time).flatMap(new Function<T, ObservableSource<CacheResult<T>>>() {
            @Override
            public ObservableSource<CacheResult<T>> apply(@NonNull T t) throws Exception {
                if (t == null) {
                    return Observable.error(new NullPointerException("Not find the cache!"));
                }
                return Observable.just(new CacheResult<T>(true, t));
            }
        });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }

    //请求成功后：异步保存
    <T> Observable<CacheResult<T>> loadRemote2(final RxCache rxCache, final String key, Observable<T> source, final boolean needEmpty) {
        Observable<CacheResult<T>> observable = source
                .map(new Function<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> apply(@NonNull T t) throws Exception {
                        HttpLog.i("loadRemote result=" + t);
                        rxCache.save(key, t).subscribeOn(Schedulers.io())
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(@NonNull Boolean status) throws Exception {
                                        HttpLog.i("save status => " + status);
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@NonNull Throwable throwable) throws Exception {
                                        if (throwable instanceof ConcurrentModificationException) {
                                            HttpLog.i("Save failed, please use a synchronized cache strategy :", throwable);
                                        } else {
                                            HttpLog.i(throwable.getMessage());
                                        }
                                    }
                                });
                        return new CacheResult<T>(false, t);
                    }
                });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }

    //请求成功后：同步保存
    <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source, final boolean needEmpty) {
        Observable<CacheResult<T>> observable = source
                .flatMap(new Function<T, ObservableSource<CacheResult<T>>>() {
                    @Override
                    public ObservableSource<CacheResult<T>> apply(final @NonNull T t) throws Exception {
                        return  rxCache.save(key, t).map(new Function<Boolean, CacheResult<T>>() {
                            @Override
                            public CacheResult<T> apply(@NonNull Boolean aBoolean) throws Exception {
                                HttpLog.i("save status => " + aBoolean);
                                return new CacheResult<T>(false, t);
                            }
                        }).onErrorReturn(new Function<Throwable, CacheResult<T>>() {
                            @Override
                            public CacheResult<T> apply(@NonNull Throwable throwable) throws Exception {
                                HttpLog.i("save status => " + throwable);
                                return new CacheResult<T>(false, t);
                            }
                        });
                    }
                });
        if (needEmpty) {
            observable = observable
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CacheResult<T>>>() {
                        @Override
                        public ObservableSource<? extends CacheResult<T>> apply(@NonNull Throwable throwable) throws Exception {
                            return Observable.empty();
                        }
                    });
        }
        return observable;
    }
}
