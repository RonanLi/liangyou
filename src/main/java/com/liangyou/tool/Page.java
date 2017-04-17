package com.liangyou.tool;

public class Page {
	private int total;
	private int currentPage;
	private int pages;
	private int pernum;
	private int start;
	private int end;
	public static int ROWS = 10;

	public Page() {
		super();
	}
	
	public Page(int total, int currentPage) {
		this(total,currentPage,ROWS);
	}

	public Page(int total, int currentPage, int pernum) {
		super();
		this.total = total;
		this.currentPage = currentPage == 0 ? 1 : currentPage;
		this.pernum = pernum;
		this.pages = (int) Math.ceil((total + 0.0) / pernum);
		count();
	}

	private void count() {
		this.start = pernum * (currentPage - 1);
		this.end = pernum * (currentPage);
		if (this.end > total) {
			this.end = total;
		}
	}

	/**
	 * 当前页是否有上一页
	 * @return
	 */
	public boolean hasPreview(){
		if(pages>1&&currentPage>1){
			return true;
		}
		return false;
	}
	/**
	 * 当前页是否有下一页
	 * @return
	 */
	public boolean hasNext(){
		if(pages>1&&currentPage<pages){
			return true;
		}
		return false;
	}
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getPernum() {
		return pernum;
	}

	public void setPernum(int pernum) {
		this.pernum = pernum;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}