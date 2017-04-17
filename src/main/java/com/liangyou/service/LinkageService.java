package com.liangyou.service;

import org.springframework.stereotype.Service;

import com.liangyou.domain.Linkage;

/**
 * 数据字典服务
 * 
 * @author 1432
 *
 */
@Service
public interface LinkageService {

	public Linkage getLinkageById(int id);

}
