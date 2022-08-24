package com.yingda.rxtools.adapter.module

import com.yingda.rxtools.adapter.BaseQuickAdapter
import com.yingda.rxtools.adapter.listener.OnUpFetchListener
import com.yingda.rxtools.adapter.listener.UpFetchListenerImp

/**
 * author: chen
 * data: 2022/8/24
 * des: 向上加载
 *
 * 需要【向上加载更多】功能的，[BaseQuickAdapter]继承此接口
*/

interface UpFetchModule {
    /**
     * 重写此方法，返回自定义模块
     * @param baseQuickAdapter BaseQuickAdapter<*, *>
     * @return BaseUpFetchModule
     */
    fun addUpFetchModule(baseQuickAdapter: BaseQuickAdapter<*, *>): BaseUpFetchModule {
        return BaseUpFetchModule(baseQuickAdapter)
    }
}

open class BaseUpFetchModule(private val baseQuickAdapter: BaseQuickAdapter<*, *>) : UpFetchListenerImp {

    private var mUpFetchListener: OnUpFetchListener? = null

    var isUpFetchEnable = false
    var isUpFetching = false
    /**
     * start up fetch position, default is 1.
     */
    var startUpFetchPosition = 1

    internal fun autoUpFetch(position: Int) {
        if (!isUpFetchEnable || isUpFetching) {
            return
        }
        if (position <= startUpFetchPosition) {
            mUpFetchListener?.onUpFetch()
        }
    }

    override fun setOnUpFetchListener(listener: OnUpFetchListener?) {
        this.mUpFetchListener = listener
    }
}
