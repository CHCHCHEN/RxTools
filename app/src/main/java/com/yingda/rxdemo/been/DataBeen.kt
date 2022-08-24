package com.yingda.rxdemo.been

data class DataBeen(
    val code: Int,
    val msg: String,
    val newslist: List<Newslist>
) {
    data class Newslist(
        val digest: String,
        val hotnum: Int,
        val title: String
    )
}