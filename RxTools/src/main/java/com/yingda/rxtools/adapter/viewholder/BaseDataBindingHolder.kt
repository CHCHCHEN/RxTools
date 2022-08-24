package com.yingda.rxtools.adapter.viewholder

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * author: chen
 * data: 2022/8/24
 * des: 方便 DataBinding 的使用
*/
open class BaseDataBindingHolder<BD : ViewDataBinding>(view: View) : BaseViewHolder(view) {

    val dataBinding = DataBindingUtil.bind<BD>(view)
}