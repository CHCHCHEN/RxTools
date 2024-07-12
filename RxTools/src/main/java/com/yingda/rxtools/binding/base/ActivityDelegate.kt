package com.yingda.rxtools.binding.base

import android.app.Activity
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.viewbinding.ViewBinding
import com.yingda.rxtools.binding.observerWhenDestroyed
import com.yingda.rxtools.binding.registerLifecycleBelowQ
import kotlin.properties.ReadOnlyProperty


abstract class ActivityDelegate<T : ViewBinding>(
    activity: Activity
) : ReadOnlyProperty<Activity, T> {

    protected var viewBinding: T? = null
    private val LIFECYCLE_FRAGMENT_TAG = "com.yingda.rxtools.binding.lifecycle_fragment"

    init {
        when (activity) {
            is ComponentActivity -> activity.lifecycle.observerWhenDestroyed { destroyed() }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    activity.observerWhenDestroyed { destroyed() }
                }
            }
        }

    }

    fun addLifecycleFragment(activity: Activity) {
        activity.registerLifecycleBelowQ {
            destroyed()
        }
    }

    private fun destroyed() {
        viewBinding = null
    }
}