package com.yingda.rxtools.wechat.listener

import com.yingda.rxtools.wechat.net.response.AccessTokenInfo
import com.yingda.rxtools.wechat.net.response.WeChatUserInfo

/**
 * author: chen
 * data: 2021-8-30
 * des: 微信登录回调接口
 */
interface OnWeChatAuthLoginListener {

    /**
     * 微信授权登录开始
     */
    fun onWeChatAuthLoginStart()

    /**
     * 微信授权登录成功
     */
    fun onWeChatAuthLoginSuccess(accessTokenInfo: AccessTokenInfo?, weChatUserInfo: WeChatUserInfo?)

    /**
     * 用户取消微信授权登录
     */
    fun onWeChatAuthLoginCancel()

    /**
     * 微信授权登录被拒绝
     * 检查包名或签名与注册信息是否相符
     */
    fun onWeChatAuthLoginAuthDenied()

    /**
     * 微信授权登录错误
     */
    fun onWeChatAuthLoginError(errorCode: Int?, errorMessage: String?)
}