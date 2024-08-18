package com.yingda.rxtools.oaid;

/**
 * author: chen
 * data: 2024/8/18
 * des: OAID获取回调
*/
public interface IGetter {

    /**
     * 成功获取到OAID
     *
     * @param result OAID
     */
    void onOAIDGetComplete(String result);

    /**
     * OAID获取失败（不正常或获取不到）
     *
     * @param error 异常信息
     */
    void onOAIDGetError(Exception error);

}
