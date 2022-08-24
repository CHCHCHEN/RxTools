package com.yingda.rxtools.http.callback;

import java.lang.reflect.Type;

/**
 * author: chen
 * data: 2022/8/24
 * des: 获取类型接口
*/
public interface IType<T> {
    Type getType();
}
