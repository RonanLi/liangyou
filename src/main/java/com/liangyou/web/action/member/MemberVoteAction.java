//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
package com.liangyou.web.action.member;

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

@Namespace("/member/vote")
@ParentPackage("p2p-default") 
@Controller
@Scope("prototype")
@InterceptorRefs(@InterceptorRef("mydefault"))
public class MemberVoteAction extends BaseAction{
	@Autowired 
	private VoteService voteService;
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String msg;
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMsg() {
		return msg;
	}
	//获取投票列表
	@Action(value="voteList",
			results={@Result(name="success",type="ftl",location="/member/vote/voteList.html")}
	)
	public String voteList(){
		try{
			SearchParam sp = SearchParam.getInstance().addPage(paramInt("page"));
			String title = paramString("title");
			if(title!=null&&title.trim()!=""){
				sp.addParam("title",Operator.LIKE,title.trim());
				request.setAttribute("title", title.trim());
			}
			sp.addParam("status","1");
			PageDataList<Vote> list = voteService.findVoteList(sp);
			if(list.getList().size()!=0){
				Vote vote = null;
				if(id==null){
					vote = list.getList().get(0);
				}else if(id!=null){
					vote = voteService.findVote(Long.parseLong(id));
				}
				if(vote!=null){
					SearchParam param = SearchParam.getInstance();
					param.addParam("vote", vote);
					request.setAttribute("vote", vote);
					param.addParam("status", 1);
					List<VoteTitle> titles = voteService.findAllVoteTitle(param);
					request.setAttribute("titles", titles);
					setPageAttribute(list, param);
					if(voteService.findAnswer(getSessionUser(),vote.getId()+"")){
						request.setAttribute("flag", "true");
					}else{
						request.setAttribute("flag", "false");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new BussinessException("系统异常");
		}
		return SUCCESS;
	}
	
	//提交投票
	@Action(value="submitVote",
			results={@Result(name="success",type="redirectAction",location="voteList.html",params={"id","%{id}"})}
			)
	public String submitVote(){
		
		try{
			SearchParam param = SearchParam.getInstance().addParam("vote.id", id);
			param.addParam("status", 1);
			List<VoteTitle> titles = voteService.findAllVoteTitle(param);
			User user = getSessionUser();
			for(int i=0;i<titles.size();i++){
				String[] oids = request.getParameterValues("option"+titles.get(i).getId());
				for(String oid : oids){
					VoteOption option = voteService.findVoteOption(Long.parseLong(oid));
					VoteAnswer answer = new VoteAnswer();
					answer.setAddTime(new Date());
					answer.setUser(user);
					answer.setVoteOption(option);
					answer.setVoteTitle(option.getVoteTitle());
					answer.setVote(option.getVote());
					voteService.saveAnswer(answer);
					option.setNum(option.getNum()+1);
					voteService.updateVoteOption(option);
				}
			}
			msg = "投票成功";
		}catch(Exception e){
			msg = "投票失败";
			e.printStackTrace();
			throw new BussinessException("投票失败");
		}
		return SUCCESS;
	}
	//查看自己已投票
	@Action(value="myVote",
			results={@Result(name="success",type="ftl",location="/member/vote/myVote.html")}
	)
	public String myVote(){
		try{
			User user = getSessionUser();
			List<Vote> voteList = voteService.findVoteByUserId(user.getUserId());
			request.setAttribute("voteList", voteList);
			Vote vote = null;
			if(id==null&&voteList.size()!=0){
				vote = voteList.get(0);
			}else if(id!=null){
				vote = voteService.findVote(Long.parseLong(id));
			}
			request.setAttribute("vote", vote);
			if(voteList.size()!=0){
				List<VoteAnswer> list = voteService.findVoteAnswerListByUserAndVote(user.getUserId(),vote.getId());
			
				for(VoteAnswer answer :list){
					answer.getVoteTitle().setVoteOption(voteService.findVoteOptionByUserId(user.getUserId(), answer.getVoteTitle().getId()));
				}
				request.setAttribute("list", list);
			}
		}catch(Exception e){
			
			throw new BussinessException("系统异常");
		}
		return SUCCESS;
	}	
}
//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end
