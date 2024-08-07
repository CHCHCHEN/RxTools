package com.yingda.rxtools.appupdate.util

import android.content.Context
/**
 * author: chen
 * data: 2024/8/7
 * des:
*/

class DensityUtil {
    companion object {
        fun dip2px(context: Context, dpValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dpValue * scale + 0.5f
        }
    }
}