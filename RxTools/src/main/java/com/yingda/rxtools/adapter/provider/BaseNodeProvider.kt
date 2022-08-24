package com.yingda.rxtools.adapter.provider

import com.yingda.rxtools.adapter.BaseNodeAdapter
import com.yingda.rxtools.adapter.entity.node.BaseNode

/**
 * author: chen
 * data: 2022/8/24
 * des:
*/
abstract class BaseNodeProvider : BaseItemProvider<BaseNode>() {

    override fun getAdapter(): BaseNodeAdapter? {
        return super.getAdapter() as? BaseNodeAdapter
    }

}