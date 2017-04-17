package com.liangyou.util;

import cn.emay.sdk.client.api.Client;

import com.liangyou.context.Global;

public class SingletonClient {
	private static Client client=null;
	private SingletonClient(){
	}
	public synchronized static Client getClient(String softwareSerialNo,String key){
		if(client==null){
			try {
				client=new Client(softwareSerialNo,key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	public synchronized static Client getClient(){
		if(client==null){
			try {
				String account=Global.getValue("sendsms_account");
				String authkey = "fDgbppTtWn";
				client=new Client(account,authkey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	public static void main(String str[]){
		SingletonClient.getClient();
	}
}
