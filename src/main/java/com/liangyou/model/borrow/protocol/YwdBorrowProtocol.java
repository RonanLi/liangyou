package com.liangyou.model.borrow.protocol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.BorrowCollection;
import com.liangyou.domain.BorrowTender;
import com.liangyou.domain.User;
import com.liangyou.tool.itext.PdfHelper;

public class YwdBorrowProtocol extends BorrowProtocol {
	public YwdBorrowProtocol(User user, long borrow_id, long tender_id,
			int borrowType, int templateType) {
		super(user, borrow_id, tender_id, borrowType, templateType);
	}

	public YwdBorrowProtocol(User user, long borrow_id, long tender_id) {
		super(user, borrow_id, tender_id);
	}

	@Override
	protected void addPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		List<BorrowCollection> list = new ArrayList(); 
		BorrowTender bt = null;
		bt = getBorrowService().getTenderById(getTender_id());
		list = bt.getBorrowCollections();	
		List cellList = null;
		List<String>[] args = new List[list.size() + 1];
		// 出借人(id)
		cellList = new ArrayList();
		cellList.add("出借人姓名");
		cellList.add("期数");
		cellList.add("还款本金");
		cellList.add("还款利息");
		cellList.add("年利率");
		cellList.add("借款开始日");
		cellList.add("截止还款日");
		cellList.add("还款本息");
		args[0] = cellList;
		for (int i = 1; i < list.size() + 1; i++) {
			BorrowCollection t = list.get(i - 1);
			cellList = new ArrayList();
			cellList.add(bt.getUser().getRealname()+"");
			cellList.add((t.getPeriod()+1)+"");
			cellList.add("￥" + t.getCapital()+"");
			cellList.add("￥"+ t.getInterest() + "");
			cellList.add(b.getApr() + "%");
			if(b.getVerifyTime()!=null) {
				//借款时间格式转换
				Date addTime = t.getAddtime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String startTime = sdf.format(addTime);
				cellList.add(startTime);
				Date  repayTime = t.getRepayTime();
				String endTimeStr =  sdf.format(repayTime);
				cellList.add(endTimeStr);
			}else{
				cellList.add("--");
				cellList.add("--");
			}
			cellList.add("￥"+t.getRepayAccount()+"");
			args[i] = cellList;
		}
		
		pdf.addTable(args,80, 7);
	}
}
