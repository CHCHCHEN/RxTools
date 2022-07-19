package com.yingda.rxtools.wechat.listener

/**
 * author: chen
 * data: 2021-8-30
 * des: 微信支付参数
 */
interface IPaymentParams {

    fun onAppId(): String

    fun onPartnerId(): String

    fun onPrepayId(): String

    fun onPackageValue(): String

    fun onNonceStr(): String

    fun onTimeStamp(): String

    fun onSign(): String
}