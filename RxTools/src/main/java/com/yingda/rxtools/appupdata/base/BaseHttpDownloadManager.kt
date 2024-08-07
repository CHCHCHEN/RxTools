package com.yingda.rxtools.appupdate.base

import com.yingda.rxtools.appupdate.base.bean.DownloadStatus
import kotlinx.coroutines.flow.Flow
/**
 * author: chen
 * data: 2024/8/7
 * des:
*/

abstract class BaseHttpDownloadManager {
    /**
     * download apk from apkUrl
     *
     * @param apkUrl
     * @param apkName
     */
    abstract fun download(apkUrl: String, apkName: String): Flow<DownloadStatus>

    /**
     * cancel download apk
     */
    abstract fun cancel()

    /**
     * release memory
     */
    abstract fun release()
}