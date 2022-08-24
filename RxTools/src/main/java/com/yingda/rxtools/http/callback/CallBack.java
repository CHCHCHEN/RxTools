package com.yingda.rxtools.http.callback;

import com.yingda.rxtools.http.exception.ApiException;
import com.yingda.rxtools.http.utils.Utils;

import java.lang.reflect.Type;

/**
 * author: chen
 * data: 2022/8/24
 * des: 网络请求回调
 */
public abstract class CallBack<T> implements IType<T> {
    public abstract void onStart();

    public abstract void onCompleted();

    public abstract void onError(ApiException e);

    public abstract void onSuccess(T t);

    @Override
    public Type getType() {//获取需要解析的泛型T类型
        return Utils.findNeedClass(getClass());
    }

    public Type getRawType() {//获取需要解析的泛型T raw类型
        return Utils.findRawType(getClass());
    }
}
