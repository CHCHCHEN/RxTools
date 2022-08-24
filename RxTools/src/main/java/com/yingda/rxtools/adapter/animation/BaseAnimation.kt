package com.yingda.rxtools.adapter.animation

import android.animation.Animator
import android.view.View

/**
 * author: chen
 * data: 2022/8/24
 * des: 
*/
interface BaseAnimation {
    fun animators(view: View): Array<Animator>
}