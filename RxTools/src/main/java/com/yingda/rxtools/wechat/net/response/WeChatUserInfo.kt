package com.yingda.rxtools.wechat.net.response

import java.io.Serializable

class WeChatUserInfo : WeChatBaseResponseInfo(), Serializable {
    // {
    //	"openid": "oDn8S6at6XVTx9V8EaQM8RXWCIZs",
    //	"nickname": "阿白",
    //	"sex": 0,
    //	"language": "zh_CN",
    //	"city": "",
    //	"province": "",
    //	"country": "",
    //	"headimgurl": "https://thirdwx.qlogo.cn/mmopen/vi_32/Q3auHgzwzM693ynoVXfpicnorrJJicZav86t5E1piaHPxtSbNO0rPS2wUx869IibaLrwUSDfkzynKI78dtHMSicPd6A/132",
    //	"privilege": [],
    //	"unionid": "oCpnz6Jd_p61mIjeFSS5AjzgW0_k"
    //}

    val openid: String? = null
    val nickname: String? = null
    val sex: String? = null
    val language: String? = null
    val city: String? = null
    val province: String? = null
    val country: String? = null
    val headimgurl: String? = null
    val privilege: Any? = null
    val unionid: String? = null

    /**
     * 接口是否反正正确
     */
    fun isSuccess(): Boolean {

        // 错误码或错误信息不为空
        if (null != errcode || !errmsg.isNullOrEmpty()) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "WeChatUserInfo(openid=$openid, nickname=$nickname, sex=$sex, language=$language, city=$city, province=$province, country=$country, headimgurl=$headimgurl, privilege=$privilege, unionid=$unionid)"
    }

}
