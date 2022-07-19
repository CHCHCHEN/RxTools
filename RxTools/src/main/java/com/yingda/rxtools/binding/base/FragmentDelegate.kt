package com.yingda.rxtools.binding.base

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.yingda.rxtools.binding.ext.observerWhenCreated
import com.yingda.rxtools.binding.ext.observerWhenDestroyed
import kotlin.properties.ReadOnlyProperty

/**
 * author: chen
 * data: 2021/9/8
 * des:
*/
abstract class FragmentDelegate<T : ViewBinding>(
    fragment: Fragment
) : ReadOnlyProperty<Fragment, T> {

    protected var viewBinding: T? = null

    init {

        /**
         *
         * 最原始的处理的方案 监听 Fragment 的生命周期，会存在 Fragment 和 Fragment 中的 View 生命周期不一致问题
         *
         */
        fragment.lifecycle.observerWhenCreated {
            fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewOwner ->
                viewOwner.lifecycle.observerWhenDestroyed {
                    destroyed()
                }
            }
        }

    }

    private fun destroyed() {
        viewBinding = null
    }
}