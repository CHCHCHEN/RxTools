package com.yingda.rxdemo

import android.graphics.Bitmap
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient

/**
 * author: chen
 * data: 2023/4/20
 * des:
 */
class MyWebViewClient : WebViewClient() {

    override fun onPageStarted(p0: WebView?, p1: String?, p2: Bitmap?) {
        super.onPageStarted(p0, p1, p2)
    }

    override fun onPageFinished(p0: WebView?, p1: String?) {
        super.onPageFinished(p0, p1)
    }

    override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
        super.onReceivedSslError(p0, p1, p2)
        p1?.proceed()
    }

    override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
        return super.shouldOverrideUrlLoading(p0, p1)
    }
}