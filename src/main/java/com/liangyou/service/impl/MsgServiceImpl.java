package com.liangyou.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.MsgOperateDao;
import com.liangyou.dao.MsgOperateTypeDao;
import com.liangyou.dao.MsgTemplateDao;
import com.liangyou.dao.MsgTypeDao;
import com.liangyou.domain.MsgOperate;
import com.liangyou.domain.MsgOperateType;
import com.liangyou.domain.MsgTemplate;
import com.liangyou.domain.MsgType;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.MsgService;

@Service(value="msgService")
@Transactional
public class MsgServiceImpl extends BaseServiceImpl implements MsgService {
	
	private static Logger logger = Logger.getLogger(MsgServiceImpl.class);

	@Autowired
	private MsgOperateDao msgOperateDao;
	@Autowired
	private MsgTemplateDao msgTemplateDao;
	@Autowired
	private MsgTypeDao msgTypeDao;
	@Autowired
	private MsgOperateTypeDao msgOperateTypeDao;
	/**
	 * 根据父id获取下一级节点
	 * @param pid
	 * @return
	 */
	@Override
	public List<MsgOperate> getMsgOperateChilds(int pid) {
		return msgOperateDao.getChilds(pid);
	}

	/**
	 * 根据id获取删除单个节点
	 * @param pid
	 * @return
	 */
	@Override
	public void delMsgOperate(int id) {
		msgOperateDao.delete(id);
	}

	/**
	 * 删除子节点
	 * @param pid
	 * @return
	 */
	@Override
	public void delMsgOperateChilds(List<MsgOperate> list) {
		msgOperateDao.delete(list);
	}

	/**
	 *查询节点
	 * @param pid
	 * @return
	 */
	@Override
	public MsgOperate getMsgOperate(int id) {
		return msgOperateDao.find(id);
	}
	
	/**
	 *查询节点
	 * @param pid
	 * @return
	 */
	@Override
	public MsgOperate getMsgOperateByCode(String code) {
		return msgOperateDao.findByProperty("code", code).get(0);
	}
	
	/**
	 * 增加子节点
	 * @param pid
	 * @return
	 */
	@Override
	public MsgOperate addOrModifyMsgOperate(MsgOperate mo) {		
		return msgOperateDao.merge(mo);
	}

	/**
	 * 按条件获取模板
	 * @param pid
	 * @return
	 */
	@Override
	public PageDataList getTemplateList(SearchParam param) {
		return msgTemplateDao.findPageList(param);
	}

	/**
	 * 新增模板
	 * @return
	 */
	@Override
	public void addTemplate(MsgTemplate mt) {
		msgTemplateDao.save(mt);
	}

	
	/**
	 * 获取模板信息	 
	 * @return
	 */
	@Override
	public MsgTemplate getTemplate(int id) {
		return msgTemplateDao.find(id);
	}

	/**
	 * 删除模板信息	 
	 * @return
	 */
	@Override
	public void delTemplate(int id) {
		msgTemplateDao.delete(id);
	}

	/**
	 * 获取模板信息	 
	 * @return
	 */
	@Override
	public MsgTemplate modifyTemplate(MsgTemplate mt) {
		return msgTemplateDao.merge(mt);
	}

	/**
	 * 查询所有信息类型	 
	 * @return
	 */
	@Override
	public PageDataList getTypeList(SearchParam param) {
		return msgTypeDao.findPageList(param);
	}

	/**
	 * 查询所有信息类型	 
	 * @return
	 */
	@Override
	public void addType(MsgType msgType) {
		msgTypeDao.save(msgType);
	}

	/**
	 * 修改信息类型	 
	 * @return
	 */
	@Override
	public void modifyType(MsgType msgType) {
		msgTypeDao.update(msgType);
	}

	/**
	 *s删除信息类型	 
	 * @return
	 */
	@Override
	public void delType(int id) {
		msgTypeDao.delete(id);
	}

	/**
	 *根据id获取类型	 
	 * @return
	 */
	@Override
	public MsgType getType(int id) {
		return msgTypeDao.find(id);
	}

	@Override
	public MsgType getTypeByCode(String code) {
		SearchParam param = SearchParam.getInstance();
		param.addParam("code", code);
		List<MsgType> list = msgTypeDao.findByCriteria(param);
		if(list.size() > 0){
			return (MsgType)list.get(0);
		}else{
			return null;
		}
	}

	@Override
	public List<MsgType> getAllType() {
		return msgTypeDao.findAll();
	}

	@Override
	public void addMsgOperateTypeList(List<MsgOperateType> list) {
		msgOperateTypeDao.save(list);
	}

	@Override
	public PageDataList getMsgOperateTypeList(SearchParam param) {
		return msgOperateTypeDao.findPageList(param);
	}

	@Override
	public MsgOperateType getMsgOperateType(int id) {
		return msgOperateTypeDao.find(id);
	}

	@Override
	public MsgOperateType modifyMsgOperateType(MsgOperateType mot) {
		return msgOperateTypeDao.merge(mot);
	}

	@Override
	public List<MsgOperateType> getMsgOperateTypeList(int operate_id){
		SearchParam param = SearchParam.getInstance();
		param.addParam("msgOperate.id", operate_id);
		return msgOperateTypeDao.findByCriteria(param);
	}

	@Override
	public void delMsgOperateTypeList(List<MsgOperateType> list) {
		msgOperateTypeDao.delete(list);
	}

	@Override
	public boolean checkCodeExist(String code) {
		List<MsgOperate> list =  msgOperateDao.findByProperty("code", code);
		if(list!=null && list.size()>=1){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public List<MsgOperateType> listMsgOperateTypeByOperateId(int operateId) {
		return this.msgOperateTypeDao.findByProperty("msgOperate.id", operateId);
	}
	
	@Override
	public void reloadMsgTemplate(){
	 List<MsgOperate> list = 	msgOperateDao.findAll();
	 for (MsgOperate ms : list) {
		 int i = 1;
		 for  (;i < 4; i++) {
			 MsgTemplate mst = new MsgTemplate();
			 String name = "";
			 if(i==1){
				 name = ms.getName()+"-站内信";
				 mst.setMsgType(new MsgType(1));
			 }
			 if(i==2){
				 name = ms.getName()+"-邮件";
				 mst.setMsgType(new MsgType(2));
			 }
			 if(i==3){
				 name = ms.getName()+"-短信";
				 mst.setMsgType(new MsgType(3));
			 }
			mst.setContent(null);
			mst.setIsuse(1);
			mst.setName(name);
			mst.setRemark(name);
			
			msgTemplateDao.save(mst);
		}
		 
	}
		
		
		
	}
}
