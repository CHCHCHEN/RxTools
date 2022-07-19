package com.yingda.rxtools.interfaces
/**
 * author: chen
 * data: 2022/7/19
 * des:
*/
interface OnWebViewLoad {
    fun onPageStarted()
    fun onReceivedTitle(title: String)
    fun onProgressChanged(newProgress: Int)
    fun shouldOverrideUrlLoading()
    fun onPageFinished()
}