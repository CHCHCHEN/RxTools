package com.yingda.rxtools.wechat.listener
/**
 * author: chen
 * data: 2021-8-30
 * des: 微信支付回调接口
 */
interface OnWeChatPaymentListener {

    /**
     * 微信支付开始
     */
    fun onWeChatPaymentStart()

    /**
     * 微信支付成功
     */
    fun onWeChatPaymentSuccess()

    /**
     * 用户取消微信支付
     */
    fun onWeChatPaymentCancel()

    /**
     * 微信支付被拒绝
     * 检查包名或签名与注册信息是否相符
     */
    fun onWeChatPaymentAuthDenied()

    /**
     * 微信支付错误
     */
    fun onWeChatPaymentError(errorCode: Int?, errorMessage: String?)
}