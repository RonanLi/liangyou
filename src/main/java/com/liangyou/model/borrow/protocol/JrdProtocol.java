package com.liangyou.model.borrow.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.liangyou.domain.Borrow;
import com.liangyou.domain.Linkage;
import com.liangyou.domain.User;
import com.liangyou.tool.itext.PdfHelper;
import com.liangyou.util.DateUtils;
import com.liangyou.util.NumberUtils;
import com.liangyou.util.StringUtils;
import com.liangyou.util.UpNumber;

/**
 * 
 * @author lx
 *
 */
public class JrdProtocol extends BorrowProtocol {
	
	public JrdProtocol(User user, long borrow_id, long tender_id,
			int borrowType, int templateType){
		super(user, borrow_id, tender_id, 105, templateType);
	}
	
	public JrdProtocol(User user, long borrow_id, long tender_id){
		super(user, borrow_id, tender_id);
	}
	
	
	
	

	@Override
	protected Map<String, Object> fillProtoclData() {
		Map<String, Object> date = super.fillProtoclData();
		date.put("borrowagreementNO", getBorrow().getBorrowProperty().getBorrowagreementNO());
		date.put("borrowNo", getBorrow().getBorrowProperty().getBorrowNo());
		date.put("tenderNo", getTender().getTenderProperty().getTenderNo());
		date.put("warrantName", getBorrow().getBorrowProperty().getWarrant().getName());
		date.put("upBorrrowAccount", StringUtils.upNumb(getTender().getAccount()));
		date.put("tenderUserName",  getTender().getUser().getUsername());
		date.put("tenderCardId",  getTender().getUser().getCardId());
		date.put("tenderRealName",  getTender().getUser().getRealname());
		date.put("borrowUserName",  getBorrow().getUser().getUsername());
		date.put("borrowCardId",  getBorrow().getUser().getCardId());
		date.put("borrowRealName",  getBorrow().getUser().getRealname());
		date.put("tenderAccountUpnum", UpNumber.toChinese(getTender().getAccount()+""));
		date.put("tenderAccount", getTender().getAccount());
		date.put("verifyTime", DateUtils.newdateStr6(getBorrow().getVerifyTime()));
		date.put("timeLimit", getBorrow().getTimeLimit());
		date.put("apr", getBorrow().getApr());
		return date;
	}
	

	@Override
	protected void initBorrowType() {
		
		setBorrowType(105);
	}

	@Override
	protected void addPdfTable(PdfHelper pdf, Borrow b)
			throws DocumentException {
		Linkage linkage = getLinkageService().getLinkageById(NumberUtils.getInt(b.getUsetype()));
		List cellList = new ArrayList();
		List<String>[] args = new List[9];
		cellList.add("本次借款总额");
		cellList.add(b.getAccount()+"元人民币");
		args[0]=cellList;
		cellList.add("本次借款最低筹集金额");
		cellList.add(b.getAccount()+"元人民币");
		args[1] = cellList;
		cellList.add("借款用途");
		String useType = "未填写";
		if(null !=linkage){
			useType=linkage.getName();
		}
		cellList.add(useType);
		args[2] = cellList;
		cellList.add("借款期限");
		cellList.add(b.getTimeLimit()+"个月");
		args[3]=(cellList);
		cellList.add("借款期间");
		cellList.add(DateUtils.newdateStr6(DateUtils.rollDay(b.getVerifyTime(), 1))+"至"+DateUtils.newdateStr6(DateUtils.rollMon(b.getVerifyTime(), b.getTimeLimit())));
		args[4]=(cellList);
		cellList.add("起息日");
		cellList.add(DateUtils.newdateStr6(DateUtils.rollDay(b.getVerifyTime(), 1)));
		args[5]=cellList;
		cellList.add("借款利率");
		cellList.add(b.getApr()+"%/年");
		args[6] = cellList;
		cellList.add("还款方式");
		cellList.add("一次性还款、利随本清");
		args[7]=cellList;
		cellList.add("约定还款日");
		cellList.add("默认为借款结束日,如借款结束日为非工作日的，顺延至借款结束日后的第一个工作日");
		args[8] = cellList;
		
		
		float cellWidth=100;
		float[] widths = {cellWidth, 350};
		PdfPTable table = new PdfPTable(widths);// 建立一个pdf表格
		table.setSpacingBefore(10f);// 设置表格上面空白宽度
		//table.setSpacingBefore(10f);
		table.setTotalWidth(450);// 设置表格的宽度
		table.setLockedWidth(true);// 设置表格的宽度固定
		PdfPCell cell = null;
		try {
			for (int i = 0; i < 1; i++) {
				for (String s : args[i]) {
					cell = new PdfPCell(new Paragraph(s, pdf.getFont()));//new PdfPCell(new Paragraph(s, pdf.getFont()));// 建立一个单元格
					table.addCell(cell);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		table.addCell(cell);
		pdf.addTable(table);
	}

	
}
