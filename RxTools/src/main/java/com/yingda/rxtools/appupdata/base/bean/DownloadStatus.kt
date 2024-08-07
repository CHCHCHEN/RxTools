package com.yingda.rxtools.appupdate.base.bean

import java.io.File
/**
 * author: chen
 * data: 2024/8/7
 * des:
*/
sealed class DownloadStatus {

    object Start : DownloadStatus()

    data class Downloading(val max: Int, val progress: Int) : DownloadStatus()

    class Done(val apk: File) : DownloadStatus()

    object Cancel : DownloadStatus()

    data class Error(val e: Throwable) : DownloadStatus()
}
