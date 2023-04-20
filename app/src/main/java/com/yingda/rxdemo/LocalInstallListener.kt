package com.n.web

/**
 *
 * @description 离线内核安装监听
 * @author N
 * @date 创建时间 2022/11/20
 * @version
 **/
interface LocalInstallListener {

    fun onSuccess()

    fun onError(message: String)
}