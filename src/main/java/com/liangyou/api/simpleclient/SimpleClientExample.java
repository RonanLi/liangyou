/*
 * $Id: SimpleClientExample.java 1600 2009-06-23 02:38:46Z hyyang $
 * $Revision: 1600 $
 */

package com.liangyou.api.simpleclient;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Mlink下行请求java示例 <br>
 * <Ul>
 * <Li>本示例定义几种下行请求消息的使用方法</Li>
 * <Li>本示例支持 jre1.5 或以上版本</Li>
 * <Li>本示例依赖于 commons-codec，commons-httpclient，commons-logging等几个jar包</Li>
 * </Ul>
 * @author hyyang
 * @since 1.5
 */
public class SimpleClientExample {

    private String mtUrl="http://esms.etonenet.com/sms/mt";

    /**
     * 启动测试
     * @param args
     */
    public static void main(String[] args) {
        SimpleClientExample test = new SimpleClientExample();
        try {
          //测试单条下行
          test.testSingleMt();
          //测试相同内容群发
          //test.testMultiMt();
          //测试不同内容群发
          //test.testMultiXMt();
          //测试文件群发
          //test.testBatchMt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //以下四个方法分别给出几种下行请求消息的使用示例
    /**
     * 单条下行实例
     * @throws Exception
     */
    public void testSingleMt() throws Exception {
        //操作命令、SP编号、SP密码，必填参数
        String command = "MT_REQUEST";
        String spid = "7834";
        String sppassword = "pj7834";
        //sp服务代码，可选参数，默认为 00
        String spsc = "00";
        //源号码，可选参数
        String sa = "10657109053657";
        //目标号码，必填参数
        String da = "8618358730161";
        //下行内容以及编码格式，必填参数
        int dc = 15;
        String ecodeform = "GBK";
        String sm = new String(Hex.encodeHex("你好,这是移通网络单条下行测试短信".getBytes(ecodeform)));

        //组成url字符串
        StringBuilder smsUrl = new StringBuilder();
        smsUrl.append(mtUrl);
        smsUrl.append("?command=" + command);
        smsUrl.append("&spid=" + spid);
        smsUrl.append("&sppassword=" + sppassword);
        smsUrl.append("&spsc=" + spsc);
        smsUrl.append("&sa=" + sa);
        smsUrl.append("&da=" + da);
        smsUrl.append("&sm=" + sm);
        smsUrl.append("&dc=" + dc);

        //发送http请求，并接收http响应
        String resStr = doGetRequest(smsUrl.toString());
        System.out.println(resStr);
        
        //解析响应字符串
        HashMap<String,String> pp = parseResStr(resStr);
        System.out.println(pp.get("command"));
        System.out.println(pp.get("spid"));
        System.out.println(pp.get("mtmsgid"));
        System.out.println(pp.get("mtstat"));
        System.out.println(pp.get("mterrcode"));
    }

    /**
     * 相同内容群发实例
     * @throws Exception
     */
    public void testMultiMt() throws Exception {
        //操作命令、SP编号、SP密码，必填参数
        String command = "MULTI_MT_REQUEST";
        String spid = "1234";
        String sppassword = "1234";
        //sp服务代码，可选参数，默认为 00
        String spsc = "00";
        //源号码，可选参数
        String sa = "10657109053657";
        //目标号码组，必填参数
        String das = "8613811111111,8613011111111";
        //下行内容以及编码格式，必填参数
        int dc = 15;
        String ecodeform = "GBK";
        String sm = new String(Hex.encodeHex("你好,这是移通网络相同内容群发测试短信".getBytes(ecodeform)));

        //组成url字符串
        StringBuilder smsUrl = new StringBuilder();
        smsUrl.append(mtUrl);
        smsUrl.append("?command=" + command);
        smsUrl.append("&spid=" + spid);
        smsUrl.append("&sppassword=" + sppassword);
        smsUrl.append("&spsc=" + spsc);
        smsUrl.append("&sa=" + sa);
        smsUrl.append("&das=" + das);
        smsUrl.append("&sm=" + sm);
        smsUrl.append("&dc=" + dc);

        //发送http请求，并接收http响应
        String resStr = doGetRequest(smsUrl.toString());
        System.out.println(resStr);
        
        //解析响应字符串
        HashMap<String,String> pp = parseResStr(resStr);
        System.out.println(pp.get("command"));
        System.out.println(pp.get("spid"));
        System.out.println(pp.get("mtmsgids"));
        System.out.println(pp.get("mtstat"));
        System.out.println(pp.get("mterrcode"));
    }

    /**
     * 不同内容群发实例
     * @throws Exception
     */
    public void testMultiXMt() throws Exception {
        //操作命令、SP编号、SP密码，必填参数
        String command = "MULTIX_MT_REQUEST";
        String spid = "1234";
        String sppassword = "1234";
        //sp服务代码，可选参数，默认为 00
        String spsc = "00";
        //源号码，可选参数
        String sa = "10657109053657";
        //编码格式，必填参数
        int dc = 15;
        String ecodeform = "GBK";
        //下行号码、内容列表

        StringBuffer dasms = new StringBuffer();
        dasms.append("8613811111111/");
        dasms.append(new String(Hex.encodeHex("你好,这是移通网络不同内容群发测试短信1".getBytes(ecodeform))));
        dasms.append(",");
        dasms.append("8613011111111/");
        dasms.append(new String(Hex.encodeHex("你好,这是移通网络不同内容群发测试短信2".getBytes(ecodeform))));

        //组成url字符串
        StringBuilder smsUrl = new StringBuilder();
        smsUrl.append(mtUrl);
        smsUrl.append("?command=" + command);
        smsUrl.append("&spid=" + spid);
        smsUrl.append("&sppassword=" + sppassword);
        smsUrl.append("&spsc=" + spsc);
        smsUrl.append("&sa=" + sa);
        smsUrl.append("&dc=" + dc);


        //发送http请求，并接收http响应
        String resStr = doPostRequest(smsUrl.toString(), dasms.toString());
        System.out.println(resStr);
        
        //解析响应字符串
        HashMap<String,String> pp = parseResStr(resStr);
        System.out.println(pp.get("command"));
        System.out.println(pp.get("spid"));
        System.out.println(pp.get("mtmsgids"));
        System.out.println(pp.get("mtstat"));
        System.out.println(pp.get("mterrcode"));
    }

    /**
     * 文件群发实例
     * @throws Exception
     */
    public void testBatchMt() throws Exception {
        //操作命令、SP编号、SP密码，必填参数
        String command = "BATCH_MT_REQUEST";
        String spid = "1234";
        String sppassword = "1234";
        //sp服务代码，可选参数，默认为 00
        String spsc = "00";
        //任务编号，可选参数
        int taskid = 10001;

        //*/相同内容文件群发
        int bmttype = 1;
        String title = java.net.URLEncoder.encode("abc,测试相同内容文件群发", "GBK");
        int dc = 15;
        String ecodeform = "GBK";
        String content = new String(Hex.encodeHex("你好，这是移通网络相同内容文件群发测试短信".getBytes(ecodeform)));
        String fileurl = "http://10.8.1.103:9001/testxiangtong.txt";
        //*/

        /*/不同内容文件群发
        int bmttype = 2;
        String title = java.net.URLEncoder.encode("abc,测试不同内容文件群发", "GBK");
        int dc = 15;
        String content = "";//此处content可置空
        String fileurl = "http://10.8.1.103:9001/testbutong.txt";
        //*/

        /*/动态模版文件群发，
        int bmttype = 3;
        String title = java.net.URLEncoder.encode("abc,测试动态模版文件群发", "GBK");
        int dc = 15;
        String ecodeform = "GBK";
        String content = new String(Hex.encodeHex("你好，这是移通网络动态模版群发测试短信，占位测试参数#p_1#，#p_2#".getBytes(ecodeform)));
        String fileurl = "http://10.8.1.103:9001/testdongtai2.txt";
        //*/

        //其他可选参数
        int priority = 1;//    优先级
        //String attime = "20081028163000";//  定时发送时间，格式yyyyMMddHHmmss
        //String validtime = "20081028173000";//   有效时间，格式yyyyMMddHHmmss

        //组成url字符串
        StringBuilder smsUrl = new StringBuilder();
        smsUrl.append(mtUrl);
        smsUrl.append("?command=" + command);
        smsUrl.append("&spid=" + spid);
        smsUrl.append("&sppassword=" + sppassword);
        smsUrl.append("&spsc=" + spsc);
        smsUrl.append("&taskid=" + taskid);
        smsUrl.append("&bmttype=" + bmttype);
        smsUrl.append("&title=" + title);
        smsUrl.append("&dc=" + dc);
        smsUrl.append("&content=" + content);
        smsUrl.append("&url=" + fileurl);
        smsUrl.append("&priority=" + priority);
        //smsUrl.append("&attime=" + attime);
        //smsUrl.append("&validtime=" + validtime);

        //发送http请求，并接收http响应
        String resStr = doGetRequest(smsUrl.toString());
        System.out.println(resStr);
        
        //解析响应字符串
        HashMap<String,String> pp = parseResStr(resStr);
        System.out.println(pp.get("command"));
        System.out.println(pp.get("spid"));
        System.out.println(pp.get("bmtmsgid"));
        System.out.println(pp.get("mtstat"));
        System.out.println(pp.get("mterrcode"));
    }
    
    /**
     * 将普通字符串转换成Hex编码字符串
     * 
     * @param dataCoding 编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
     * @param realStr 普通字符串
     * @return Hex编码字符串
     * @throws UnsupportedEncodingException 
     */
    public static String encodeHexStr(int dataCoding, String realStr) {
        String hexStr = null;
        if (realStr != null) {
            try {
                if (dataCoding == 15) {
                    hexStr = new String(Hex.encodeHex(realStr.getBytes("GBK")));
                } else if ((dataCoding & 0x0C) == 0x08) {
                    hexStr = new String(Hex.encodeHex(realStr.getBytes("UnicodeBigUnmarked")));
                } else {
                    hexStr = new String(Hex.encodeHex(realStr.getBytes("ISO8859-1")));
                }
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.toString());
            }
        }
        return hexStr;
    }
    
    /**
     * 将Hex编码字符串转换成普通字符串
     * 
     * @param dataCoding 反编码格式，15表示GBK编码，8表示UnicodeBigUnmarked编码，0表示ISO8859-1编码
     * @param hexStr Hex编码字符串
     * @return 普通字符串
     */
    public static String decodeHexStr(int dataCoding, String hexStr) {
        String realStr = null;
        try {
            if (hexStr != null) {
                if (dataCoding == 15) {
                    realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "GBK");
                } else if ((dataCoding & 0x0C) == 0x08) {
                    realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "UnicodeBigUnmarked");
                } else {
                    realStr = new String(Hex.decodeHex(hexStr.toCharArray()), "ISO8859-1");
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        
        return realStr;
    }

    /**
     * 发送http GET请求，并返回http响应字符串
     * 
     * @param urlstr 完整的请求url字符串
     * @return
     */
    public static String doGetRequest(String urlstr) {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);
        
        HttpEntity entity = null;
        String entityContent = null;
        try {
            HttpGet httpGet = new HttpGet(urlstr.toString());

            HttpResponse httpResponse = client.execute(httpGet);
            entityContent = EntityUtils.toString(httpResponse.getEntity());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (Exception e) {
                }
            }
        }
        return entityContent;
    }

    /**
     * 发送http POST请求，并返回http响应字符串
     * 
     * @param urlstr 完整的请求url字符串
     * @return
     */
    public static String doPostRequest(String urlstr, String dasm) {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);
        
        List<NameValuePair> ls = new ArrayList<NameValuePair>();
        NameValuePair param = new BasicNameValuePair("dasm", dasm.toString());
        ls.add(param);
        
        HttpEntity entity = null;
        String entityContent = null;
        try {
            HttpPost httpPost = new HttpPost(urlstr.toString());
            UrlEncodedFormEntity uefe = new UrlEncodedFormEntity(ls);
            httpPost.setEntity(uefe);

            HttpResponse httpResponse = client.execute(httpPost);
            entityContent = EntityUtils.toString(httpResponse.getEntity());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (Exception e) {
                }
            }
        }
        return entityContent;
    }
    
    /**
     * 将 短信下行 请求响应字符串解析到一个HashMap中
     * @param resStr
     * @return
     */
    public static HashMap<String,String> parseResStr(String resStr) {
        HashMap<String,String> pp = new HashMap<String,String>();
        try {
            String[] ps = resStr.split("&");
            for(int i=0;i<ps.length;i++){
                int ix = ps[i].indexOf("=");
                if(ix!=-1){
                   pp.put(ps[i].substring(0,ix),ps[i].substring(ix+1)); 
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return pp;
    }

}
