package com.yingda.rxtools.log.parser;

import com.yingda.rxtools.log.common.LogConstant;

/**
 * author: chen
 * data: 2022/7/31
 * des: 解析器接口
*/
public interface Parser<T> {
    String LINE_SEPARATOR = LogConstant.BR;

    Class<T> parseClassType();

    String parseString(T t);
}
