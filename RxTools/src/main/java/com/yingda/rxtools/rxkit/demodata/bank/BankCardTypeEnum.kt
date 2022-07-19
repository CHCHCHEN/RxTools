package com.yingda.rxtools.demodata.bank

/**
 * author: chen
 * data: 2022/7/19
 * des: 银行卡类型枚举类
*/
enum class BankCardTypeEnum(private val cardName: String) {
    /**
     * 借记卡/储蓄卡
     */
    DEBIT("借记卡/储蓄卡"),

    /**
     * 信用卡/贷记卡
     */
    CREDIT("信用卡/贷记卡");

}