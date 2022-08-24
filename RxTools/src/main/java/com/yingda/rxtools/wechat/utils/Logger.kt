package com.yingda.rxtools.wechat.utils

import com.yingda.rxtools.log.ViseLog
import com.yingda.rxtools.wechat.WeChatHelper

/**
 * author: chen
 * data: 2021-8-30
 * des: 微信SDK-LOG
 */
object Logger {
    fun d(log: String?) {
        if (WeChatHelper.IS_LOGGABLE) {
            ViseLog.d("微信SDK-LOG: " + log)
        }
    }
}