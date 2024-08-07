package com.yingda.rxtools.appupdate.listener
/**
 * author: chen
 * data: 2024/8/7
 * des:
*/

interface OnButtonClickListener {
    companion object {
        /**
         * click update button
         */
        const val UPDATE = 0

        /**
         * click cancel button
         */
        const val CANCEL = 1
    }

    fun onButtonClick(id: Int)
}