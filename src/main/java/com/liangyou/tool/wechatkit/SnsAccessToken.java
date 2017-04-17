
package com.liangyou.tool.wechatkit;

import java.io.Serializable;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.liangyou.model.wechatModel.ReturnCode;
import com.liangyou.tool.wechatkit.RetryUtils.ResultCheck;

/**
 * SnsAccessToken
 * 封装 access_token
 */
public class SnsAccessToken implements ResultCheck, Serializable 
{
    
    private static final long serialVersionUID = 6369625123403343963L;
    
    private String access_token;    // 正确获取到 access_token 时有值,此access_token与基础支持的access_token不同
    private Integer expires_in;        // 正确获取到 access_token 时有值,access_token接口调用凭证超时时间，单位（秒）
    private String refresh_token;    //用户刷新access_token
    private String openid;    //用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
    private String scope;    //用户授权的作用域，使用逗号（,）分隔
    private String unionid;    //只有绑定了开放平台账户才会出现该字段
    private Integer errcode;        // 出错时有值
    private String errmsg;            // 出错时有值

    private Long expiredTime;        // 正确获取到 access_token 时有值，存放过期时间
    private String json;

    public SnsAccessToken(String jsonStr)
    {
        this.json = jsonStr;

        try
        {
            @SuppressWarnings("unchecked")
            Map<String, Object> temp = JSON.parseObject(jsonStr, Map.class);
            access_token = (String) temp.get("access_token");
            expires_in = getInt(temp, "expires_in");
            refresh_token = (String) temp.get("refresh_token");
            openid = (String) temp.get("openid");
            unionid = (String) temp.get("unionid");
            scope = (String) temp.get("scope");
            errcode = getInt(temp, "errcode");
            errmsg = (String) temp.get("errmsg");

            if (expires_in != null)
                expiredTime = System.currentTimeMillis() + ((expires_in - 5) * 1000);

        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getJson()
    {
        return json;
    }

    private Integer getInt(Map<String, Object> temp, String key) {
        Number number = (Number) temp.get(key);
        return number == null ? null : number.intValue();
    }
    
    public boolean isAvailable()
    {
        if (expiredTime == null)
            return false;
        if (errcode != null)
            return false;
        if (expiredTime < System.currentTimeMillis())
            return false;
        return access_token != null;
    }

    public String getAccessToken()
    {
        return access_token;
    }

    public Integer getExpiresIn()
    {
        return expires_in;
    }

    public String getRefresh_token()
    {
        return refresh_token;
    }

    public String getOpenid()
    {
        return openid;
    }

    public String getScope()
    {
        return scope;
    }

    public Integer getErrorCode()
    {
        return errcode;
    }

    public String getErrorMsg()
    {
        if (errcode != null)
        {
            String result = ReturnCode.get(errcode);
            if (result != null)
                return result;
        }
        return errmsg;
    }

    public String getUnionid()
    {
        return unionid;
    }

	@Override
	public boolean matching() {
		return isAvailable();
	}

	@Override
	public String toString() {
		return "SnsAccessToken [access_token=" + access_token + ", expires_in=" + expires_in + ", refresh_token="
				+ refresh_token + ", openid=" + openid + ", scope=" + scope + ", unionid=" + unionid + ", errcode="
				+ errcode + ", errmsg=" + errmsg + ", expiredTime=" + expiredTime + ", json=" + json + "]";
	}
	
}
