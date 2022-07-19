package com.yingda.rxtools.interfaces

import java.io.File

/**
 * author: chen
 * data: 2022/7/19
 * des: 
*/
interface OnRxCamera {
    fun onBefore()
    fun onSuccessCompress(filePhoto: File?)
    fun onSuccessExif(filePhoto: File?)
}