package com.liangyou.util;

import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StrHelper {

    //是否为空字符
	public static boolean isEmpty(String str){
        return str == null || str.trim().length() == 0;
    }
	
	/**
	 * 判断字符串是否为数字函数，正则表达式
	 */
	public static boolean isDigitalChar(String strNumber){
		if(isEmpty(strNumber)) return false;
		
		Pattern p = Pattern.compile("[^0-9]");
		Matcher m = p.matcher(strNumber);
		return !m.find();
	}
	
	public static Long getLong(String strValue,long replaceIfNull){
        if(isEmpty(strValue))	return replaceIfNull;
        try {
        	if(isDigitalChar(strValue))
        		return Long.valueOf(strValue);
        }
        catch(Exception ex){}
        
        return replaceIfNull;
    }
	
	public static String getString(Object strValue){
        return getString(strValue, "");
    }
	
	public static String getString(Object strValue, String replaceIfNull){
        if(strValue == null)
            return replaceIfNull;
        else
            return strValue.toString();
    }

	public static String expandStr(String str, int len, char ch, boolean fillOnLeft){
        int nLen = str.length();
        if(len <= nLen)
            return str;
        String sRet = str;
        for(int i = 0; i < len - nLen; i++)
            sRet = fillOnLeft ? String.valueOf(ch) + String.valueOf(sRet) : String.valueOf(sRet) + String.valueOf(ch);

        return sRet;
    }

	public static String setEndswith(String str, String ch) {
        if(str == null)	return null;
        if(!str.endsWith(ch))
            return str + ch;
        else
            return str;
    }
	
	public static String replaceStr(String strSrc, String strOld, String strNew){
        if(strSrc == null)	return null;
        char srcBuff[] = strSrc.toCharArray();
        int nSrcLen = srcBuff.length;
        if(nSrcLen == 0)	return "";
        char oldStrBuff[] = strOld.toCharArray();
        int nOldStrLen = oldStrBuff.length;
        if(nOldStrLen == 0 || nOldStrLen > nSrcLen)
            return strSrc;
        StringBuffer retBuff = new StringBuffer(nSrcLen * (1 + strNew.length() / nOldStrLen));
        boolean bIsFound = false;
        for(int i = 0; i < nSrcLen;){
            bIsFound = false;
            if(srcBuff[i] == oldStrBuff[0]){
                int j;
                for(j = 1; j < nOldStrLen && i + j < nSrcLen && srcBuff[i + j] == oldStrBuff[j]; j++);
                bIsFound = (j == nOldStrLen);
            }
            if(bIsFound){
                retBuff.append(strNew);
                i += nOldStrLen;
            }
            else {
                int nSkipTo;
                if(i + nOldStrLen >= nSrcLen)
                    nSkipTo = nSrcLen - 1;
                else
                    nSkipTo = i;
                while(i <= nSkipTo){
                    retBuff.append(srcBuff[i]);
                    i++;
                }
            }
        }

        srcBuff = null;
        oldStrBuff = null;
        return retBuff.toString();
    }

    public static String replaceStr(StringBuffer strSrc, String strOld, String strNew){
        if(strSrc == null)
            return null;
        int nSrcLen = strSrc.length();
        if(nSrcLen == 0)
            return "";
        char oldStrBuff[] = strOld.toCharArray();
        int nOldStrLen = oldStrBuff.length;
        if(nOldStrLen == 0 || nOldStrLen > nSrcLen)
            return strSrc.toString();
        StringBuffer retBuff = new StringBuffer(nSrcLen * (1 + strNew.length() / nOldStrLen));
        boolean bIsFound = false;
        for(int i = 0; i < nSrcLen;){
            bIsFound = false;
            if(strSrc.charAt(i) == oldStrBuff[0]){
                int j;
                for(j = 1; j < nOldStrLen && i + j < nSrcLen && strSrc.charAt(i + j) == oldStrBuff[j]; j++);
                bIsFound = j == nOldStrLen;
            }
            if(bIsFound){
                retBuff.append(strNew);
                i += nOldStrLen;
            }
            else{
                int nSkipTo;
                if(i + nOldStrLen >= nSrcLen)
                    nSkipTo = nSrcLen - 1;
                else
                    nSkipTo = i;
                while(i <= nSkipTo) {
                    retBuff.append(strSrc.charAt(i));
                    i++;
                }
            }
        }

        oldStrBuff = null;
        return retBuff.toString();
    }

    public static String byteToString(byte bytes[], char ch, int radix) {
        String sRet = "";
        for(int i = 0; i < bytes.length; i++){
            if(i > 0)
                sRet = String.valueOf(String.valueOf(sRet)).concat(",");
            sRet = String.valueOf(sRet) + String.valueOf(Integer.toString(bytes[i], radix));
        }

        return sRet;
    }

    //截取字符串
    public static String truncateStr(String _string, int _maxLength){
        return truncateStr(_string, _maxLength, "..");
    }

    public static String truncateStr(String _string, int _maxLength, String _sExt){
        if(_string == null)
            return null;
        String sExt = "..";
        if(_sExt != null)
            sExt = _sExt;
        int nExtLen = getBytesLength(sExt);
        if(nExtLen >= _maxLength)
            return _string;
        int nMaxLen = (_maxLength - nExtLen) + 1;
        char srcBuff[] = _string.toCharArray();
        int nLen = srcBuff.length;
        StringBuffer dstBuff = new StringBuffer(nLen + 2);
        int nGet = 0;
        int i = 0;
        do {
            if(i >= nLen)
                break;
            char aChar = srcBuff[i];
            boolean bUnicode = false;
            int j = 0;
            if(aChar == '&'){
                for(j = i + 1; j < nLen && j < i + 9 && !bUnicode; j++){
                    char cTemp = srcBuff[j];
                    if(cTemp != ';')
                        continue;
                    if(j == i + 5){
                        bUnicode = false;
                        j = 0;
                        break;
                    }
                    bUnicode = true;
                }

                nGet++;
            }
            else{
                nGet += aChar > '\177' ? 2 : 1;
            }
            if(nGet >= nMaxLen){
                if(nGet == _maxLength && i == nLen - 1){
                    dstBuff.append(aChar);
                    for(; i < j - 1; i++)
                        dstBuff.append(srcBuff[i + 1]);
                }
                else {
                    dstBuff.append(sExt);
                }
                break;
            }
            dstBuff.append(aChar);
            for(; i < j - 1; i++)
                dstBuff.append(srcBuff[i + 1]);

            i++;
        } while(true);
        return dstBuff.toString();
    }
    
    public static int getBytesLength(String str) {
        if(str == null)
            return 0;
        char srcBuff[] = str.toCharArray();
        int nGet = 0;
        for(int i = 0; i < srcBuff.length; i++){
            char aChar = srcBuff[i];
            nGet += aChar > '\177' ? 2 : 1;
        }

        return nGet;
    }
    
    public static String[] split(String str, String regx) throws Exception {
	    if(isEmpty(str)) return null;
	    return str.split(regx);
    }
    
    public static String transDisplay(String content){
        return transDisplay(content, true);
    }

    public static String transDisplay(String content, boolean changeBlank){
        if(content == null)	return "";
        char srcBuff[] = content.toCharArray();
        int nSrcLen = srcBuff.length;
        StringBuffer retBuff = new StringBuffer(nSrcLen * 2);
        for(int i = 0; i < nSrcLen; i++){
            char cTemp = srcBuff[i];
            switch(cTemp){
            case 32: // ' '
                retBuff.append(changeBlank ? "&nbsp;" : " ");
                break;
            case 60: // '<'
                retBuff.append("&lt;");
                break;
            case 62: // '>'
                retBuff.append("&gt;");
                break;
            case 10: // '\n'
                retBuff.append("<br>");
                break;
            case 34: // '"'
                retBuff.append("&quot;");
                break;
            case 38: // '&'
                boolean bUnicode = false;
                for(int j = i + 1; j < nSrcLen && !bUnicode; j++){
                    cTemp = srcBuff[j];
                    if(cTemp == '#' || cTemp == ';'){
                        retBuff.append("&");
                        bUnicode = true;
                    }
                }

                if(!bUnicode)
                    retBuff.append("&amp;");
                break;
            case 9: // '\t'
                retBuff.append(changeBlank ? "&nbsp;&nbsp;&nbsp;&nbsp;" : "    ");
                break;
            default:
                retBuff.append(cTemp);
                break;
            }
        }

        return retBuff.toString();
    }

    public static String encode(String aValue) {
		if (aValue == null)	return null;
		try {
			return (URLEncoder.encode(aValue, "UTF-8"));
		}
		catch (Exception ex) {
			return aValue;
		}
	}

	public static String decode(String aValue) {
		if (aValue == null)	return null;
		try {
			return new String(aValue.getBytes("ISO-8859-1"), "UTF-8");
		}
		catch (Exception ex) {
			return aValue;
		}
	}
	
	public static int changeObjToInt(Object obj) {
		if (obj == null)
			return 0;
		else
			return (int)(Double.parseDouble(String.valueOf(obj)));
	}
	
	public static Object toDouble(double d){
		String r="0";
		try{
			DecimalFormat df1 = new DecimalFormat("#0.00"); 
		    r = df1.format(d);
		    if(r.equals("00.00")||r.equals("0.00"))
				return 0;
		}catch(Exception ex){}
		return r;
	}
}

