package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.Purview;
import com.liangyou.domain.PurviewModel;
import com.liangyou.domain.UserType;

/**
 * 权限服务
 * 
 * @author 1432
 *
 */
public interface AuthService {

	public List getPurviewByUserid(long user_id);

	public List getPurviewByPid(int pid);

	public Purview getPurview(long id);

	public List<Purview> getAllPurview();

	public void addPurview(Purview purview);

	public void delPurview(long id);

	public void modifyPurview(Purview purview);

	public void addUserTypePurviews(List purviewid, long user_type_id);

	public List<Purview> getAllCheckedPurview(long type_id);

	public List getAllUserType();

	public UserType getUserType(long type_id);

	public void addUserType(UserType userType);

	public void delUserType(long type_id);

	public void modifyUserType(UserType userType);

	/**
	 * 通过上级权限名获取下级权限
	 * 
	 * @param itemname
	 * @return
	 */

	public List<Purview> getPurviewByAndLevel(int level, long pid,
			long userId);
}
