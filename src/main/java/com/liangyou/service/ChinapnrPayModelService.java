package com.liangyou.service;

/**
 * 汇付天下接口处理
 * 
 * @author 1432
 *
 */
public interface ChinapnrPayModelService {
	/**
	 * 
	 * 专门处理关于汇付放款和还款接口有时候 汇付处理失败，时时返回信息为失败，但是 汇付会不停处理，不停回调，所以要更改 调度任务表中此条信息的时时状态
	 * 
	 * @param ordNo
	 * @param respcode
	 * @param respdesc
	 */
	public void dealChinapnrBack(String ordNo, String respcode, String respdesc);

}
