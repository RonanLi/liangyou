package com.liangyou.service;

import java.util.List;

import com.liangyou.domain.User;
import com.liangyou.domain.Vote;
import com.liangyou.domain.VoteAnswer;
import com.liangyou.domain.VoteOption;
import com.liangyou.domain.VoteTitle;
import com.liangyou.model.PageDataList;
import com.liangyou.model.SearchParam;

//v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 start
/**
 * 投票服务
 * 
 * @author 1432
 *
 */
public interface VoteService {
	public PageDataList<VoteTitle> findVoteTitleList(SearchParam param);

	public void saveVote(VoteTitle title, String options);

	void save(VoteTitle title);

	public List<VoteOption> findVoteOptionList(SearchParam param);

	public VoteOption findVoteOption(long id);

	public VoteTitle findVoteTitle(long id);

	public void updateVoteOption(VoteOption option);

	void updateVoteTitle(VoteTitle title);

	public void deleteVoteOption(long id);

	public void deleteVoteTitle(long id);

	void saveVoteOption(VoteTitle title, String options);

	public void updateVoteTitle(VoteTitle title, String options);

	public void saveAnswer(VoteAnswer answer);

	public List<VoteTitle> findVoteTitleAll();

	public PageDataList<VoteAnswer> findVoteAnswerList(SearchParam param);

	public PageDataList<Vote> findVoteList(SearchParam param);

	public void saveVote(Vote vote);

	public void updateVote(Vote vote);

	public Vote findVote(long id);

	public VoteAnswer findVoteAnswer(long id);

	void clearAnswer(User sessionUser, long id);

	public List<VoteTitle> findAllVoteTitle(SearchParam param);

	public boolean findAnswer(User user, String id);

	public List<Vote> findVoteByUserId(long id);

	public List<VoteAnswer> findVoteAnswerListByUserAndVote(SearchParam param);

	public List<VoteOption> findVoteOptionByUserId(long userId, long tid);

	public List<VoteAnswer> findVoteAnswerListByUserAndVote(long userId, long id);
}
// v1.8.0.4_u1 TGPROJECT-16 zf 2014-5-5 end