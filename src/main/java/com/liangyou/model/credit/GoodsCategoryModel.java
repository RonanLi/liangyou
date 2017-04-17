package com.liangyou.model.credit;

import java.io.Serializable;
import java.util.List;

import com.liangyou.domain.GoodsCategory;

public class GoodsCategoryModel implements Serializable {
	
	private GoodsCategory model;
	
	private String parentName;
	
	private List<GoodsCategory> childList;
	
	public GoodsCategoryModel(GoodsCategory model){
		this.model = model;
		this.parentName = model.getName();
	}
	
	public List<GoodsCategory> getChildList() {
		return childList;
	}

	public void setChildList(List<GoodsCategory> childList) {
		this.childList = childList;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
}
