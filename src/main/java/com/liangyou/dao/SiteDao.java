package com.liangyou.dao;

import java.util.List;

import com.liangyou.domain.Site;

public interface SiteDao extends BaseDao<Site> {

    public Site getSiteByCode(String code);

    public List<Site> getSiteByCodes(String[] codes);

    public List getSiteList();

    public List getSubSiteList(int pid);

    public List getAllSubSiteList(int pid);
}
