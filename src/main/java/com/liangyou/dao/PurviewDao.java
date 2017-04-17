package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Purview;
import com.liangyou.domain.PurviewModel;

public interface PurviewDao extends BaseDao<Purview> {
	/**
	 * 根据Pid获取权限列表
	 * @return
	 */
	public List<Purview> getPurviewByPid(int pid);
	
	/**
	 * 根据用户ID获取该用户的后台访问权限
	 * @param user_id
	 * @return
	 */
	public List getPurviewByUserid(long user_id);
	
	/**
	 * 获取所有的权限
	 * @return
	 */
	public List getAllPurview();
	/**
	 * 获取所有的权限,
	 * @return
	 */
	public List getAllCheckedPurview(long user_typeid);
	
	public void addPurview(Purview p);
	
	public Purview getPurview(long id);
	
	public void delPurview(long id);
	
	public boolean isRoleHasPurview(long id);
	
	public void modifyPurview(Purview p);
	
	public void addUserTypePurviews(List<Integer> purviewid,long user_type_id);
	
	public void delUserTypePurviews(long user_type_id) ;
	
	public List<Purview> getPurviewsByLevel(int level);
	
	/**通过权限名称,查询下级权限
	 * @param itemname
	 * @return
	 */

	public List<Purview> getPurviewByAndLevel(int level, long pid,
			long userId);
}
