package com.liangyou.api.chinapnr;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liangyou.context.Global;
import com.liangyou.util.StringUtils;
  
public class FssPurchaseReconciliation extends ChinapnrModel {
	private static final Logger logger=Logger.getLogger(FssPurchaseReconciliation.class);
		private String beginDate;
		private String endDate;
		private String pageNum;
		private String pageSize;
		private String totalItems;
		private String fssReconciliation;
		
		
		public FssPurchaseReconciliation(String beginDate,String endDate,String pageNum,String pageSize){
			super();
			this.setBeginDate(beginDate);
			this.setEndDate(endDate);
			this.setPageNum(pageNum);
			this.setPageSize(pageSize);
			this.setCmdId("FssPurchaseReconciliation");
		}
		
		
		
		private String[] paramNames=new String[] {
				"version","cmdId","merCustId","beginDate","endDate","pageNum",
				"pageSize","reqExt","chkValue"
		};
		
		public StringBuffer getMerData() throws UnsupportedEncodingException{
			StringBuffer MerData = super.getMerData();
						 MerData
						 .append(getBeginDate())
						 .append(getEndDate())
						 .append(getPageNum())
						 .append(getPageSize())
						 .append(getReqExt());
			return MerData;
		}
		

		public String getBeginDate() {
			return beginDate;
		}

		public void setBeginDate(String beginDate) {
			this.beginDate = beginDate;
		}

		public String getEndDate() {
			return endDate;
		}

		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

		public String getPageNum() {
			return pageNum;
		}

		public void setPageNum(String pageNum) {
			this.pageNum = pageNum;
		}

		public String getPageSize() {
			return pageSize;
		}

		public void setPageSize(String pageSize) {
			this.pageSize = pageSize;
		}

		public String getTotalItems() {
			return totalItems;
		}

		public void setTotalItems(String totalItems) {
			this.totalItems = totalItems;
		}

		public String getFssReconciliation() {
			return fssReconciliation;
		}

		public void setFssReconciliation(String fssReconciliation) {
			this.fssReconciliation = fssReconciliation;
		}

		public String[] getParamNames() {
			return paramNames;
		}

		public void setParamNames(String[] paramNames) {
			this.paramNames = paramNames;
		}
		
		
		
}
