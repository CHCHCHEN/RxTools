package com.yingda.rxtools.model

/**
 * author: chen
 * data: 2022/7/19
 * des: 功能描述：弹窗内部子类项（绘制标题和图标）
*/
class ActionItem {
    /**
     * 定义文本对象
     */
    @JvmField
    var mTitle: CharSequence

    @JvmField
    var mResourcesId = 0

    constructor(title: CharSequence, mResourcesId: Int) {
        this.mResourcesId = mResourcesId
        mTitle = title
    }

    constructor(title: CharSequence) {
        mTitle = title
    }
}