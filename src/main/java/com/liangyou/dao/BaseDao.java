package com.liangyou.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

public interface BaseDao<T> {
	public T save(T entity);
	/**
	 * 保存对象集合
	 */
	public void save(Collection<T> ts);
	/**
	 * 
	 * @param entity
	 * @return
	 */
	public T merge(T entity);
	/**
	 * 更新对象
	 * @param entity
	 */
	public void update(T entity);
	
	/**
	 * 更新对象
	 * @param entity
	 */
	public void updateWithRefresh(T entity);
	/**
	 * 更新对象集合
	 */
	public boolean update(Collection<T> ts);
	/**
	 * 悲观锁
	 * @param entity
	 */
	public void lock(T entity);
	/**
	 * 删除
	 * @param entityids
	 */
	public void delete(Serializable[] entityids) ;
	/**
	 * 删除
	 * @param entityids
	 */
	public void delete(Serializable entityids) ;
	
	public void delete(Collection<T> c) ;

	public void clear();
	
	public void detach(Collection<T> ts);

	public void detach(Serializable entityid) ;

	public void flush() ;
	
	/**
	 * 获取实体类
	 */
    public T find(Class<T> entityClass, Object id) ;
    public T find(Serializable entityId);
    public T findWithLock(Serializable entityId);
	/**
	 * 获取实体类集合
	 */
    public List<T> findAll() ;
    
    public List<T> findByCriteria(SearchParam param);
    
    public T findByCriteriaForUnique(SearchParam param);
    
    public List<T> findByCriteria(SearchParam param,int start,int limit);
    
    public int countByCriteria(SearchParam param);
    /**
     * 根据属性查找对象列表
     * @param value
     * @return
     */
    public List<T> findByProperty(String property,Object value);
    /**
     * 根据属性查找对象
     * @param value
     * @return
     */
    public T findByPropertyForUnique(String property,Object value);
    
    public PageDataList<T> findPageList(SearchParam param);
    
    public List<T> findAllListBySql(String datasql,SearchParam param,Map<String,Object> paramMap,Class clazz);
    
    public PageDataList<T> findPageListBySql(String datasql,String countsql,SearchParam param,Map<String,Object> paramMap,Class clazz);
    
    
	/**
	 * 查询的当前条件下所有的列
	 * @param param
	 * @return
	 */
    public PageDataList<T> findAllPageList(SearchParam param);
    
    /**
     * 获取NamedParameterJdbcTemplate，纯sql 查询。
     */
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate();
	/**
	 * 返回BeanPropertyRowMapper封装，防止数据库字段为空抛出异常，默认设置null
	 * @param clazz
	 * @return
	 */
	public RowMapper getBeanMapper(Class clazz);
    
    
}
