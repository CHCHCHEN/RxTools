package com.yingda.rxdemo.app

import com.yingda.rxtools.log.ViseLog
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * Author: CHEN
 * date:2022-3-4
 * des: 全局访问规则
 */
class UnSafeHostnameVerifier(host: String) : HostnameVerifier {

    private val host: String?

    override fun verify(hostname: String, session: SSLSession?): Boolean {
        ViseLog.i("############### verify " + hostname + " " + host)
        return if (host == null || "" == host || !host.contains(hostname)) false else true
    }

    init {
        this.host = host
        ViseLog.i("###############　UnSafeHostnameVerifier $host")
    }
}