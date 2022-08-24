package com.yingda.rxtools.adapter.binder

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.yingda.rxtools.adapter.util.getItemView
import com.yingda.rxtools.adapter.viewholder.BaseViewHolder


/**
 * author: chen
 * data: 2022/8/24
 * des: 使用布局 ID 快速构建 Binder
 *
 * @param T item 数据类型
 */
abstract class QuickItemBinder<T> : BaseItemBinder<T, BaseViewHolder>() {
    
    @LayoutRes
    abstract fun getLayoutId(): Int
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(parent.getItemView(getLayoutId()))
    
}

