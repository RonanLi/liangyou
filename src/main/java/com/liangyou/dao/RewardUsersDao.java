package com.liangyou.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liangyou.domain.Prize;

public interface RewardUsersDao extends JpaRepository<Prize, Integer> {

	List<Prize> findByUseType(int useType);

}
