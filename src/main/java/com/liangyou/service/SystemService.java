package com.liangyou.service;

import java.util.List;

import com.liangyou.model.SystemInfo;

/**
 * 系统信息
 * 
 * @author 1432
 *
 */
public interface SystemService {

	public SystemInfo getSystemInfo();

	public List getSystemInfoForList();

	/**
	 * 根据模块显示系统设置信息
	 * 
	 * @return
	 */
	public List getSystemInfoForListBysytle(int i);

	/**
	 * @author lijie
	 * @param list
	 *            <SystemConfig>
	 */
	public void updateSystemInfo(List list);

	/**
	 * @author lijie
	 * @param url
	 *            url 为网站根目录路径
	 */
	public void clean(String url);

	public void updateSystem(long id);

	public void addDrawBankList(List list);

	/**
	 * 更新 系统config
	 */
	void updateSystemInfo();
}
