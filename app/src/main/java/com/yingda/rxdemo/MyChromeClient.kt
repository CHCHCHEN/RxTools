package com.yingda.rxdemo

import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebView

/**
 * author: chen
 * data: 2023/4/20
 * des:
 */
class MyChromeClient: WebChromeClient() {
    override fun onJsAlert(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
        return true
    }
}