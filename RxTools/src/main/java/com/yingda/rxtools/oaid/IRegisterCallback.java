package com.yingda.rxtools.oaid;

/**
 * author: chen
 * data: 2024/8/18
 * des:
*/
public interface IRegisterCallback {
    /**
     * @deprecated 使用{@link #onComplete(String)}代替
     */
    @Deprecated
    default void onComplete() {
        onComplete("", null);
    }

    /**
     * 启动时注册完成回调，
     *
     * @param clientId 客户端标识按优先级尝试获取IMEI/MEID、OAID、AndroidID、GUID。
     * @param error    OAID获取失败时的异常信息
     */
    default void onComplete(String clientId, Exception error) {
    }
}
