package com.liangyou.tool.jcaptcha;

import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;

/**
 * JCaptcha Ajax获取验证码 的单例
 * 
 * @author young
 * 
 */
public class AjaxCaptchaServiceSingleton {

	private static CustomGenericManageableCaptchaService instance = new CustomGenericManageableCaptchaService(new FastHashMapCaptchaStore(), new RdImageEngine(), 180, 100000, 75000);

	public static CustomGenericManageableCaptchaService getInstance() {
		return instance;
	}
}