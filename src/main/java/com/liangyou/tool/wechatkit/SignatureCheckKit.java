package com.liangyou.tool.wechatkit;

import java.util.Arrays;

/**
 * @author lijing
 *
 *校验 signature
 */

public class SignatureCheckKit {
	public static final SignatureCheckKit sign = new SignatureCheckKit();
	public static boolean checkSignature(String signature,String timestamp,String nonce) {
		String token = ApiConfigKit.getApiConfig().getToken();
		String[] checkStrings = {token,timestamp,nonce};
		Arrays.sort(checkStrings);
		String checkString = new StringBuilder().append(checkStrings[0]+checkStrings[1]+checkStrings[2]).toString();
		checkString = HashKit.sha1(checkString);
		if(checkString.equals(signature)){
			return true;
		}
		return false;
	}
}
