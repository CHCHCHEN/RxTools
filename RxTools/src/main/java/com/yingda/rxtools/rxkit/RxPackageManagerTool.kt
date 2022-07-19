package com.yingda.rxtools

import android.content.Context
import android.content.pm.PackageInfo
import java.util.*

/**
 * author: chen
 * data: 2022/7/19
 * des:
*/
object RxPackageManagerTool {
    @JvmStatic
    fun initPackageManager(mContext: Context): List<PackageInfo> {
        val mPackageManager = mContext.packageManager
        return mPackageManager.getInstalledPackages(0)
    }

    /**
     * 包名是否存在
     * @param mContext 实体
     * @param packageName 待检测 包名
     * @return 结果
     */
    @JvmStatic
    fun haveExistPackageName(mContext: Context, packageName: String): Boolean {
        val packageInfos = initPackageManager(mContext)
        val mPackageNames: MutableList<String> = ArrayList()
        for (i in packageInfos.indices) {
            mPackageNames.add(packageInfos[i].packageName)
        }
        return mPackageNames.contains(packageName)
    }
}