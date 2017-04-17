package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.MsgOperate;
import com.liangyou.domain.MsgOperateType;
import com.liangyou.domain.MsgTemplate;
import com.liangyou.domain.MsgType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

/*****************
 * @ClassName: MsgService
 * @Description: TODO
 * @author xx_erongdu
 * @date 2013-7-17 下午2:11:35
 *
 *****************/
/**
 * 消息服务
 * 
 * @author 1432
 *
 */
public interface MsgService {
	/**
	 * 删除子节点
	 * 
	 * @param pid
	 * @return
	 */
	public MsgOperate getMsgOperate(int id);

	/**
	 * 根据code查询
	 * 
	 * @param code
	 * @return
	 */
	public MsgOperate getMsgOperateByCode(String code);

	/**
	 * 根据父id获取下一级节点
	 * 
	 * @param pid
	 * @return
	 */
	public List<MsgOperate> getMsgOperateChilds(int pid);

	/**
	 * 根据id删除单个节点
	 * 
	 * @param pid
	 * @return
	 */
	public void delMsgOperate(int id);

	/**
	 * 删除子节点
	 * 
	 * @param pid
	 * @return
	 */
	public void delMsgOperateChilds(List<MsgOperate> list);

	/**
	 * 增加子节点
	 * 
	 * @param pid
	 * @return
	 */
	public MsgOperate addOrModifyMsgOperate(MsgOperate mo);

	/**
	 * 按条件获取模板
	 * 
	 * @param pid
	 * @return
	 */
	public PageDataList getTemplateList(SearchParam param);

	/**
	 * 新增模板
	 * 
	 * @return
	 */
	public void addTemplate(MsgTemplate mt);

	/**
	 * 获取模板信息
	 * 
	 * @return
	 */
	public MsgTemplate getTemplate(int id);

	/**
	 * 获取模板信息
	 * 
	 * @return
	 */
	public MsgTemplate modifyTemplate(MsgTemplate mt);

	/**
	 * 删除模板
	 * 
	 * @return
	 */
	public void delTemplate(int id);

	/**
	 * 查询所有信息类型
	 * 
	 * @return
	 */
	public PageDataList getTypeList(SearchParam param);

	/**
	 * 增加类型
	 * 
	 * @return
	 */
	public void addType(MsgType msgType);

	/**
	 * 修改类型
	 * 
	 * @return
	 */
	public void modifyType(MsgType msgType);

	/**
	 * 删除类型
	 * 
	 * @return
	 */
	public void delType(int id);

	/**
	 * 根据id获取类型
	 * 
	 * @return
	 */
	public MsgType getType(int id);

	/**
	 * 根据code获取类型
	 * 
	 * @return
	 */
	public MsgType getTypeByCode(String code);

	/**
	 * 获取所有类型
	 * 
	 * @return List
	 */
	public List<MsgType> getAllType();

	/**
	 * 批量保存业务与信息类型关联对象
	 * 
	 * @return List
	 */
	public void addMsgOperateTypeList(List<MsgOperateType> list);

	/**
	 * 查询业务关联模板集合
	 * 
	 * @return List
	 */
	public PageDataList getMsgOperateTypeList(SearchParam param);

	/**
	 * 查询业务关联模板集合
	 * 
	 * @return List
	 */
	public List<MsgOperateType> getMsgOperateTypeList(int operate_id);

	/**
	 * 查询绑定模板
	 * 
	 * @return List
	 */
	public MsgOperateType getMsgOperateType(int id);

	/**
	 * 修改关联
	 * 
	 * @return List
	 */
	public MsgOperateType modifyMsgOperateType(MsgOperateType mot);

	/**
	 * 删除关联
	 * 
	 * @return List
	 */
	public void delMsgOperateTypeList(List<MsgOperateType> list);

	/**
	 * operate code唯一性验证
	 * 
	 * @return List
	 */
	public boolean checkCodeExist(String code);

	/**
	 * 根据业务Id获取 MsgOperateType
	 * 
	 * @param operateId
	 * @return MsgOperateType
	 */
	public List<MsgOperateType> listMsgOperateTypeByOperateId(int operateId);

	/**
	 * 测试添加使用
	 */
	public void reloadMsgTemplate();

}
