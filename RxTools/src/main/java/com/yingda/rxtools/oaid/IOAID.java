package com.yingda.rxtools.oaid;

/**
 * author: chen
 * data: 2024/8/18
 * des: OAID接口
*/
public interface IOAID {

    /**
     * 是否支持OAID
     *
     * @return 支持则返回true，不支持则返回false
     */
    boolean supported();

    /**
     * 异步获取OAID
     *
     * @param getter 回调
     */
    void doGet(IGetter getter);

}
