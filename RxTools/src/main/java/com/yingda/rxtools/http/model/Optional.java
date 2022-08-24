package com.yingda.rxtools.http.model;

import io.reactivex.Observable;

/**
 * author: chen
 * data: 2022/8/24
 * des: 为了使Rxjava2 onNext 返回null,使用了此包装类，进行过渡
 */
public class Optional<T> {
    Observable<T> obs;

    public Optional(Observable<T> obs) {
        this.obs = obs;
    }

    public static <T> Optional<T> of(T value) {
        if (value == null) {
            throw new NullPointerException();
        } else {
            return new Optional<T>(Observable.just(value));
        }
    }

    public static <T> Optional<T> ofNullable(T value) {
        if (value == null) {
            return new Optional<T>(Observable.<T>empty());
        } else {
            return new Optional<T>(Observable.just(value));
        }
    }

    public T get() {
        return obs.blockingSingle();
    }

    public T orElse(T defaultValue) {
        return obs.defaultIfEmpty(defaultValue).blockingSingle();
    }
}
