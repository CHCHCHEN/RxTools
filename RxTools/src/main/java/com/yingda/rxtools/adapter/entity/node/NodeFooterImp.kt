package com.yingda.rxtools.adapter.entity.node

/**
 * author: chen
 * data: 2022/8/24
 * des: 可以实现此接口，返回脚部节点
*/
interface NodeFooterImp {
    /**
     * 返回脚部节点
     * @return BaseNode? 如果返回 null，则代表没有脚部节点
     */
    val footerNode: BaseNode?
}