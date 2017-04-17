package com.liangyou.model.borrow.protocol;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.itextpdf.text.DocumentException;
import com.liangyou.util.ReflectUtils;

/**
 * 未经授权不得进行修改、复制、出售及商业使用。
 * <b>Copyright (c)</b> 杭州融都科技有限公司-版权所有<br/>
 * @ClassName: ProtocolValue
 * @Description: 
 * @author fuxingxing somesky.cn@gmail.com
 * @date 2013-4-3 上午11:37:37
 */
public class ProtocolValue {
	private static Logger logger = Logger.getLogger(ProtocolValue.class);
	private Map<String,Object> data=new HashMap<String,Object>();
	
	private BorrowProtocol protocol;
	private final String NULL="NULL";
	
	public ProtocolValue() {
		super();
	}

	public Object printProtocol(String var){
		Object ret="";
		String[] tVars=var.split("\\.");
		if(tVars==null){
			return ret;
		}else if(tVars.length==1){
			ret=data.get(tVars[0]);
		}else if(tVars.length==2){
			ret=data.get(tVars[0]);
			if(ret==null){
				return NULL;
			}else if(tVars[0].equals("tenderTable")){
				try {
					protocol.addPdfTable(protocol.getPdf(), protocol.getBorrow());
				} catch (DocumentException e) {
					logger.error(e);
				}
			}else{
				ret=ReflectUtils.invokeGetMethod(ret.getClass(), ret, tVars[1]);
			}
		}
		return ret;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
}
