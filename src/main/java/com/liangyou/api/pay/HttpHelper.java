package com.liangyou.api.pay;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.log4j.Logger;

public class HttpHelper {
	private static Logger logger = Logger.getLogger(HttpHelper.class);	
	private String url="";
	
	private NameValuePair[] params;
	
	private String charset="UTF-8";
	
	private HttpClient httpClient = new HttpClient();

	public HttpHelper(String url, String[][] params, String charset) {
		super();
		this.url = url;
		this.params = wrapParam(params);;
		this.charset = charset;
	}

	public HttpHelper(String url, String[][] params) {
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
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestBody(params);
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
		postMethod.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
		int statusCode = 0;
		String respString="";
		try {
			statusCode = httpClient.executeMethod(postMethod);
			if (statusCode == HttpStatus.SC_OK){
				respString = StringUtils.trim((new String(postMethod.getResponseBody(),charset)));
			}			
		} catch (IOException e) {
			logger.error(e);
		} finally {
			postMethod.releaseConnection();
		}
		return respString;
	}
	

}
