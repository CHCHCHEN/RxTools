package com.yingda.rxtools.adapter.diff;

import androidx.annotation.NonNull;

/**
 * author: chen
 * data: 2022/8/24
 * des: 使用java接口定义方法
*/
public interface DifferImp<T> {
    void addListListener(@NonNull ListChangeListener<T> listChangeListener);
}
