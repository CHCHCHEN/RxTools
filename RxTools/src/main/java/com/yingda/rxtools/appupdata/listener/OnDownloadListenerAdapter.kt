package com.yingda.rxtools.appupdate.listener

import java.io.File
/**
 * author: chen
 * data: 2024/8/7
 * des:
*/
abstract class OnDownloadListenerAdapter : OnDownloadListener {
    override fun start() {
    }

    override fun downloading(max: Int, progress: Int) {
    }

    override fun done(apk: File) {
    }

    override fun cancel() {
    }

    override fun error(e: Throwable) {
    }
}