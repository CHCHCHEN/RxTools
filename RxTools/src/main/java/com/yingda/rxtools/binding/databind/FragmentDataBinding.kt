package com.yingda.rxtools.binding.databind

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.yingda.rxtools.binding.base.FragmentDelegate
import com.yingda.rxtools.binding.ext.inflateMethod
import kotlin.reflect.KProperty

/**
 * author: chen
 * data: 2021/9/8
 * des: 
*/

class FragmentDataBinding<T : ViewDataBinding>(
    classes: Class<T>,
    val fragment: Fragment,
    private var block: (T.() -> Unit)? = null
) : FragmentDelegate<T>(fragment) {

    private val layoutInflater = classes.inflateMethod()

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        return viewBinding?.run {
            return this

        } ?: let {

            val bind: T
            if (thisRef.view == null) {
                // 这里为了兼容在 navigation 中使用 Fragment
                bind = layoutInflater.invoke(null, thisRef.layoutInflater) as T
            } else {
                bind = DataBindingUtil.bind(thisRef.view!!)!!
            }

            return bind.apply {
                viewBinding = this
                lifecycleOwner = fragment.viewLifecycleOwner
                block?.invoke(this)
                block = null
            }
        }
    }
}