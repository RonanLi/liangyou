package com.liangyou.model.purview;

import com.liangyou.domain.Purview;

public class PurviewCheck{
	boolean checked;
	private Purview purview;
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Purview getPurview() {
		return purview;
	}

	public void setPurview(Purview purview) {
		this.purview = purview;
	}
	
	
}
