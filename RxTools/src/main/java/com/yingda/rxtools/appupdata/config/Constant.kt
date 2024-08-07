package com.yingda.rxtools.appupdate.config

/**
 * author: chen
 * data: 2024/8/7
 * des:
*/
object Constant {

    /**
     * Http timeout(ms)
     */
    const val HTTP_TIME_OUT = 30_000

    /**
     * Logcat tag
     */
    const val TAG = "AppUpdate."

    /**
     * Apk file extension
     */
    const val APK_SUFFIX = ".apk"

    /**
     * Coroutine Name
     */
    const val COROUTINE_NAME = "app-update-coroutine"

    /**
     * Notification channel id
     */
    const val DEFAULT_CHANNEL_ID = "appUpdate"

    /**
     * Notification id
     */
    const val DEFAULT_NOTIFY_ID = 1011

    /**
     * Notification channel name
     */
    const val DEFAULT_CHANNEL_NAME = "AppUpdate"

    /**
     * Compat Android N file uri
     */
    var AUTHORITIES: String? = null

    /**
     * Apk path
     */
    var APK_PATH = "/storage/emulated/0/Android/data/%s/cache"
}