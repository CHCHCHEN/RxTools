package com.yingda.rxtools.view

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 * Author: CHEN
 * date:2024/8/13
 * des:
 */
class MarqueeView : AppCompatTextView {
    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        //设置单行
        setSingleLine()
        //设置Ellipsize
        ellipsize = TextUtils.TruncateAt.MARQUEE
        //获取焦点
        isFocusable = true
        //走马灯的重复次数，-1代表无限重复
        marqueeRepeatLimit = -1
        //强制获得焦点
        isFocusableInTouchMode = true
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun isFocused(): Boolean {
        return true
    }
}
