package com.yingda.rxtools.binding.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.yingda.rxtools.binding.ext.observerWhenCreated
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
            val fragmentManager = fragment.parentFragmentManager
            fragmentManager.registerFragmentLifecycleCallbacks(object :
                FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentViewDestroyed(fm, f)
                    // 检查 fragment 的目的，为了防止类似于加载多个 Fragment 场景销毁的时候，出现不必要的异常
                    if (f == fragment) {
                        destroyed()
                        fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                    }
                    
                }
            }, false)
        }
    }
    
    private fun destroyed() {
        viewBinding = null
    }
}