package com.n.web

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.yingda.rxtools.log.ViseLog
import java.io.File
import java.io.FileOutputStream
import java.util.*

class X5Util(context: Context, listener: LocalInstallListener) {

    private val TAG = "tbsX5Util"

    /**
     * 内核存放的文件夹
     */
    private val DIR =context.filesDir


    /**
     * 内核版本号
     */
    private val CORE_VERSION = 46141
//    private val CORE_VERSION = 46011

    /**
     * 内核文件名称
     */
    private val CORE_NAME = "tbs_core_046141_20220915165042_nolog_fs_obfs_arm64-v8a_release.tbs"

    /**
     * 内核文件路径
     */
    private val CORE_PATH = "$DIR/$CORE_NAME"

    /**
     * 上下文
     */
    private val mContext: Context = context

    /**
     * 离线内核安装监听
     */
    private val mLocalInstallListener: LocalInstallListener = listener

    /**
     * 是否初始化x5成功
     *
     * @param context 上下文环境
     * @return
     */
    fun isInited(context: Context?): Boolean {
        val version = QbSdk.getTbsVersion(context)
        return version > 0
    }

    /**
     * 开始安装x5内核
     */
    fun startInstallX5() {

        /**
         *  校验手机abi型号
         */
        var abi: String? = Build.CPU_ABI


        // 如果是v8a的，和内核型号匹配则直接进行离线内核安装
        if ("arm64-v8a" == abi) {
            val isExitCore = copyX5Core()
            if (isExitCore) {
                // 存在内核文件
                startInstallX5LocationCore()
            }
        } else {
            ViseLog.d("内核型号不匹配，无法进行本地离线安装内核")
        }
    }

    /**
     * 开始安装本地离线内核
     */
    private fun startInstallX5LocationCore() {
        try {
            QbSdk.installLocalTbsCore(mContext, CORE_VERSION, CORE_PATH)
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    QbSdk.initX5Environment(mContext, object : PreInitCallback {
                        override fun onCoreInitFinished() {
                            ViseLog.d( "onCoreInitFinished")
                        }

                        override fun onViewInitFinished(p0: Boolean) {
                            ViseLog.d("onViewInitFinished_p0=$p0")
//                            if (!p0) {
//                                QbSdk.initX5Environment(mContext, null)
//                            } else {
//                                if (QbSdk.getTbsVersion(mContext) > 0) {
//                                    mLocalInstallListener.onSuccess()
//                                    Log.d(TAG, "x5内核安装完成，版本号${QbSdk.getTbsVersion(mContext)}")
//                                }
//                            }
                        }
                    })
                    val version = QbSdk.getTbsVersion(mContext)
                    if (version > 0) {
                        timer.cancel()
                        mLocalInstallListener.onSuccess()
                    } else {
                        ViseLog.d( "循环检验内核版本$version")
                    }
                }
            }, 0, 1000)
        } catch (e: Exception) {
            ViseLog.d("本地离线内核安装异常,异常信息>${e.message}")
        }
    }

    /**
     * 将内核文件拷贝到指定目录
     */
    private fun copyX5Core(): Boolean {
        var file: File?
        try {
            // 目录存在，则将assets中的内核文件复制进去
            file = File(CORE_PATH)
            if (file.exists()) {
                // 如果文件存在，为了确保文件完整性，需要删除后重新从项目中复制进去
                file.delete()
            }

            // 开始复制文件到指定目录
            val ins = mContext.resources.assets.open(CORE_NAME)
            Log.d(TAG, "开始读取内核文件")
            val fos = FileOutputStream(file)
            Log.d(TAG, "开始拷贝内核文件")
            val buffer = ByteArray(1024)
            var count = 0
            // 循环写出
            while (ins.read(buffer).also { count = it } > 0) {
                fos.write(buffer, 0, count)
            }
            fos.close()
            ins.close()
            ViseLog.d("拷贝内核文件完成")
            return true
        } catch (e: Exception) {
            ViseLog.d( "拷贝内核文件异常，异常信息>${e.message}")
            mLocalInstallListener.onError(e.message.toString())
        }
        return false
    }
}