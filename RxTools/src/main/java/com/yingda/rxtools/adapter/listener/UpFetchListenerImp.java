package com.yingda.rxtools.adapter.listener;

import androidx.annotation.Nullable;

/**
 * author: chen
 * data: 2022/8/24
 * des: UpFetch需要设置的接口,使用java定义,以兼容java写法
*/
public interface UpFetchListenerImp {
    void setOnUpFetchListener(@Nullable OnUpFetchListener listener);
}
