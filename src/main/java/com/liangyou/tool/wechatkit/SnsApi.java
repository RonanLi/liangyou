package com.liangyou.tool.wechatkit;

import com.liangyou.model.wechatModel.ApiResult;

/**
 * 网页授权获取 access_token API
 */
public class SnsApi
{
    private static String getUserInfo = "https://api.weixin.qq.com/sns/userinfo";
    
    /**
     * 获取用户个人信息
     * @param accessToken 调用凭证access_token
     * @param openId 普通用户的标识，对当前开发者帐号唯一
     * @return ApiResult
     */
    public static ApiResult getUserInfo(String accessToken, String openId)
    {
        ParamMap pm = ParamMap.create("access_token", accessToken).put("openid", openId).put("lang", "zh_CN");
        return new ApiResult(HttpUtils.doGet(getUserInfo, pm.getData()));
    }
    
}
