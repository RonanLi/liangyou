package com.liangyou.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liangyou.dao.VoteAnswerDao;
import com.liangyou.dao.VoteDao;
import com.liangyou.dao.VoteOptionDao;
import com.liangyou.dao.VoteTitleDao;
import com.liangyou.domain.User;
import com.liangyou.domain.Vote;
import com.liangyou.domain.VoteAnswer;
import com.liangyou.domain.VoteOption;
import com.liangyou.domain.VoteTitle;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;
import com.liangyou.service.VoteService;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {
	// v1.8.0.4_u4 TGPROJECT-16 zf 2014-04-29 start
	@Autowired
	private VoteDao voteDao;
	@Autowired
	private VoteTitleDao titleDao;
	@Autowired
	private VoteOptionDao optionDao;
	@Autowired
	private VoteAnswerDao answerDao;
	@Override
	public PageDataList<VoteTitle> findVoteTitleList(SearchParam param) {
		
		return titleDao.findPageList(param);
	}
	@Override
	public void save(VoteTitle title) {
		
		titleDao.save(title);
	}
	
	@Override
	public void saveVote(VoteTitle title, String options) {

		title.setAddTime(new Date());
		titleDao.save(title);
		if(!"".equals(options)){
			String[] ops = options.split("；");
			for(String op : ops){
				VoteOption option = new VoteOption();
				option.setAddTime(new Date());
				option.setName(op.trim());
				option.setVoteTitle(title);
				option.setVote(title.getVote());
				optionDao.save(option);
			}
		}
	}
	@Override
	public List<VoteOption> findVoteOptionList(SearchParam param) {
		
		return optionDao.findByCriteria(param);
	}
	@Override
	public VoteOption findVoteOption(long id) {
		
		return optionDao.find(id);
	}
	@Override
	public VoteTitle findVoteTitle(long id) {
		return titleDao.find(id);
	}
	@Override
	public void updateVoteTitle(VoteTitle title) {
		titleDao.merge(title);
	}
	@Override
	public void updateVoteOption(VoteOption option) {
		optionDao.merge(option);
	}
	@Override
	public void deleteVoteOption(long id) {
		optionDao.delete(id);
	}
	@Override
	public void deleteVoteTitle(long id) {
		try{

			titleDao.delete(id);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void saveVoteOption(VoteTitle title,String options) {
		if(!"".equals(options)){
			String[] ops = options.split("；");
			for(String op : ops){
				VoteOption option = new VoteOption();
				option.setAddTime(new Date());
				option.setName(op);
				option.setVoteTitle(title);
				optionDao.save(option);
			}
		}
	}
	@Override
	public void updateVoteTitle(VoteTitle title, String options) {
		titleDao.merge(title);
		if(!"".equals(options)){
			String[] ops = options.split("；");
			for(String op : ops){
				VoteOption option = new VoteOption();
				option.setAddTime(new Date());
				option.setName(op.trim());
				option.setNum(0);
				option.setVoteTitle(title);
				optionDao.save(option);
			}
		}
	}
	@Override
	public void saveAnswer(VoteAnswer answer){
		answerDao.save(answer);
	}
	
	@Override
	public List<VoteTitle> findVoteTitleAll() {
		return titleDao.findAll();
	}
	@Override
	public void clearAnswer(User sessionUser,long id) {

		answerDao.clearByUserId(sessionUser,id);
	}
	
	@Override
	public PageDataList<VoteAnswer> findVoteAnswerList(SearchParam param) {
		return answerDao.findPageList(param);
	}
	@Override
	public PageDataList<Vote> findVoteList(SearchParam param) {
		
		return voteDao.findPageList(param);
	}
	@Override
	public void saveVote(Vote vote) {
		voteDao.save(vote);
	}
	@Override
	public void updateVote(Vote vote) {
		voteDao.merge(vote);
	}
	@Override
	public Vote findVote(long id) {
		
		return voteDao.find(id);
	}
	@Override
	public VoteAnswer findVoteAnswer(long id) {
		return answerDao.find(id);
	}
	@Override
	public List<VoteTitle> findAllVoteTitle(SearchParam param) {
		
		return titleDao.findByCriteria(param);
	}
	@Override
	public boolean findAnswer(User user, String id) {
		
		return answerDao.findAnswer(user,id);
	}
	@Override
	public List<Vote> findVoteByUserId(long id) {
		
		return voteDao.findVoteByUserId(id);
	}
	@Override
	public List<VoteAnswer> findVoteAnswerListByUserAndVote(SearchParam param) {
		
		return answerDao.findByCriteria(param);
	}
	@Override
	public List<VoteOption> findVoteOptionByUserId(long userId,long tid) {
		
		return optionDao.findVoteOptionByUserId(userId,tid);
	}
	@Override
	public List<VoteAnswer> findVoteAnswerListByUserAndVote(long userId, long id) {
		
		return answerDao.findVoteAnswerListByUserAndVote(userId, id);
	}
}// v1.8.0.4_u4 TGPROJECT-16 zf 2014-04-29 end