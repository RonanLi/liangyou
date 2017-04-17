package com.liangyou.web.action.admin;

import java.util.Date;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.liangyou.context.Constant;
import com.liangyou.domain.User;
import com.liangyou.domain.Vote;
import com.liangyou.domain.VoteAnswer;
import com.liangyou.domain.VoteOption;
import com.liangyou.domain.VoteTitle;
import com.liangyou.exception.BussinessException;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.model.SearchFilter.Operator;
import com.liangyou.service.VoteService;
import com.liangyou.web.action.BaseAction;

//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
@Namespace("/admin/vote")
@ParentPackage("p2p-default") 
@Controller
@Scope("prototype")
@InterceptorRefs(@InterceptorRef("manageAuthStack"))
public class AdminVoteAction extends BaseAction{
	@Autowired 
	private VoteService voteService;
	private String msg;
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private long[] ids;
	private String[] names;
	public long[] getIds() {
		return ids;
	}
	public void setIds(long[] ids) {
		this.ids = ids;
	}
	public String[] getNames() {
		return names;
	}
	public void setNames(String[] names) {
		this.names = names;
	}
	//获取投票列表
	@Action(value="getVoteList",
			results={@Result(name="success",type="ftl",location="/admin/vote/getVoteList.html")}
	)
	public String getVoteList(){
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		param.addParam("status",Operator.NOTEQ,2);
		String title = paramString("title");
		if(title!=null&&title.trim()!=""){
			param.addParam("title",Operator.LIKE,title.trim());
			request.setAttribute("title", title.trim());
		}
		PageDataList<Vote> list = voteService.findVoteList(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	//添加投票页面
	@Action(value="getVoteTitleList",
			results={@Result(name="success",type="ftl",location="/admin/vote/getVoteTitleList.html")}
			)
	public String getVoteTitleList(){
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		Vote vote = voteService.findVote(paramLong("id"));
		request.setAttribute("vote", vote);
		param.addParam("vote", vote);
		param.addParam("status",1);
		String title = paramString("title");
		if(title!=null&&title.trim()!=""){
			param.addParam("title",Operator.LIKE,title.trim());
			request.setAttribute("title", title.trim());
		}
		PageDataList<VoteTitle> list = voteService.findVoteTitleList(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	//查看投票详情
	@Action(value="getVoteOptionList",
			results={@Result(name="success",type="ftl",location="/admin/vote/getVoteOptionList.html")}
			)
	public String getVoteOptionList(){
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		VoteTitle title = voteService.findVoteTitle(paramLong("oid"));
		request.setAttribute("title", title);
		param.addParam("voteTitle", title);
		List<VoteOption> list = voteService.findVoteOptionList(param);
		request.setAttribute("list", list);
		return SUCCESS;
	}
	//修改投票
	@Action(value="updateVote",
			results={@Result(name="success",type="redirectAction",location="getVoteTitleList.html",params={"id","%{id}","msg","%{msg}"})}
			)
	public String updateVote(){
		try{
			if(ids!=null){
				for(int i=0;i<ids.length;i++){
					VoteOption option = voteService.findVoteOption(ids[i]);
					option.setName(names[i]);
					option.setUpdateTime(new Date());
					voteService.updateVoteOption(option);
				}
			}
			VoteTitle title = voteService.findVoteTitle(paramLong("oid"));
			title.setTitle(paramString("title"));
			title.setType(paramString("type"));
			title.setMin(paramInt("min"));
			title.setMax(paramInt("max"));
			title.setUser((User)session.get(Constant.AUTH_USER));
			title.setUpdateTime(new Date());
			String options = paramString("options");
			voteService.updateVoteTitle(title,options);
			msg = "修改投票成功";
		}catch(Exception e){
			msg = "修改投票失败";
			throw new BussinessException("修改投票失败");
		}
		return SUCCESS;
	}
	//修改投票信息页面
	@Action(value="updateVoteUI",
			results={@Result(name="success",type="ftl",location="/admin/vote/updateVoteUI.html")}
			)
	public String updateVoteUI(){
		Vote vote = voteService.findVote(paramLong("id"));
		request.setAttribute("vote", vote);
		return SUCCESS;
	}
	//修改投票信息
	@Action(value="updateVoteInfo",
			results={@Result(name="success",type="redirectAction",location="getVoteList.html",params={"id","%{id}","msg","%{msg}"})}
			)
	public String updateVoteInfo(){
		try{
			Vote vote = voteService.findVote(paramLong("id"));
			vote.setUpdateTime(new Date());
			vote.setDescription(paramString("description"));
			vote.setTitle(paramString("title"));
			vote.setStatus(paramInt("status"));
			voteService.updateVote(vote);
			msg = "修改投票成功";
		}catch(Exception e){
			msg = "修改投票失败";
			throw new BussinessException("修改投票失败");
		}
		return SUCCESS;
	}
	//删除投票
	@Action(value="deleteVote",
			results={@Result(name="success",type="redirectAction",location="getVoteList.html",params={"msg","%{msg}"})}
			)
	public String deleteVote(){
		try{
			Vote vote = voteService.findVote(Long.parseLong(id));
			vote.setStatus(2);
			voteService.updateVote(vote);;
			msg = "删除投票成功";
		}catch(Exception e){
			msg = "删除投票失败";
			throw new BussinessException("删除投票失败");
		}
		return SUCCESS;
	}
	//删除投票小标题
	@Action(value="deleteVoteTitle",
			results={@Result(name="success",type="redirectAction",location="getVoteTitleList.html",params={"id","%{id}","msg","%{msg}"})}
			)
	public String deleteVoteTitle(){
		try{
			VoteTitle title = voteService.findVoteTitle(paramLong("tid"));
			title.setStatus(0);
			voteService.updateVoteTitle(title);;
			msg = "删除投票小标题成功";
		}catch(Exception e){
			msg = "删除投票小标题失败";
			throw new BussinessException("删除投票小标题失败");
		}
		return SUCCESS;
	}
	//删除投票选项
	@Action(value="deleteVoteOption",
			results={@Result(name="success",type="redirectAction",location="getVoteOptionList.html",params={"id","%{id}","msg","%{msg}"})}
			)
	public String deleteVoteOption(){
		try{
			voteService.deleteVoteOption(paramLong("oid"));
			msg = "删除选项成功";
		}catch(Exception e){
			msg = "删除选项失败";
			throw new BussinessException("删除选项失败");
			
		}
		return SUCCESS;
	}
	//添加投票页面
	@Action(value="addVoteUI",
			results={@Result(name="success",type="ftl",location="/admin/vote/addVoteUI.html")}
			)
	public String addVoteUI(){
		
		return SUCCESS;
	}
	//添加投票内容页面
	@Action(value="addVoteTitleUI",
			results={@Result(name="success",type="ftl",location="/admin/vote/addVoteTitleUI.html")}
			)
	public String addVoteTitleUI(){
		Vote vote = voteService.findVote(paramLong("id"));
		request.setAttribute("vote", vote);
		return SUCCESS;
	}
	//添加投票标题
	@Action(value="addVote",
			results={@Result(name="success",type="redirect",location="/admin/vote/getVoteList.html",params={"msg","%{msg}"})}
			)
	public String addVote(){
		try{
			Vote vote = new Vote();
			vote.setTitle(paramString("title"));
			vote.setDescription(paramString("description"));
			vote.setStatus(paramInt("status"));
			vote.setUser((User)session.get(Constant.AUTH_USER));
			vote.setAddTime(new Date());
			voteService.saveVote(vote);
			msg = "添加投票标题成功";
		}catch(Exception e){
			msg = "添加投票标题失败";
			throw new BussinessException("添加投票标题失败");
		}
		return SUCCESS;
	}
	//添加投票内容
	@Action(value="addVoteTitle",
			results={@Result(name="success",type="redirect",location="/admin/vote/getVoteTitleList.html",params={"id","%{id}","msg","%{msg}"})}
			)
	public String addVoteTitle(){
		try{
			VoteTitle title = new VoteTitle();
			title.setTitle(paramString("title"));
			title.setType(paramString("type"));
			title.setMin(paramInt("min"));
			title.setMax(paramInt("max"));
			title.setStatus(1);
			title.setVote(voteService.findVote(paramLong("id")));
			title.setUser((User)session.get(Constant.AUTH_USER));
			String options = paramString("options");
			voteService.saveVote(title, options);
			msg = "添加投票内容成功";
		}catch(Exception e){
			msg = "添加投票内容失败";
			throw new BussinessException("添加投票内容失败");
		}
		return SUCCESS;
	}
	//统计投票
	@Action(value="countVote",
			results={@Result(name="success",type="ftl",location="/admin/vote/countVote.html")}
			)
	public String countVote(){
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		param.addParam("vote.id",id);
		String title = paramString("title");
		if(title!=null&&title.trim()!=""){
			param.addParam("title",Operator.LIKE,title.trim());
			request.setAttribute("title", title.trim());
		}
		param.addParam("status", 1);
		PageDataList<VoteTitle> list = voteService.findVoteTitleList(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	//查看投票用户
	@Action(value="countVoteUser",
			results={@Result(name="success",type="ftl",location="/admin/vote/countVoteUser.html")}
			)
	public String countVoteUser(){
		
		SearchParam param = SearchParam.getInstance().addPage(paramInt("page"));
		param.addParam("voteOption.id",id);
		PageDataList<VoteAnswer> list = voteService.findVoteAnswerList(param);
		setPageAttribute(list, param);
		return SUCCESS;
	}
	
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end
