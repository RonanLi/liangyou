package com.liangyou.api.moneymoremore;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;

public class MmmHttpHelper {
	
	private String url="";
	
	private NameValuePair[] params;
	
	private String charset="UTF-8";
	
	private HttpClient httpClient = new HttpClient();

	public MmmHttpHelper(String url, String[][] params, String charset) {
		super();
		this.url = url;
		this.params = wrapParam(params);;
		this.charset = charset;
	}

	public MmmHttpHelper(String url, String[][] params) {
		this(url,params,"UTF-8");
	}
	
	private NameValuePair[] wrapParam(String[][] params){
		NameValuePair[] data=new NameValuePair[params.length];
		for(int i=0;i<params.length;i++){
			data[i]=new NameValuePair(params[i][0],params[i][1]);
		}
		return data;
	}
	
	public String execute() throws HttpException, IOException{
		//创建POST方法的实例。在POST方法的构造函数中传入待连接的地址
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestBody(params);
		//使用系统提供的默认的恢复策略
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		int statusCode = 0;
		String respString="";
		try {
			//执行postMethod,生成状态码，该状态码能表示出该方法执行是否成功、需要认证或者页面发生了跳转等
			statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK){
				respString = StringUtils.trim((new String(postMethod.getResponseBody(),charset)));
			}			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();//释放连接，不管执行成功还是失败都要释放
			}
		return respString;
	}
	

}
